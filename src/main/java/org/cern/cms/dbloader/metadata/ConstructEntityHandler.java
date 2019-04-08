package org.cern.cms.dbloader.metadata;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.model.construct.PartDetailsBase;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

@Getter
@ToString(of = {"name"})
@EqualsAndHashCode(callSuper = false, of = {"id"})
public class ConstructEntityHandler extends EntityHandler<PartDetailsBase> {

    protected final String tname;
    protected final String id;
    protected final String kopName;


    // added id
    public ConstructEntityHandler(String id, String kopName, String schema, String tableName, ResultSetMetaData md) throws Exception {
        super(schema, tableName, md);

        this.tname = tableName;
        this.id = id;
        this.kopName = kopName;

        // Check columns
        PropertyHandler idd = null, partId = null;
        for (PropertyHandler cmd : this.properties) {
            if (cmd.getColumnName().equals("ID")) {
                idd = cmd;
            } else if (cmd.getColumnName().equals("PART_ID")) {
                partId = cmd;
            }
        }

        if (idd == null) {
            throw new SQLException("Mandatory column ID not found?");
        }

        if (partId == null) {
            throw new SQLException("Mandatory column PART_ID not found?");
        }

        if (!Number.class.isAssignableFrom(idd.getPropertyClass())) {
            throw new SQLException("Mandatory column ID is not of correct type?");
        }

        if (!Number.class.isAssignableFrom(partId.getPropertyClass())) {
            throw new SQLException("Mandatory column PART_ID is not of correct type?");
        }

        this.properties.remove(partId);
        this.properties.remove(idd);

    }

    @Override
    public String getSuperClass() {
        return PartDetailsBase.class.getName();
    }

    @Override
    public String getPackage() {
        return PropertiesManager.CONSTRUCT_EXT_PACKAGE;
    }

    @Override
    public EntityFactory<PartDetailsBase> getEntityFactory() {
        return new EntityFactory<PartDetailsBase>() {

            @Override
            public void modifyClass(ClassBuilder cb) {
                cb.newAnnotation(XmlRootElement.class).addMember("name", "PART_EXTENSION").build();
                cb.newAnnotation(JsonRootName.class).addMember("value", "PartExtension").build();
            }
        };
    }
}
