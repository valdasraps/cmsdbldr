package org.cern.cms.dbloader.model.serial;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;

import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.iov.Iov;
import org.cern.cms.dbloader.model.iov.Tag;

@Getter @Setter
@JsonRootName("Elements")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Elements {

	@XmlElement(name = "DATA_SET")
	@JsonProperty("DataSets")
	private Set<Dataset> datasets;

	@XmlElement(name = "IOV")
	@JsonProperty("Iovs")
	private Set<Iov> iovs;
	
	@XmlElement(name = "TAG")
	@JsonProperty("Tags")
	private Set<Tag> tags;
	
}
