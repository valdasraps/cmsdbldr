package org.cern.cms.dbloader.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Level;
import org.cern.cms.dbloader.metadata.ConstructEntityHandler;
import org.cern.cms.dbloader.model.OptId;

import java.math.BigInteger;
import java.sql.*;
import java.util.*;

@Log4j
@Singleton
public class ConstructManager {

    private final static String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private final static String PARTS_DETAILS_SQL = "select EXTENSION_TABLE_NAME from %s.KINDS_OF_PARTS WHERE IS_RECORD_DELETED = 'F'";

//    private Set<CondEntityHandler> conditions = new HashSet<>();
//    private Map<String, ChannelEntityHandler> channels = new HashMap<>();
    private Set<ConstructEntityHandler> parts_details = new HashSet<>();

    private final PropertiesManager props;

    @Inject
    public ConstructManager(PropertiesManager props) throws Exception {
        this.props = props;
        try (Connection conn = getConnection()) {

            // Collect Kinds of Conditions
            try (PreparedStatement stmt = conn.prepareStatement(String.format(PARTS_DETAILS_SQL, props.getCoreConstructSchemaName()))) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {

                        String table = rs.getString(1);
                        String fullTable = props.getExtConstructTable(table);

                        try (PreparedStatement stmt1 = conn.prepareStatement("select * from ".concat(fullTable))) {
                            ResultSetMetaData md = stmt1.getMetaData();

                        } catch (Exception ex) {
                            log.warn(String.format("KOP extension table [%s]: %s. Skipping..",
                                    fullTable, ex.getMessage()),
                                    props.getLogLevel().equals(Level.DEBUG) ? ex : null);
                        }
                    }
                }
            }
        }
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(ORACLE_DRIVER);
        Connection conn = DriverManager.getConnection(props.getUrl(), props.getUsername(), props.getPassword());
        return conn;
    }

    
//    public ConstructEntityHandler getConstructHandler(OptId optId) {
//        if (optId.hasId()) {
//            return getConstructHandler(optId.getId());
//        } else {
//            return getConstructHandler(optId.getName());
//        }
//    }

//    public ConstructEntityHandler getConstructHandler(String name) {
//        for (ConstructEntityHandler h : parts_details) {
//            if (h.getName().equals(name)) {
//                return h;
//            }
//        }
//        return null;
//    }
//
//    public ConstructEntityHandler getConstructHandler(BigInteger id) {
//        for (ConstructEntityHandler h : parts_details) {
//            if (h.getId().equals(id)) {
//                return h;
//            }
//        }
//        return null;
//    }

    public Collection<ConstructEntityHandler> getConditionHandlers() {
        return parts_details;
    }

}
