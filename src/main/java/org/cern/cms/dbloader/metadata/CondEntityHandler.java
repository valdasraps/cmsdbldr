package org.cern.cms.dbloader.metadata;

import java.math.BigInteger;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.model.condition.CondBase;

@Getter
@ToString(of = {"name"})
@EqualsAndHashCode(callSuper = false, of = {"id"})
public class CondEntityHandler extends EntityHandler<CondBase> {

    protected final BigInteger id;
    protected final String name;

    // added id
    public CondEntityHandler(BigInteger id, String name, String schema, String tableName, ResultSetMetaData md) throws Exception {
        super(schema, tableName, md);

        this.id = id;
        this.name = name;

        // Check columns
        PropertyHandler recordId = null, conditionDataSetId = null;
        for (PropertyHandler cmd : this.properties) {
            if (cmd.getColumnName().equals("RECORD_ID")) {
                recordId = cmd;
            } else if (cmd.getColumnName().equals("CONDITION_DATA_SET_ID")) {
                conditionDataSetId = cmd;
            }
        }

        if (recordId == null) {
            throw new SQLException("Mandatory column RECORD_ID not found?");
        }
        if (conditionDataSetId == null) {
            throw new SQLException("Mandatory column CONDITION_DATA_SET_ID not found?");
        }

        if (!Number.class.isAssignableFrom(recordId.getPropertyClass())) {
            throw new SQLException("Mandatory column RECORD_ID is not of correct type?");
        }

        if (!Number.class.isAssignableFrom(conditionDataSetId.getPropertyClass())) {
            throw new SQLException("Mandatory column CONDITION_DATA_SET_ID is not of correct type?");
        }

        this.properties.remove(recordId);
        this.properties.remove(conditionDataSetId);

    }

    @Override
    public String getSuperClass() {
        return CondBase.class.getName();
    }

    @Override
    public String getPackage() {
        return PropertiesManager.CONDITION_EXT_PACKAGE;
    }

    @Override
    public EntityFactory<CondBase> getEntityFactory() {
        return new EntityFactory<CondBase>() {

            @Override
            public void modifyClass(ClassBuilder cb) {
                cb.newAnnotation(XmlRootElement.class).addMember("name", "DATA").build();
            }

        };
    }

}
