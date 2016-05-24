package org.cern.cms.dbloader.dao;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j;

import org.cern.cms.dbloader.manager.CondManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.EntityClass;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.xml.ChannelUpdate;
import org.hibernate.jdbc.Work;

@Log4j
public class ChannelDao extends DaoBase {

    @Inject
    private CondManager condm;
	
    private ChannelEntityHandler handler;
    
    @Inject
    public ChannelDao(@Assisted SessionManager sm) {
        super(sm);
    }
    
    public void process(final ChannelUpdate update, AuditLog alog) throws Exception {
        
    	handler = condm.getChannelHandler(update.getExtensionTableName());
    	if (handler == null) {
    		session.doWork(new Work() {
				@Override
				public void execute(Connection connection) throws SQLException {
					try {
						handler = condm.getChannelEntityHandler(props, connection, update.getExtensionTableName());
					} catch (Exception ex) {
						throw new SQLException(ex);
					}
				}
			});
    	}
    	
    	log.info(String.format("JMT Channel handler for %s found: %s", update, handler));
    	
    	EntityClass<ChannelBase> entityClass = handler.getEntityClass();
    	//entityClass.
    	
        // TODO: take ChannelHandler (aka JPA class) based on extension table
        // TODO: if ChannelHandler does not exist - try to create one:
        // TODO: create test! (a) create table TEST_CHANNELS_ONE, no data
        /*
                String extensionTable = rs.getString(1);
        String extensionTableWithSchema = props.getExtConditionTable(extensionTable);

        try (PreparedStatement stmt1 = conn.prepareStatement("select * from ".concat(extensionTableWithSchema))) {
            ResultSetMetaData md = stmt1.getMetaData();

            ChannelEntityHandler t = new ChannelEntityHandler(props.getExtConditionSchemaName(), extensionTable, md);
            */
        
        // TODO: load CSV file, look at LOB example
        // TODO: initialize classes for each CSV row?
        // TODO: save entities to database
        
    }

}
