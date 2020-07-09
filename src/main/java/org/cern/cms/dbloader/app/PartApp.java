package org.cern.cms.dbloader.app;

import org.cern.cms.dbloader.dao.PartDao;
import org.cern.cms.dbloader.manager.*;
import org.cern.cms.dbloader.manager.file.DataFile;

import com.google.inject.Singleton;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.serial.Root;
import org.cern.cms.dbloader.util.NotAuthorizedException;


@Singleton
public class PartApp extends AppBase {

    @Override
    public void checkPermission() throws NotAuthorizedException {
        if (!props.isOperatorConstructPermission()) {
            throw new NotAuthorizedException(PropertiesManager.UserOption.OPERATOR_CONSTRUCT_PERMISSION.name());
        }
    }
    
    @Override
    public boolean handleInfo() throws Exception {
        return false;
    }

    @Override
    public void handleData(SessionManager sm, DataFile file, AuditLog alog) throws Exception {

        Root root = file.getRoot();

        PartDao dao = rf.createPartDao(sm);
        dao.savePart(root, alog, file);
    }

}
