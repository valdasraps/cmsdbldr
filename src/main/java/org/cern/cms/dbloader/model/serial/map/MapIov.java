package org.cern.cms.dbloader.model.xml.map;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MapIov extends MapBase {
	
	@XmlElement(name = "DATA_SET")
	private Set<MapDataset> datasets;
	
}
