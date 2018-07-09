package org.cern.cms.dbloader.model.serial.map;

import javax.xml.bind.annotation.XmlAttribute;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = MapDataset.class, name = "DataSet"),
		@JsonSubTypes.Type(value = MapIov.class, name = "Iov"),
		@JsonSubTypes.Type(value = MapTag.class, name = "Tag")
})
public abstract class MapBase {
	
	@XmlAttribute(name = "idref", required = true)
	@JsonProperty("Idref")
	private Integer refid;
}
