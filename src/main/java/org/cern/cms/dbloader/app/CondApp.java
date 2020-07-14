package org.cern.cms.dbloader.app;

import javax.management.modelmbean.XMLParseException;

import lombok.extern.log4j.Log4j;

import org.cern.cms.dbloader.manager.DynamicEntityGenerator;
import org.cern.cms.dbloader.manager.HelpPrinter;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.EntityHandler;
import org.cern.cms.dbloader.model.OptId;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.serial.Header;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.model.managemnt.AuditLog;

import org.cern.cms.dbloader.manager.XmlManager;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.DatasetRoot;
import org.cern.cms.dbloader.util.NotAuthorizedException;
import org.cern.cms.dbloader.util.OperatorAuth;

@Log4j
@Singleton
public class CondApp extends AppBase {

    @Inject
    private DynamicEntityGenerator enGenerator;

    @Inject
    private XmlManager xmlm;

    @Override
    public void checkPermission(OperatorAuth auth) throws NotAuthorizedException {
        if (!auth.isConditionPermission()) {
            throw new NotAuthorizedException(PropertiesManager.UserOption.OPERATOR_CONDITION_PERMISSION.name());
        }
    }
    
    @Override
    public boolean handleInfo() throws Exception {

        if (props.isConditionsList()) {
            HelpPrinter.outputConditionList(System.out, enGenerator);
            return true;
        }

        if (props.isChannelsList()) {
            HelpPrinter.outputChannelList(System.out, enGenerator);
            return true;
        }

        if (props.isConditionDesc()) {
            OptId optId = props.getConditionDesc();
            EntityHandler<CondBase> tm = enGenerator.getConditionHandler(optId);
            if (tm == null) {
                throw new IllegalArgumentException(String.format("[%s] condition not found!", optId.getName()));
            }
            HelpPrinter.outputConditionDesc(System.out, optId.getName(), tm);
            return true;
        }

        if (props.isChannelDesc()) {
            String chanName = props.getChannelDesc();
            EntityHandler<ChannelBase> tm = enGenerator.getChannelHandler(chanName);
            if (tm == null) {
                enGenerator.registerChannelEntityHandler(chanName);
                tm = enGenerator.getChannelHandler(chanName);
            }
            if (tm == null) {
                throw new IllegalArgumentException(String.format("[%s] channel not found!", chanName));
            }
            HelpPrinter.outputConditionDesc(System.out, chanName, tm);
            return true;
        }

        if (props.isConditionClass()) {
            OptId optId = props.getConditionClass();
            EntityHandler<CondBase> tm = enGenerator.getConditionHandler(optId);
            if (tm == null) {
                throw new IllegalArgumentException(String.format("[%s] condition not found!", optId.getName()));
            }
            tm.getEntityClass().outputClassFile(System.out);
            return true;
        }

        if (props.isConditionXml()) {
            OptId optId = props.getConditionXml();
            CondEntityHandler ceh = enGenerator.getConditionHandler(optId);
            if (ceh == null) {
                throw new IllegalArgumentException(String.format("[%s] condition not found!", optId.getName()));
            }
            xmlm.printCondExample(props, ceh, System.out);
            return true;
        }

        if (props.isChannelClass()) {
            String chName = props.getChannelClass();
            EntityHandler<ChannelBase> tm = enGenerator.getChannelHandler(chName);
            if (tm == null) {
                enGenerator.registerChannelEntityHandler(chName);
                tm = enGenerator.getChannelHandler(chName);
            }
            if (tm == null) {
                throw new IllegalArgumentException(String.format("[%s] channel not found!", chName));
            }
            tm.getEntityClass().outputClassFile(System.out);
            return true;
        }

        return false;

    }

    @Override
    public void handleData(SessionManager sm, DataFile file, AuditLog alog, OperatorAuth auth) throws Exception {

        saveDatasetRoot(sm, file.getRoot(), alog, file, null, null, auth);
        
    }
    
    // Recursively process datasets and save
    private void saveDatasetRoot(SessionManager sm, DatasetRoot root, 
                                 AuditLog alog, DataFile file,
                                 Dataset parent,
                                 Header parentHeader,
                                 OperatorAuth auth) throws Exception {
        
        Header header = root.getHeader();
        
        if (header == null) {
            throw new XMLParseException("Header is not defined!");
        }

        if (root.getHeader().getRun() == null && parentHeader != null) {
            root.getHeader().setRun(parentHeader.getRun());
        }
        
        // Save root dataset
        rf.createCondDao(sm, auth).saveCondition(root, alog, file, parent);

        // Save children
        for (Dataset d: root.getDatasets()) {
            if (d.getChildDatasets() != null) {
                for (DatasetRoot childRoot: d.getChildDatasets()) {

                    saveDatasetRoot(sm, childRoot, alog, file, d, root.getHeader(), auth);

                }
            }
        }
    }
    
}
