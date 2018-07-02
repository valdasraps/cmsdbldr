package org.cern.cms.dbloader.model.serial.map;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MapTag extends MapBase {

	@XmlElement(name = "IOV")
	private Set<MapIov> iovs;
	
}
