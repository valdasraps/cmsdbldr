package org.cern.cms.dbloader.app;

import javax.management.modelmbean.XMLParseException;

import lombok.extern.log4j.Log4j;

import org.cern.cms.dbloader.manager.DynamicEntityGenerator;
import org.cern.cms.dbloader.manager.HelpPrinter;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.EntityHandler;
import org.cern.cms.dbloader.model.OptId;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.serial.Header;
import org.cern.cms.dbloader.model.serial.Root;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.model.managemnt.AuditLog;

import org.cern.cms.dbloader.manager.XmlManager;

@Log4j
@Singleton
public class CondApp extends AppBase {

    @Inject
    private PropertiesManager props;

    @Inject
    private DynamicEntityGenerator enGenerator;

    @Inject
    private ResourceFactory rf;

    @Inject
    private XmlManager xmlm;
    
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
    public void handleData(SessionManager sm, DataFile file, AuditLog alog) throws Exception {
        Root root = file.getRoot();
        Header h = root.getHeader();

        if (root.getDatasets().isEmpty()) {
            throw new XMLParseException("No dataset defined!");
        }

        if (h.getKindOfCondition() == null) {
            throw new XMLParseException("No Kind of Conition defined!");
        }

        if (h.getKindOfCondition().getName() == null) {
            throw new XMLParseException("No Kind of Condition name defined!");
        }
        
        // Try to load the file!
        rf.createCondDao(sm).saveCondition(root, alog, file);

    }

}
