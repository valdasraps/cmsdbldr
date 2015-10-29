package org.cern.cms.dbloader.app;

import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.xml.Root;

public abstract class AppBase {

    /**
     * Information handler
     *
     * @return true if info was handled, false - otherwise
     * @throws java.lang.Exception
     */
    public boolean handleInfo() throws Exception {
        return false;
    }

    /**
     * Data handler
     *
     * @param file
     * @param hbm
     * @param root
     * @param auditLog
     * @return true if info was handled, false - otherwise
     * @throws Exception
     */
    public boolean handleData(DataFile file, HbmManager hbm, Root root, AuditLog auditLog) throws Exception {
        return false;
    }

}
