package org.cern.cms.dbloader.app;

import org.cern.cms.dbloader.dao.PartDao;
import org.cern.cms.dbloader.manager.*;
import org.cern.cms.dbloader.manager.file.DataFile;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.ConstructEntityHandler;
import org.cern.cms.dbloader.model.construct.KindOfPart;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.serial.Header;
import org.cern.cms.dbloader.model.serial.Root;
import org.hibernate.criterion.Restrictions;

import javax.management.modelmbean.XMLParseException;

@Singleton
public class PartApp extends AppBase {

    @Inject
    private ResourceFactory rf;

    @Inject
    private PropertiesManager props;

    @Inject
    private DynamicEntityGenerator enGenerator;

    @Override
    public boolean handleInfo() throws Exception {
        return false;
    }

    @Override
    public void handleData(SessionManager sm, DataFile file, AuditLog alog) throws Exception {

        Root root = file.getRoot();


        for (Part p : root.getParts()) {
            ConstructEntityHandler coneh = enGenerator.getConstructHandler(p.getKindOfPartName());
            if (coneh == null) {
                throw new XMLParseException(String.format("PART DETAILS not resolved: %s", p.getKindOfPartName()));
            }


        ConstructXmlManager xmlm = new ConstructXmlManager(coneh);
        root = (Root) xmlm.unmarshal(file.getFile());


        LobManager lobManager = new LobManager();
        lobManager.lobParserParts(root, coneh, file);

        }


        PartDao dao = rf.createPartDao(sm);
        dao.savePart(root, alog);
    }

}
