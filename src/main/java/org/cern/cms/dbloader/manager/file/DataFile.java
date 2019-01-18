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
    private final Type fileType;

    public enum Type {
        XMA,
        XML,
        JSON
    }

    @Inject
    public DataFile(XmlManager xmlm, JsonManager jmngr, @Assisted FileBase archive, @Assisted File file, @Assisted Type type) throws Exception {
        super(file);
        this.archive = archive;
        Root tmpRoot;
        if (type == Type.JSON) {
            tmpRoot = jmngr.deserialize(file);
        } else {
            // XMA or XML
            tmpRoot = xmlm.unmarshal(file);
        }
        this.fileType = type;
        this.root = tmpRoot;
        this.type = DataFile.resolveType(this.root);
    }

    @Override
    public boolean isArchive() {
        return false;
    }

    @Override
    public List<DataFile> getDataFiles() { 
        return Collections.singletonList(this);
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
                
            } else if (root.getRequests() != null) {
            
                return DataFileType.REQUEST;

            } else if (root.getShipments() != null) {

                return DataFileType.SHIPMENT;

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
