package org.cern.cms.dbloader.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.apache.log4j.Level;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;

@Log4j
public class CondManager {

	private final static String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
	private final static String KINDS_OF_CONDITIONS_SQL = "select NAME, EXTENSION_TABLE_NAME from %s.KINDS_OF_CONDITIONS WHERE IS_RECORD_DELETED = 'F'";
	private final static String CHANNELS_SQL = "select distinct EXTENSION_TABLE_NAME from %s.CHANNEL_MAPS_BASE WHERE IS_RECORD_DELETED = 'F'";
	
	private Map<String, CondEntityHandler> conditions = new HashMap<>();
	private Map<String, ChannelEntityHandler> channels = new HashMap<>();
	
	public CondManager(PropertiesManager props) throws Exception {
		
		Class.forName(ORACLE_DRIVER);
		try (Connection conn = DriverManager.getConnection(props.getUrl(), props.getUsername(), props.getPassword())) {
			
			// Collect Kinds of Conditions
			try (PreparedStatement stmt = conn.prepareStatement(String.format(KINDS_OF_CONDITIONS_SQL, props.getCoreConditionSchemaName()))) {
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						
						String conditionName = rs.getString(1);
						String extensionTable = rs.getString(2);
						String extensionTableWithSchema = props.getExtConditionTable(extensionTable);
						
						try (PreparedStatement stmt1 = conn.prepareStatement("select * from ".concat(extensionTableWithSchema))) {
							ResultSetMetaData md = stmt1.getMetaData();
							
							CondEntityHandler t = new CondEntityHandler(conditionName, props.getExtConditionSchemaName(), extensionTable, md);
							conditions.put(conditionName, t);
							
						} catch (Exception ex) {
							log.warn(String.format("Condition [%s] table [%s]: %s. Skipping..", 
									conditionName, extensionTableWithSchema, ex.getMessage()), 
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
	
	public CondEntityHandler getConditionHandler(String condition) {
		return conditions.get(condition);
	}
	
	public Collection<CondEntityHandler> getConditionHandlers() {
		return conditions.values();
	}

	public ChannelEntityHandler getChannelHandler(String extensionTableName) {
		return channels.get(extensionTableName);
	}
	
	public Collection<ChannelEntityHandler> getChannelHandlers() {
		return channels.values();
	}
	
}
