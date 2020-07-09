package org.cern.cms.dbloader.app;

import com.google.inject.Singleton;
import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.dao.ConfigDao;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.serial.Root;
import org.cern.cms.dbloader.util.NotAuthorizedException;

/**
 * Created by aisi0860 on 5/24/17.
 */
@Log4j
@Singleton
public class ConfigApp extends AppBase{

    @Override
    public void checkPermission() throws NotAuthorizedException {
        if (!props.isOperatorConditionPermission()) {
            throw new NotAuthorizedException(PropertiesManager.UserOption.OPERATOR_CONDITION_PERMISSION.name());
        }
    }
    
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