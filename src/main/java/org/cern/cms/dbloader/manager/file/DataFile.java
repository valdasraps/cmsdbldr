package org.cern.cms.dbloader.manager.file;

import com.google.inject.assistedinject.Assisted;
import lombok.Getter;
import lombok.ToString;
import org.cern.cms.dbloader.manager.JsonManager;
import org.cern.cms.dbloader.manager.XmlManager;
import org.cern.cms.dbloader.model.serial.Root;

import javax.inject.Inject;
import javax.management.modelmbean.XMLParseException;
import java.io.File;
import java.util.Collections;
import java.util.List;

@Getter
@ToString(callSuper=true)
public class DataFile extends FileBase implements Comparable<DataFile> {

    private final FileBase archive;
    private final Root root;
    private final DataFileType type;

    @Inject
    public DataFile(XmlManager xmlm, JsonManager jmngr,  @Assisted FileBase archive, @Assisted File file) throws Exception {
        super(file);
        this.archive = archive;
        Root tmpRoot = null;
        try {
            // Handle xml
            tmpRoot = xmlm.unmarshal(file);
        } catch (Exception e) {
            // Handle Json
            tmpRoot = jmngr.deserialize(file);
        }
        this.root = tmpRoot;
        this.type = DataFile.resolveType(this.root);
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
    public int compareTo(DataFile dataFile) {
        return this.type.ordinal() - dataFile.getType().ordinal();
    }

    public static DataFileType resolveType(Root root) throws Exception {

        if (root.getMaps() == null) {
            if (root.getParts() != null) {
                return DataFileType.PART;
            } else if (root.getHeader() != null || root.getDatasets() != null || root.getElements() != null) { //There are files where root has and Header and DATASET
                return DataFileType.CONDITION;
            } else if (root.getChannelUpdates() != null) {
                return DataFileType.CHANNEL;
            }
        } else if (root.getMaps().getTags() != null) {
            return DataFileType.CONDITION;
        } else if (root.getMaps().getVersionAliases() != null) {
            return DataFileType.VERSION_ALIAS;
        } else if (root.getMaps().getKey() != null) {
            return DataFileType.KEY;
        } else if (root.getMaps().getKeyAlias() != null) {
            return DataFileType.KEY_ALIAS;
        } else if (root.getMaps().getTags() != null) {
            return DataFileType.CONDITION;
        }

        throw new XMLParseException(String.format("File type not resolved: %s", root));
    }

}
