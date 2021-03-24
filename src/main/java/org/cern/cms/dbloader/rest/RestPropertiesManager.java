package org.cern.cms.dbloader.rest;

import java.util.Properties;
import org.cern.cms.dbloader.manager.PropertiesManager;

public class RestPropertiesManager extends PropertiesManager {

    private final String detName;
    private final String dbName;

    public RestPropertiesManager(final Properties props, final String detName, final String dbName) throws Exception {
        super(props, new String[]{ });
        this.detName = detName;
        this.dbName = dbName;
    }
    
    @Override
    public boolean printHelp() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDetName() { 
        return detName; 
    }

    public String getDbName() {
        return dbName;
    }
    
}
