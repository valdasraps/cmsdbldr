package org.cern.cms.dbloader.dao;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.xml.ChannelUpdate;

public class ChannelDao extends DaoBase {

    @Inject
    public ChannelDao(@Assisted SessionManager sm) {
        super(sm);
    }
    
    public void process(ChannelUpdate update, AuditLog alog) throws Exception {
        
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
