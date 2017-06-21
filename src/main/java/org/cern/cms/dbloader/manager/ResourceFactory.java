package org.cern.cms.dbloader.manager;

import org.cern.cms.dbloader.dao.*;
import org.cern.cms.dbloader.handler.AuditLogHandler;
import org.cern.cms.dbloader.manager.file.ArchiveFile;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.manager.file.FileBase;

import java.io.File;

public interface ResourceFactory {

	AuditLogHandler createAuditDao(FileBase df);
	CondDao createCondDao(SessionManager sm);
	PartDao createPartDao(SessionManager sm);
	DatasetDao createDatasetDao(SessionManager sm);
	ChannelDao createChannelDao(SessionManager sm);
    SessionManager createSessionManager();
	ConfigDao createConfigDao(SessionManager sm);
	DataFile createDataFile(FileBase archive, File file);
	ArchiveFile createArchiveFile(File file);
}
