package org.cern.cms.dbloader.app;

import org.cern.cms.dbloader.manager.SessionManager;
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
     * @param sm
     * @param file
     * @param alog
     * @return true if info was handled, false - otherwise
     * @throws Exception
     */
    public void handleData(SessionManager sm, DataFile file, AuditLog alog) throws Exception {

    }

}
