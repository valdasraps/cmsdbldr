package org.cern.cms.dbloader.model.serial.part;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class ParentPart {

	@XmlElement(name = "KIND_OF_PART")
	private String kindOfPart;
	
	@XmlElement(name = "SERIAL_NUMBER")
	private String serialNumber;
	
}
