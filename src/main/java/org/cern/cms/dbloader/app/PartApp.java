package org.cern.cms.dbloader.app;

import org.cern.cms.dbloader.dao.PartDao;
import org.cern.cms.dbloader.manager.*;
import org.cern.cms.dbloader.manager.file.DataFile;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.serial.Root;


@Singleton
public class PartApp extends AppBase {

    @Inject
    private ResourceFactory rf;

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
