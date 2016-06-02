package org.cern.cms.dbloader.manager;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LogManager {

    public static void setLogging(PropertiesManager props) {
        
        Logger.getRootLogger().setLevel(props.getLogLevel());
        
        if (props.isPrintSQL()) {
            Logger.getLogger("org.hibernate.SQL").setLevel(Level.DEBUG);
            Logger.getLogger("org.hibernate.type").setLevel(Level.TRACE);
            Logger.getLogger("java.sql").setLevel(Level.TRACE);
        } else {
            Logger.getLogger("org.hibernate.SQL").setLevel(props.getLogLevel());
            Logger.getLogger("org.hibernate.type").setLevel(props.getLogLevel());
            Logger.getLogger("java.sql").setLevel(props.getLogLevel());
        }
        
    }
    
}
