package org.cern.cms.dbloader.model.condition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.model.serial.Elements;
import org.cern.cms.dbloader.model.serial.Header;
import org.cern.cms.dbloader.model.serial.map.Maps;

@Getter
@Setter
@ToString
@JsonRootName(value = "ChildDataset")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChildDatasetRoot implements DatasetRoot {
    
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
    
}
