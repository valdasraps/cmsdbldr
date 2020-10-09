package org.cern.cms.dbloader.app;

import org.cern.cms.dbloader.manager.*;
import org.cern.cms.dbloader.manager.file.DataFile;

import com.google.inject.Singleton;
import org.cern.cms.dbloader.dao.AssemblyDao;
import org.cern.cms.dbloader.model.construct.ext.AssemblyStep;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.serial.Root;
import org.cern.cms.dbloader.util.NotAuthorizedException;
import org.cern.cms.dbloader.util.OperatorAuth;


@Singleton
public class AssemblyApp extends AppBase {

    @Override
    public void checkPermission(OperatorAuth auth) throws NotAuthorizedException {
        if (!auth.isConstructPermission()) {
            throw new NotAuthorizedException(PropertiesManager.UserOption.OPERATOR_CONSTRUCT_PERMISSION.name());
        }
    }
    
    @Override
    public boolean handleInfo() throws Exception {
        return false;
    }

    @Override
    public void handleData(SessionManager sm, DataFile file, AuditLog alog, OperatorAuth auth) throws Exception {

        Root root = file.getRoot();
        
        for (AssemblyStep step: root.getAssemblySteps()) {
            AssemblyDao dao = rf.createAssemblyDao(sm, auth);
            dao.saveAssembly(step, alog, file);
        }
    }

}
