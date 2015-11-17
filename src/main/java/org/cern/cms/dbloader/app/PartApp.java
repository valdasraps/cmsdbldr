package org.cern.cms.dbloader.app;

import org.cern.cms.dbloader.dao.PartDao;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.model.xml.Root;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.model.managemnt.AuditLog;

@Singleton
public class PartApp extends AppBase {

    @Inject
    private ResourceFactory rf;

    @Override
    public boolean handleInfo() throws Exception {
        return false;
    }

    @Override
    public boolean handleData(SessionManager sm, DataFile file, Root root, AuditLog alog) throws Exception {
        if (root.getParts() != null) {
            PartDao dao = rf.createPartDao(sm);
            dao.savePart(root, alog);
            return true;
        }
        return false;

    }

}
