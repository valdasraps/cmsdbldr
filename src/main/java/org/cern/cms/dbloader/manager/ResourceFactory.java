package org.cern.cms.dbloader.manager;

import org.cern.cms.dbloader.dao.*;
import org.cern.cms.dbloader.dao.AuditLogDao;
import org.cern.cms.dbloader.manager.file.ArchiveFile;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.manager.file.FileBase;

import java.io.File;
import org.cern.cms.dbloader.util.OperatorAuth;

public interface ResourceFactory {

    AuditLogDao createAuditDao(FileBase df, OperatorAuth auth);
    CondDao createCondDao(SessionManager sm, OperatorAuth auth);
    TrackingDao createTrackingDao(SessionManager sm, OperatorAuth auth);
    PartDao createPartDao(SessionManager sm, OperatorAuth auth);
    DatasetDao createDatasetDao(SessionManager sm, OperatorAuth auth);
    ChannelDao createChannelDao(SessionManager sm, OperatorAuth auth);
    ConfigDao createConfigDao(SessionManager sm, OperatorAuth auth);
    AssemblyDao createAssemblyDao(SessionManager sm, OperatorAuth auth);

    SessionManager createSessionManager();
    DataFile createDataFile(FileBase archive, File file, DataFile.Type type);
    ArchiveFile createArchiveFile(File file);

}
