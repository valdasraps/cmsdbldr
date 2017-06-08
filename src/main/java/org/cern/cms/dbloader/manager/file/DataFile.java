package org.cern.cms.dbloader.manager.file;

import lombok.Getter;
import lombok.ToString;
import org.cern.cms.dbloader.manager.FileTypeManager;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Getter
@ToString(callSuper=true)
public class DataFile extends FileBase implements Comparable<DataFile> {

    private final FileBase archive;
    private Integer type;

    FileTypeManager fm = new FileTypeManager();

    public DataFile(FileBase archive, File file) throws Exception {
        super(file);
        this.archive = archive;
        this.type = fm.returnFileType(file);
    }

    @Override
    public boolean isArchive() {
        return false;
    }

    @Override
    public List<DataFile> getDataFiles() { return Collections.singletonList(this);
//    public ArrayList<DataFile> getDataFiles() { return Collections.singleton(this);
    }

    @Override
    public int compareTo(DataFile o) {
        return getType().compareTo(o.getType());
    }

    public void setType(int type) {
        this.type = type;
    }

}
