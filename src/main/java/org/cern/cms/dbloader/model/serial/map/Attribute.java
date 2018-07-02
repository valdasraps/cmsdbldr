package org.cern.cms.dbloader.model.xml.map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class Attribute {
    
    @XmlElement(name="NAME")
    private String name;
    
    @XmlElement(name="VALUE")
    private String value;

}
