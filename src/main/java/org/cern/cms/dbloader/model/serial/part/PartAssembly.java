package org.cern.cms.dbloader.model.serial.part;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.serial.map.Attribute;
import org.cern.cms.dbloader.model.serial.map.ChildUnique;

@Getter @Setter
@XmlAccessorType(XmlAccessType.FIELD)
@ToString
public class PartAssembly {

	@XmlElement(name = "PARENT_PART")
	private Part parentPart;
	
	@XmlElement(name = "CHILD_UNIQUELY_IDENTIFIED_BY")
	private ChildUnique uniqueChild;
        
        @XmlTransient
        public Attribute getAttribute() {
            return (uniqueChild == null ? null : uniqueChild.getAttribute());
        }
	
}
