package org.cern.cms.dbloader.app;

import com.google.inject.Inject;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.cern.cms.dbloader.dao.ChannelDao;
import org.cern.cms.dbloader.manager.DynamicEntityGenerator;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.serial.ChannelUpdate;
import org.cern.cms.dbloader.model.serial.Root;
import org.cern.cms.dbloader.util.NotAuthorizedException;
import org.cern.cms.dbloader.util.OperatorAuth;

@Log4j
public class ChannelApp extends AppBase {

    @Inject
    private DynamicEntityGenerator enGenerator;

    @Override
    public void checkPermission(OperatorAuth auth) throws NotAuthorizedException {
        if (!auth.isConstructPermission()) {
            throw new NotAuthorizedException(PropertiesManager.UserOption.OPERATOR_CONSTRUCT_PERMISSION.name());
        }
    }

    @Override
    public void handleData(SessionManager sm, DataFile file, AuditLog alog, OperatorAuth auth) throws Exception {
        Root root = file.getRoot();
        List<ChannelUpdate> updates = root.getChannelUpdates();
        if (updates != null && !updates.isEmpty()) {

            alog.setDatasetRecordCount(0);
            alog.setDatasetCount(0);
            
            for (ChannelUpdate update : updates) {

                if (enGenerator.getChannelHandler(update.getExtensionTableName()) == null) {
                    enGenerator.registerChannelEntityHandler(update.getExtensionTableName());
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

        }

    }

}
