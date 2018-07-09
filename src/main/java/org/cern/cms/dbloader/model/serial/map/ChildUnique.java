package org.cern.cms.dbloader.model.serial.map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class ChildUnique {
    
    @XmlElement(name="ATTRIBUTE")
    @JsonProperty("Attribute")
    private Attribute attribute;
    
}
