package org.cern.cms.dbloader.dao;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.PropertiesManager;

@RequiredArgsConstructor
public abstract class DaoBase {

    protected static final String DEFAULT_VERSION = "1.0";
    protected static final String NO_RUN_MODE = "no-run";
    protected static final long DEFAULT_EMAP_RUN_ID = 10000000L;

    @Inject
    protected PropertiesManager props;

    protected final HbmManager hbm;

}
