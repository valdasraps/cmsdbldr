package org.cern.cms.dbloader.dao;

import lombok.RequiredArgsConstructor;

import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.manager.PropertiesManager;

@RequiredArgsConstructor
public abstract class DaoBase {

	protected final PropertiesManager props;
	protected final HbmManager hbm;
	
}
