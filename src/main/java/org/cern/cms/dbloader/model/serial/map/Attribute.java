package org.cern.cms.dbloader.model.serial.map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@JsonRootName("Attribute")
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
public class Attribute {
    
    @XmlElement(name="NAME")
    // @JsonProperty("NAME")
    @JsonProperty("Name")
    private String name;
    
    @XmlElement(name="VALUE")
    // @JsonProperty("VALUE")
    @JsonProperty("Value")
    private String value;

}