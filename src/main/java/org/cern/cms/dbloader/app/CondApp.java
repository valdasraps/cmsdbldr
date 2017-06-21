package org.cern.cms.dbloader.app;

import javax.management.modelmbean.XMLParseException;

import lombok.extern.log4j.Log4j;

import org.cern.cms.dbloader.dao.CondDao;
import org.cern.cms.dbloader.manager.CondManager;
import org.cern.cms.dbloader.manager.CondXmlManager;
import org.cern.cms.dbloader.manager.HelpPrinter;
import org.cern.cms.dbloader.manager.LobManager;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.EntityHandler;
import org.cern.cms.dbloader.model.OptId;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.xml.Header;
import org.cern.cms.dbloader.model.xml.Root;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.model.managemnt.AuditLog;

@Log4j
@Singleton
public class CondApp extends AppBase {

    @Inject
    private PropertiesManager props;

    @Inject
    private CondManager condm;

    @Inject
    private ResourceFactory rf;

    @Override
    public boolean handleInfo() throws Exception {

        if (props.isConditionsList()) {
            HelpPrinter.outputConditionList(System.out, condm);
            return true;
        }

        if (props.isChannelsList()) {
            HelpPrinter.outputChannelList(System.out, condm);
            return true;
        }

        if (props.isConditionDesc()) {
            OptId optId = props.getConditionDesc();
            EntityHandler<CondBase> tm = condm.getConditionHandler(optId);
            if (tm == null) {
                throw new IllegalArgumentException(String.format("[%s] condition not found!", optId.getName()));
            }
            HelpPrinter.outputConditionDesc(System.out, optId.getName(), tm);
            return true;
        }

        if (props.isChannelDesc()) {
            String chanName = props.getChannelDesc();
            EntityHandler<ChannelBase> tm = condm.getChannelHandler(chanName);
            if (tm == null) {
                condm.registerChannelEntityHandler(chanName);
                tm = condm.getChannelHandler(chanName);
            }
            if (tm == null) {
                throw new IllegalArgumentException(String.format("[%s] channel not found!", chanName));
            }
            HelpPrinter.outputConditionDesc(System.out, chanName, tm);
            return true;
        }

        if (props.isConditionClass()) {
            OptId optId = props.getConditionClass();
            EntityHandler<CondBase> tm = condm.getConditionHandler(optId);
            if (tm == null) {
                throw new IllegalArgumentException(String.format("[%s] condition not found!", optId.getName()));
            }
            tm.getEntityClass().outputClassFile(System.out);
            return true;
        }

        if (props.isConditionXml()) {
            OptId optId = props.getConditionXml();
            CondEntityHandler ceh = condm.getConditionHandler(optId);
            if (ceh == null) {
                throw new IllegalArgumentException(String.format("[%s] condition not found!", optId.getName()));
            }
            CondXmlManager xmlm = new CondXmlManager(ceh, null);
            xmlm.printExample(props, System.out);
            return true;
        }

        if (props.isChannelClass()) {
            String chName = props.getChannelClass();
            EntityHandler<ChannelBase> tm = condm.getChannelHandler(chName);
            if (tm == null) {
                condm.registerChannelEntityHandler(chName);
                tm = condm.getChannelHandler(chName);
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

        CondEntityHandler condeh = condm.getConditionHandler(h.getKindOfCondition().getName());
        if (condeh == null) {
            throw new XMLParseException(String.format("Kind of Condition not resolved: %s", h.getKindOfCondition()));
        }

        ChannelEntityHandler chaneh = null;
        if (h.getHint() != null) {
            if (h.getHint().getChannelMap() != null) {
                chaneh = condm.getChannelHandler(h.getHint().getChannelMap());
                if (chaneh == null) {
                    throw new XMLParseException(String.format("Channel Map not resolved: %s", h.getHint()));
                }

            }
        }

        // So far - success!
        log.info(String.format("%s (%s) with %d dataset and %s channel map to be processed", condeh.getId(), condeh.getName(), root.getDatasets().size(), chaneh));

        CondXmlManager xmlm = new CondXmlManager(condeh, chaneh);
        root = (Root) xmlm.unmarshal(file.getFile());

        LobManager lobManager = new LobManager();
        lobManager.lobParser(root, condeh, file);

        // Try to load the file!
        CondDao dao = rf.createCondDao(sm);
        dao.saveCondition(root, alog);

    }

}
