package org.cern.cms.dbloader.manager.file;

import javax.persistence.EnumType;

/**
 * Created by aisi0860 on 5/24/17.
 */
public enum FileType {
    PART(0),
    CHANNEL(1),
    CONDITION(2),
    VERSION_ALIAS(3),
    KEY(4),
    KEY_ALIAS(5);

    private int id;

    FileType(int id){
        this.id = id;
    }

    public int getFileType(){
        return id;
    }
}
