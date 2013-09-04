package org.cern.cms.dbloader.metadata;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.model.condition.ChannelBase;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
public class ChannelEntityHandler extends EntityHandler<ChannelBase> {

	public ChannelEntityHandler(String schema, String tableName, ResultSetMetaData md) throws Exception {
		super(schema, tableName, md);
		
		// Check columns
		PropertyHandler channelMapId = null;
		for (PropertyHandler cmd: this.properties) {
			if (cmd.getColumnName().equals("CHANNEL_MAP_ID")) {
				channelMapId = cmd;
			}	
		}
		
		if (channelMapId == null) throw new SQLException("Mandatory column CHANNEL_MAP_ID not found?");
		
		if (! Number.class.isAssignableFrom(channelMapId.getPropertyClass())) 
			throw new SQLException("Mandatory column CHANNEL_MAP_ID is not of correct type?");

		this.properties.remove(channelMapId);
		
	}

	@Override
	public String getSuperClass() {
		return ChannelBase.class.getName();
	}

	@Override
	public String getPackage() {
		return PropertiesManager.CONDITION_EXT_PACKAGE;
	}
	
	@Override
	public EntityFactory<ChannelBase> getEntityFactory() {
		return new EntityFactory<ChannelBase>() {

			@Override
			public void modifyClass(ClassBuilder cb) {
				cb.newAnnotation(XmlRootElement.class).addMember("name", "CHANNEL").build();
			}
			
		};
	}

}
