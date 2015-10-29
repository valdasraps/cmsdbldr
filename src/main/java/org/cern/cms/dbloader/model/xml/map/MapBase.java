package org.cern.cms.dbloader.model.xml.map;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class MapBase {
	
	@XmlAttribute(name = "idref", required = true)
	private Integer refid;
}
