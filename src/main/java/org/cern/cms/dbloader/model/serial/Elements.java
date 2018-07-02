package org.cern.cms.dbloader.model.serial;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.Setter;

import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.iov.Iov;
import org.cern.cms.dbloader.model.iov.Tag;

@Getter @Setter
public class Elements {

	@XmlElement(name = "DATA_SET")
	private Set<Dataset> datasets;

	@XmlElement(name = "IOV")
	private Set<Iov> iovs;
	
	@XmlElement(name = "TAG")
	private Set<Tag> tags;
	
}
