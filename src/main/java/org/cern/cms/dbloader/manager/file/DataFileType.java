package org.cern.cms.dbloader.manager.file;

import javax.persistence.EnumType;

/**
 * Created by aisi0860 on 5/24/17.
 */
public enum FileType {
    PART,
    CHANNEL,
    CONDITION,
    VERSION_ALIAS,
    KEY,
    KEY_ALIAS;

    private int id;

    FileType(int id){
        this.id = id;
    }

    public int getFileType(){
        return id;
    }
}
