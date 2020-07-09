package org.cern.cms.dbloader.app;

import com.google.inject.Inject;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.util.NotAuthorizedException;

public abstract class AppBase {

    @Inject
    protected ResourceFactory rf;

    @Inject
    protected PropertiesManager props;

    /**
     * Check action permission.On error - throw exception.
     * @throws org.cern.cms.dbloader.util.NotAuthorizedException
     */
    public abstract void checkPermission() throws NotAuthorizedException;
    
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
     * @throws Exception
     */
    public void handleData(SessionManager sm, DataFile file, AuditLog alog) throws Exception {

    }

}
