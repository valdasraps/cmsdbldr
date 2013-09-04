package org.cern.cms.dbloader.app;

import java.io.File;

import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.xml.Root;

public abstract class AppBase {

	/**
	 * Information handler
	 * @return true if info was handled, false - otherwise
	 */
	public boolean handleInfo() throws Exception {
		return false;
	}
	
	/**
	 * Data handler
	 * @param file
	 * @param root
	 * @param auditLog
	 * @return true if info was handled, false - otherwise
	 * @throws Exception
	 */
	public boolean handleData(File file, HbmManager hbm, Root root, AuditLog auditLog) throws Exception {
		return false;
	}

}
