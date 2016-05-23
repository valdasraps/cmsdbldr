package org.cern.cms.dbloader.app;

import com.google.inject.Inject;
import java.util.List;
import org.cern.cms.dbloader.dao.ChannelDao;
import org.cern.cms.dbloader.manager.CondManager;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.xml.ChannelUpdate;
import org.cern.cms.dbloader.model.xml.Root;

public class ChannelApp extends AppBase {

    @Inject
    private PropertiesManager props;

    @Inject
    private CondManager condm;

    @Inject
    private ResourceFactory rf;
    
    @Override
    public boolean handleData(SessionManager sm, DataFile file, Root root, AuditLog alog) throws Exception {
        List<ChannelUpdate> updates = root.getChannelUpdates();
        if (updates != null && !updates.isEmpty()) {
            
            for (ChannelUpdate update: updates) {
                ChannelDao dao = rf.createChannelDao(sm);
                dao.process(update, alog);
            }
            
            return true;
        }
        return false;
    }
    
}
