package org.cern.cms.dbloader.dao;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.Arrays;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.cern.cms.dbloader.manager.DynamicEntityGenerator;
import org.cern.cms.dbloader.manager.CsvManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.serial.ChannelUpdate;
import org.cern.cms.dbloader.util.OperatorAuth;

@Log4j
public class ChannelDao extends DaoBase {

    @Inject
    private DynamicEntityGenerator enGenerator;
    
    private final CsvManager csv = new CsvManager();

    @Inject
    public ChannelDao(@Assisted SessionManager sm, @Assisted OperatorAuth auth) throws Exception {
        super(sm, auth);
    }

    public void process(ChannelUpdate update, AuditLog alog) throws Exception {

        ChannelEntityHandler handler = enGenerator.getChannelHandler(update.getExtensionTableName());
        log.info(String.format("Channel handler for %s found: %s", update, handler));

        String [] mapping = handler.getProperties().stream().map(p -> p.getName()).toArray(i -> new String[i]);
        log.info(Arrays.toString(mapping));
        List<? extends ChannelBase> list =  csv.read(handler.getEntityClass().getC(), 
                mapping, update.getFileName());
        
        log.info(String.format("%s loaded channels %d", update, list.size()));
        for (ChannelBase ch: list) {
            ch.setExtensionTableName(update.getExtensionTableName());
            session.save(ch);
        }
        
        alog.setDatasetRecordCount(alog.getDatasetRecordCount() + list.size());
        
    }

}
