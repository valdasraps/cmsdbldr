package org.cern.cms.dbloader.model.xml.part;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.Setter;

import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.xml.map.ChildUnique;

@Getter @Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class PartAssembly {

	@XmlElement(name = "PARENT_PART")
	private Part parentPart;
	
	@XmlElement(name = "CHILD_UNIQUELY_IDENTIFIED_BY")
	private ChildUnique attribute;
	
}
