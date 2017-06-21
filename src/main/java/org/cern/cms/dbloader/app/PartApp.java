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
    public void handleData(SessionManager sm, DataFile file, AuditLog alog) throws Exception {
        PartDao dao = rf.createPartDao(sm);
        dao.savePart(file.getRoot(), alog);
    }

}
