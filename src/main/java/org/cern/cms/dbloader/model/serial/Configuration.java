package org.cern.cms.dbloader.model.serial;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.KindOfCondition;
import org.cern.cms.dbloader.model.construct.Part;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by aisi0860 on 5/26/17.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
public class Configuration {

    @XmlElement(name="PART")
    private Part part;

    @XmlElement(name="KIND_OF_CONDITION")
    private KindOfCondition koc;

    @XmlElement(name = "DATA_SET")
    private Dataset dataset;

}
