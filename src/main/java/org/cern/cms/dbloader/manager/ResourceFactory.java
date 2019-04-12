package org.cern.cms.dbloader.manager;

import com.google.inject.assistedinject.Assisted;
import org.cern.cms.dbloader.dao.*;
import org.cern.cms.dbloader.handler.AuditLogHandler;
import org.cern.cms.dbloader.manager.file.ArchiveFile;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;

import java.io.File;
import java.util.Optional;

public interface ResourceFactory {

    AuditLogHandler createAuditDao(FileBase df);
    CondDao createCondDao(SessionManager sm);
    TrackingDao createTrackingDao(SessionManager sm);
    PartDao createPartDao(SessionManager sm);
    DatasetDao createDatasetDao(SessionManager sm);
    ChannelDao createChannelDao(SessionManager sm);
    SessionManager createSessionManager();
	ConfigDao createConfigDao(SessionManager sm);
	DataFile createDataFile(FileBase archive, File file, DataFile.Type type);
	ArchiveFile createArchiveFile(File file);
    CondXmlManager createCondXmlManager(CondEntityHandler condeh, Optional<ChannelEntityHandler> chaneh);

}
