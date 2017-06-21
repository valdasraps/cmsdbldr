package org.cern.cms.dbloader.manager.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.inject.assistedinject.Assisted;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.cern.cms.dbloader.manager.FilesManager;

import javax.inject.Inject;

@Getter
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = true)
public class ArchiveFile extends FileBase {


    @Inject
    FilesManager fm;

    @Inject
    public ArchiveFile(@Assisted File archive) throws IOException {
        super(archive);
    }

    @Override
    public boolean isArchive() {
        return true;
    }

    @Override
    public List<DataFile> getDataFiles() throws Exception {
        return fm.getDataFiles(this);
    }
    
}
