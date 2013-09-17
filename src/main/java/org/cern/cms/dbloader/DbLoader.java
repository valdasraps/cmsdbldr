package org.cern.cms.dbloader;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.management.modelmbean.XMLParseException;

import lombok.extern.log4j.Log4j;

import org.apache.log4j.Logger;
import org.cern.cms.dbloader.app.CondApp;
import org.cern.cms.dbloader.dao.AuditDao;
import org.cern.cms.dbloader.manager.CondHbmManager;
import org.cern.cms.dbloader.manager.EntityModificationManager;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.FilesManager.DataFile;
import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.cern.cms.dbloader.manager.XmlManager;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.managemnt.UploadStatus;
import org.cern.cms.dbloader.model.xml.Root;
import org.cern.cms.dbloader.util.PropertiesException;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;

@Log4j
public class DbLoader {
	
	public static void main(String[] args) {
		
        // Install SLF4J logger to direct all java.util.logging messages to log4j
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
		
		try {

			final PropertiesManager props = new PropertiesManager(args);

			if (props.printVersion()) {
				return;
			}
			
			if (props.printHelp()) {
				return;
			}
			
			Logger.getRootLogger().setLevel(props.getLogLevel());

			/**
			 * Modify individual classes. This routine must be run before 
			 * the class loader tries to access the to-be-modified classes!
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
			
			if (props.getArgs().isEmpty()) {
				throw new IllegalArgumentException("No input files provided");
			}
			
			try (HbmManager hbm = injector.getInstance(CondHbmManager.class)) {
				
				AuditDao auditDao = rf.createAuditDao(hbm);
				for (DataFile df: FilesManager.getFiles(props)) {
					
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

						boolean loaded = condApp.handleData(df.getData(), hbm, root, alog);

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
			
		} catch (PropertiesException ex) {
			System.err.println("ERROR: " + ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}

	}
		
}
