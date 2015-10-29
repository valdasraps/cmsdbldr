package org.cern.cms.dbloader.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.xml.map.Maps;

@XmlRootElement(name = "ROOT")
@Getter
@Setter
@ToString
public class Root {

	@XmlElement(name = "HEADER")
	private Header header;

	@XmlElement(name = "ELEMENTS")
	private Elements elements;
	
	@XmlElement(name = "MAPS")
	private Maps maps;

	@XmlElement(name = "DATA_SET")
	private List<Dataset> datasets;
	
	@XmlElementWrapper(name="PARTS")
	@XmlElement(name="PART", type=Part.class)
	private List<Part> parts;
	
}
