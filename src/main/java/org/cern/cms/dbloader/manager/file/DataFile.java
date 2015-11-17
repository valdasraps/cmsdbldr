package org.cern.cms.dbloader.manager.file;

import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Getter
@ToString(callSuper=true)
public class DataFile extends FileBase implements Comparable<DataFile> {

    private final FileBase archive;
    
    public DataFile(FileBase archive, File file) throws IOException {
        super(file);
        this.archive = archive;
    }

    @Override
    public boolean isArchive() {
        return false;
    }

    @Override
    public Set<DataFile> getDataFiles() {
        return Collections.singleton(this);
    }

    @Override
    public int compareTo(DataFile o) {
        return getFile().getName().compareTo(o.getFile().getName());
    }

}
