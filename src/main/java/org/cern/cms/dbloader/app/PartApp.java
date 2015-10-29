package org.cern.cms.dbloader.app;

import org.cern.cms.dbloader.dao.PartDao;
import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.xml.Root;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PartApp extends AppBase {

    @Inject
    private ResourceFactory rf;

    @Override
    public boolean handleInfo() throws Exception {
        return false;
    }

    @Override
    public boolean handleData(DataFile file, HbmManager hbm, Root root, AuditLog auditlog) throws Exception {
        if (root.getParts() != null) {
            PartDao dao = rf.createPartDao(hbm);
            dao.savePart(root, auditlog);
            return true;
        }
        return false;

    }

}
