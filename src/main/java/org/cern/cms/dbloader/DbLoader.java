package org.cern.cms.dbloader;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.List;

import javax.management.modelmbean.XMLParseException;

import org.apache.log4j.Logger;
import org.cern.cms.dbloader.app.CondApp;
import org.cern.cms.dbloader.app.PartApp;
import org.cern.cms.dbloader.dao.AuditDao;
import org.cern.cms.dbloader.dao.DatasetDao;
import org.cern.cms.dbloader.manager.CondHbmManager;
import org.cern.cms.dbloader.manager.CondManager;
import org.cern.cms.dbloader.manager.CondXmlManager;
import org.cern.cms.dbloader.manager.EntityModificationManager;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.manager.HelpPrinter;
import org.cern.cms.dbloader.manager.CLIPropertiesManager;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.cern.cms.dbloader.manager.XmlManager;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.model.OptId;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.managemnt.UploadStatus;
import org.cern.cms.dbloader.model.xml.Root;
import org.cern.cms.dbloader.util.PropertiesException;
import org.hibernate.Session;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j;

@Log4j
@RequiredArgsConstructor
public class DbLoader {

    private final PropertiesManager props;

    private void run() throws Exception {

        Logger.getRootLogger().setLevel(props.getLogLevel());

        /**
         * Modify individual classes. This routine must be run before the class
         * loader tries to access the to-be-modified classes!
         */
        EntityModificationManager.modify(props);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {

                bind(PropertiesManager.class).toInstance(props);
                install(new FactoryModuleBuilder().build(ResourceFactory.class));

            }
        });

        ResourceFactory rf = injector.getInstance(ResourceFactory.class);

        if (props.isSchema()) {
            XmlManager xmlm = injector.getInstance(XmlManager.class);
            xmlm.generateSchema();
            return;
        }

        CondApp condApp = injector.getInstance(CondApp.class);
        if (condApp.handleInfo()) {
            return;
        }

        PartApp partApp = injector.getInstance(PartApp.class);
        if (partApp.handleInfo()) {
            return;
        }

        if (props.isCondDatasets()) {
            OptId optId = props.getCondDatasets();
            CondManager cm = injector.getInstance(CondManager.class);
            CondEntityHandler ch = cm.getConditionHandler(optId);
            try (HbmManager hbm = injector.getInstance(CondHbmManager.class)) {
                DatasetDao dao = rf.createDatasetDao(hbm);
                HelpPrinter.outputDatasetList(System.out, dao.getCondDatasets(ch));
            }
            return;
        }

        if (props.isCondDataset()) {
            BigInteger dataSetId = props.getCondDataset();
            CondManager cm = injector.getInstance(CondManager.class);

            try (HbmManager hbm = injector.getInstance(CondHbmManager.class)) {
                DatasetDao dao = rf.createDatasetDao(hbm);
                Session session = hbm.getSession();
                try {
                    Dataset dataset = dao.getDataset(session, dataSetId);
                    BigInteger id = dataset.getKindOfCondition().getId();
                    CondEntityHandler ceh = cm.getConditionHandler(id);
                    if (ceh == null) {
                        throw new IllegalArgumentException(String.format("[%s] dataset not found!", dataSetId));
                    }

                    List<? extends CondBase> dataSetData = dao.getDatasetData(session, ceh, dataset);
                    if (dataSetData.isEmpty()) {
                        throw new IllegalArgumentException(String.format("[%s] dataset data not found!", dataSetId));
                    }

                    CondXmlManager xmlm = new CondXmlManager(ceh);
                    xmlm.printDatasetDataXML(dataset, dataSetData);
                } finally {
                    session.close();
                }
            }

            return;
        }

        /**
         * Processing files from here!
         */
        if (props.getArgs().isEmpty()) {
            throw new IllegalArgumentException("No input files provided");
        }

        try (HbmManager hbm = injector.getInstance(CondHbmManager.class)) {

            AuditDao auditDao = rf.createAuditDao(hbm);
            for (DataFile df : FilesManager.getFiles(props)) {

                AuditLog alog = new AuditLog();
                try {

                    alog.setArchiveFileName(df.getArchive().getName());
                    alog.setDataFileName(df.getData().getName());
                    alog.setDataFileChecksum(df.getMd5());
                    auditDao.saveAuditRecord(alog);

                    log.info(String.format("Processing %s", df));
                    XmlManager xmlm = injector.getInstance(XmlManager.class);
                    Root root = xmlm.unmarshal(df.getData());

                    if (log.isDebugEnabled()) {
                        log.debug(root);
                    }

                    boolean loaded = condApp.handleData(df, hbm, root, alog);

                    if (!loaded) {
                        loaded = partApp.handleData(df, hbm, root, alog);
                    }

                    if (loaded) {
                        alog.setStatus(UploadStatus.Success);
                    } else {
                        throw new XMLParseException("XML file not recognized by handlers");
                    }

                } catch (Exception ex) {

                    StringWriter sw = new StringWriter();
                    ex.printStackTrace(new PrintWriter(sw));
                    alog.setUploadLogTrace(sw.toString());
                    alog.setStatus(UploadStatus.Failure);

                    log.error(ex.getMessage(), ex);

                }

                auditDao.saveAuditRecord(alog);

            }

        }

    }

    public static int main(String[] args) {

        // Install SLF4J logger to direct all java.util.logging messages to log4j
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        try {

            final PropertiesManager props = new CLIPropertiesManager(args);

            if (props.printVersion()) {
                return 0;
            }

            if (props.printHelp()) {
                return 0;
            }

            DbLoader loader = new DbLoader(props);
            loader.run();

            return 0;
            
        } catch (PropertiesException ex) {

            System.err.println("ERROR: " + ex.getMessage());
            return 1;
            
        } catch (Exception ex) {
            
            ex.printStackTrace(System.err);
            return 2;
            
        }

    }

}
