package org.cern.cms.dbloader.manager.file;

import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.cern.cms.dbloader.manager.FilesManager;

@Getter
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = true)
public class ArchiveFile extends FileBase {

    public ArchiveFile(File archive) throws IOException {
        super(archive);
    }

    @Override
    public boolean isArchive() {
        return true;
    }

    @Override
    public List<DataFile> getDataFiles() throws Exception {
        return FilesManager.getDataFiles(this);
    }
    
}
