package org.cern.cms.dbloader.app;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.dao.ConfigDao;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.xml.Root;

import javax.management.modelmbean.XMLParseException;

/**
 * Created by aisi0860 on 5/24/17.
 */
@Log4j
@Singleton
public class ConfigApp extends AppBase{

    @Inject
    private ResourceFactory rf;

    @Override
    public void handleData(SessionManager sm, DataFile file, AuditLog alog) throws Exception {

        ConfigDao dao = rf.createConfigDao(sm);
        Root root = file.getRoot();

        switch (file.getType()) {
            case VERSION_ALIAS:
            dao.saveVersionAliases(root.getMaps().getVersionAliases(), alog);
            break;
            case KEY:
                dao.saveKey(root.getMaps().getKey(), alog);
                break;
            case KEY_ALIAS:
                dao.saveKeyAlias(root.getMaps().getKeyAlias(), alog);
        }

    }



}