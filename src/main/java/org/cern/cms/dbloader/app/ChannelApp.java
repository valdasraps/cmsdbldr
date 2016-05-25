package org.cern.cms.dbloader.app;

import com.google.inject.Inject;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.cern.cms.dbloader.dao.ChannelDao;
import org.cern.cms.dbloader.manager.CondManager;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.xml.ChannelUpdate;
import org.cern.cms.dbloader.model.xml.Root;

@Log4j
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

            alog.setDatasetRecordCount(0);
            alog.setDatasetCount(0);
            
            for (ChannelUpdate update : updates) {

                if (condm.getChannelHandler(update.getExtensionTableName()) == null) {
                    condm.registerChannelEntityHandler(update.getExtensionTableName());
                }

                Path path = Paths.get(file.getFile().toURI()).resolveSibling(update.getFileName());

                if (!(Files.isRegularFile(path) && Files.exists(path) && Files.isReadable(path))) {
                    throw new FileNotFoundException(path.toAbsolutePath().toString());
                }

                update.setFileName(path.toAbsolutePath().toString());
            }

            for (ChannelUpdate update : updates) {

                ChannelDao dao = rf.createChannelDao(sm);
                dao.process(update, alog);
                
                alog.setDatasetCount(alog.getDatasetCount() + 1);

            }

            return true;
        }
        return false;
    }

}
