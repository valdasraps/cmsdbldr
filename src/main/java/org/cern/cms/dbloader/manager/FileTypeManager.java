package org.cern.cms.dbloader.manager;

import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.manager.file.FileType;
import org.cern.cms.dbloader.model.xml.Root;

import javax.inject.Inject;
import java.io.File;

/**
 * Created by Aivaras Šilalė on 5/23/17.
 */

public class FileTypeManager {


    public int returnFileType(File f) throws Exception {

        XmlManager xmlm = new XmlManager();

        Root root = xmlm.unmarshal(f);

        if (root.getMaps() == null) {
            if (root.getParts() != null) {
                return FileType.PART.getFileType();
            } else if (root.getHeader() != null || root.getDatasets() != null || root.getElements() != null) { //There are files where root has and Header and DATASET
                return FileType.CONDITION.getFileType();
            } else if (root.getChannelUpdates() != null) {
                return FileType.CHANNEL.getFileType();
            }
        } else if (root.getMaps().getTags() != null) {
            return FileType.CONDITION.getFileType();
        } else if (root.getMaps().getVersionAlias() != null) {
            return FileType.VERSION_ALIAS.getFileType();
        } else if (root.getMaps().getKey() != null) {
            return FileType.KEY.getFileType();
        } else if (root.getMaps().getKeyAlias() != null) {
            return FileType.KEY_ALIAS.getFileType();
        } else if (root.getMaps().getTags() != null) {
            return FileType.CONDITION.getFileType();
        }

        return 999; //undefined file type
    }
}
