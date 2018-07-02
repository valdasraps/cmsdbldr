package org.cern.cms.dbloader.model.serial.map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class ChildUnique {
    
    @XmlElement(name="ATTRIBUTE")
    private Attribute attribute;
    
}
