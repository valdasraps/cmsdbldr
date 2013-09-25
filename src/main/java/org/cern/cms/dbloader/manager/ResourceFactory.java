package org.cern.cms.dbloader.manager;

import org.cern.cms.dbloader.dao.AuditDao;
import org.cern.cms.dbloader.dao.CondDao;
import org.cern.cms.dbloader.dao.DatasetDao;

public interface ResourceFactory {

	AuditDao createAuditDao(HbmManager hbm);
	CondDao createCondDao(HbmManager hbm);
	DatasetDao createDatasetDao(HbmManager hbm);
	
}
