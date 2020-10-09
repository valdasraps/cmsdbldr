package org.cern.cms.dbloader.manager;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.apache.log4j.Level;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.ConstructEntityHandler;
import org.cern.cms.dbloader.model.OptId;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.cern.cms.dbloader.model.construct.PartDetailsBase;

@Log4j
@Singleton
public class DynamicEntityGenerator {

    private final static String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";

    private final static String KINDS_OF_CONDITIONS_SQL = "select KIND_OF_CONDITION_ID, NAME, EXTENSION_TABLE_NAME from %s.KINDS_OF_CONDITIONS WHERE IS_RECORD_DELETED = 'F'";
    private final static String CHANNELS_SQL = "select distinct EXTENSION_TABLE_NAME from %s.CHANNEL_MAPS_BASE WHERE IS_RECORD_DELETED = 'F'";
    private final static String PARTS_DETAILS_SQL = "select EXTENSION_TABLE_NAME, KIND_OF_PART_ID, DISPLAY_NAME from %s.KINDS_OF_PARTS WHERE IS_RECORD_DELETED = 'F'";


    private Set<CondEntityHandler> conditions = new HashSet<>();
    private Map<String, ChannelEntityHandler> channels = new HashMap<>();
    private Set<ConstructEntityHandler> parts_details = new HashSet<>();


    private final PropertiesManager props;

    @Inject
    public DynamicEntityGenerator(PropertiesManager props) throws Exception {
        this.props = props;
        try (Connection conn = getConnection()) {

            // Collect Kinds of Conditions
            try (PreparedStatement stmt = conn.prepareStatement(String.format(KINDS_OF_CONDITIONS_SQL, props.getCoreConditionSchemaName()))) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {

                        BigInteger id = new BigInteger(rs.getString(1));
                        String name = rs.getString(2);
                        String table = rs.getString(3);
                        String fullTable = props.getExtConditionTable(table);

                        try (PreparedStatement stmt1 = conn.prepareStatement("select * from ".concat(fullTable))) {
                            ResultSetMetaData md = stmt1.getMetaData();

                            CondEntityHandler t = new CondEntityHandler(id, name, props.getExtConditionSchemaName(), table, md);
                            conditions.add(t);

                        } catch (Exception ex) {
                            log.warn(String.format("Condition [%s] table [%s]: %s. Skipping..",
                                    name, fullTable, ex.getMessage()),
                                    props.getLogLevel().equals(Level.DEBUG) ? ex : null);
                        }
                    }
                }
            }

            // Collect Channels
            try (PreparedStatement stmt = conn.prepareStatement(String.format(CHANNELS_SQL, props.getCoreConditionSchemaName()))) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String extensionTable = rs.getString(1);
                        try {

                            registerChannelEntityHandler(conn, extensionTable);

                        } catch (Exception ex) {
                            log.warn(String.format("Channel table [%s]: %s. Skipping..",
                                    props.getExtConditionTable(extensionTable), ex.getMessage()),
                                    props.getLogLevel().equals(Level.DEBUG) ? ex : null);
                        }
                    }
                }
            }

            // Collect KINDS_OF_PARTS extension tables
            try (PreparedStatement stmt = conn.prepareStatement(String.format(PARTS_DETAILS_SQL, props.getCoreConstructSchemaName()))) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {

                        String table = rs.getString(1);
                        String id = rs.getString(2);
                        String kopName = rs.getString(3);
                        String fullTable = props.getExtConstructTable(table);

                        if (table != null && ! "PARTS".equals(table)) {
                        
                            try (PreparedStatement stmt1 = conn.prepareStatement("select * from ".concat(fullTable))) {
                                ResultSetMetaData md = stmt1.getMetaData();

                                ConstructEntityHandler t = new ConstructEntityHandler(id, kopName, props.getExtConstructSchemaName(), table, md);

                                parts_details.add(t);

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
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(ORACLE_DRIVER);
        Connection conn = DriverManager.getConnection(props.getUrl(), props.getUsername(), props.getPassword());
        return conn;
    }
    
    public final void registerChannelEntityHandler(String extensionTableName) throws Exception {
        try (Connection conn = getConnection()) {
            registerChannelEntityHandler(conn, extensionTableName);
        }
    }
    
    private void registerChannelEntityHandler(Connection conn, String extensionTableName) throws Exception {
        String extensionTableWithSchema = props.getExtConditionTable(extensionTableName);
        try (PreparedStatement stmt1 = conn.prepareStatement("select * from ".concat(extensionTableWithSchema))) {
            ResultSetMetaData md = stmt1.getMetaData();
            ChannelEntityHandler t = new ChannelEntityHandler(props.getExtConditionSchemaName(), extensionTableName, md);
            channels.put(extensionTableName, t);

        }
    }
    
    public CondEntityHandler getConditionHandler(OptId optId) {
        if (optId.hasId()) {
            return getConditionHandler(optId.getId());
        } else {
            return getConditionHandler(optId.getName());
        }
    }

    public CondEntityHandler getConditionHandler(String name) {
        for (CondEntityHandler h : conditions) {
            if (h.getName().equals(name)) {
                return h;
            }
        }
        return null;
    }

    public CondEntityHandler getConditionHandler(BigInteger id) {
        for (CondEntityHandler h : conditions) {
            if (h.getId().equals(id)) {
                return h;
            }
        }
        return null;
    }

    public Collection<CondEntityHandler> getConditionHandlers() {
        return conditions;
    }

    public ChannelEntityHandler getChannelHandler(String extensionTableName) {
        return channels.get(extensionTableName);
    }

    public Collection<ChannelEntityHandler> getChannelHandlers() {
        return channels.values();
    }

    public ConstructEntityHandler getConstructHandler(String kopName) {
        for (ConstructEntityHandler h : parts_details) {
            if (h.getKopName().equals(kopName)) {
                return h;
            }
        }

        return null;
    }

    public PartDetailsBase copyParameters(PartDetailsBase x) {
        Set<ConstructEntityHandler>  data = parts_details;
        return x;
    }
    public Collection<ConstructEntityHandler> getConstructHandlers() {
        return parts_details;
    }

}
