package org.cern.cms.dbloader.manager.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;

@Getter
@ToString(callSuper=false, of={"file"})
@EqualsAndHashCode(of = { "md5" })
public abstract class FileBase {

    private final File file;
    public final String md5;
    
    public FileBase(File file) throws IOException {
        this.file = file;

        // Calculated MD5 and checks file for availability!
        try (FileInputStream fis = new FileInputStream(file)) {
            this.md5 = DigestUtils.md5Hex(fis);
        }

    }
    
    public String getFilename() {
        return file.getName();
    }

    public abstract boolean isArchive();
    public abstract List<DataFile> getDataFiles() throws Exception;
    
}
