package org.cern.cms.dbloader.dao;

import lombok.RequiredArgsConstructor;

import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.manager.PropertiesManager;

import com.google.inject.Inject;

@RequiredArgsConstructor
public abstract class DaoBase {

	@Inject
	protected PropertiesManager props;
	
	protected final HbmManager hbm;
	
}
