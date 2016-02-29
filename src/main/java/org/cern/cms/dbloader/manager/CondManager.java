package org.cern.cms.dbloader.manager;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.apache.log4j.Level;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.model.OptId;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Log4j
@Singleton
public class CondManager {

    private final static String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    // ADDED TO QUERY KIND_OF_CONDITION_ID
    private final static String KINDS_OF_CONDITIONS_SQL = "select KIND_OF_CONDITION_ID, NAME, EXTENSION_TABLE_NAME from %s.KINDS_OF_CONDITIONS WHERE IS_RECORD_DELETED = 'F'";
    private final static String CHANNELS_SQL = "select distinct EXTENSION_TABLE_NAME from %s.CHANNEL_MAPS_BASE WHERE IS_RECORD_DELETED = 'F'";

    private Set<CondEntityHandler> conditions = new HashSet<>();
    private Map<String, ChannelEntityHandler> channels = new HashMap<>();

    @Inject
    public CondManager(PropertiesManager props) throws Exception {

        Class.forName(ORACLE_DRIVER);
        try (Connection conn = DriverManager.getConnection(props.getUrl(), props.getUsername(), props.getPassword())) {

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
                        String extensionTableWithSchema = props.getExtConditionTable(extensionTable);

                        try (PreparedStatement stmt1 = conn.prepareStatement("select * from ".concat(extensionTableWithSchema))) {
                            ResultSetMetaData md = stmt1.getMetaData();

                            ChannelEntityHandler t = new ChannelEntityHandler(props.getExtConditionSchemaName(), extensionTable, md);
                            channels.put(extensionTable, t);

                        } catch (Exception ex) {
                            log.warn(String.format("Channel table [%s]: %s. Skipping..",
                                    extensionTableWithSchema, ex.getMessage()),
                                    props.getLogLevel().equals(Level.DEBUG) ? ex : null);
                        }
                    }
                }
            }

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

}
