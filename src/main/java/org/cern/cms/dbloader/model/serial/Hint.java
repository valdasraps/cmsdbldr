package org.cern.cms.dbloader.model.serial;

import javax.xml.bind.annotation.XmlAttribute;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName("Hint")
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
public class Hint {
	
	@XmlAttribute(name = "channelmap")
	@JsonProperty("ChannelMap")
	private String channelMap;
	
}
