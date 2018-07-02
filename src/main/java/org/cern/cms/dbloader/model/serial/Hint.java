package org.cern.cms.dbloader.model.serial;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class Hint {
	
	@XmlAttribute(name = "channelmap")
	private String channelMap;
	
}
