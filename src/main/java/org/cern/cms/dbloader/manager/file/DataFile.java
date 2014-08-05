package org.cern.cms.dbloader.manager.file;

import lombok.Getter;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Getter
@ToString(callSuper=false, of={"data"})
public class DataFile {

    public final File archive;
    public final File data;
    public final String md5;

    public DataFile(File archive, File data) throws IOException {
        this.archive = archive;
        this.data = data;

        // Calculated MD5 and checks file for availability!
        FileInputStream fis = new FileInputStream(data);
        this.md5 = DigestUtils.md5Hex(fis);
        fis.close();

    }

}
