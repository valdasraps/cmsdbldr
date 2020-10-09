package org.cern.cms.dbloader.model.serial;

import java.util.List;

import javax.persistence.Transient;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.construct.ext.Request;
import org.cern.cms.dbloader.model.construct.ext.Shipment;
import org.cern.cms.dbloader.model.serial.map.Maps;
import org.cern.cms.dbloader.model.condition.DatasetRoot;

@Getter
@Setter
@ToString
@XmlRootElement(name = "ROOT")
@JsonRootName(value = "Root")
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Root implements DatasetRoot {

    @Transient
    @XmlAttribute(name = "operator_name")
    private String operatorName;

    // Condition

    @XmlElement(name = "HEADER")
    @JsonProperty("Header")
    private Header header;

    @XmlElement(name = "ELEMENTS")
    @JsonProperty("Elements")
    private Elements elements;

    @XmlElement(name = "MAPS")
    @JsonProperty("Maps")
    private Maps maps;

    @XmlElement(name = "DATA_SET")
    @JsonProperty("Datasets")
    private List<Dataset> datasets;

    // Construct
    
    @XmlElementWrapper(name="PARTS")
    @XmlElement(name="PART", type=Part.class)
    @JsonProperty("Parts")
    private List<Part> parts;

    // Channels
    
    @XmlElement(name="CHANNEL_UPDATE", type=ChannelUpdate.class)
    @JsonProperty("ChannelUpdates")
    private List<ChannelUpdate> channelUpdates;

    // Shipments
    
    @XmlElementWrapper(name="REQUESTS")
    @XmlElement(name="REQUEST", type=Request.class)
    @JsonProperty("Requests")
    private List<Request> requests;

    @XmlElementWrapper(name="SHIPMENTS")
    @XmlElement(name="SHIPMENT", type=Shipment.class)
    @JsonProperty("Shipments")
    private List<Shipment> shipments;
    
}


