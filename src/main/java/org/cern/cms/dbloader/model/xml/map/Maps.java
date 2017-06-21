package org.cern.cms.dbloader.model.xml.map;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.Setter;
import org.cern.cms.dbloader.model.config.Key;
import org.cern.cms.dbloader.model.config.KeyAlias;
import org.cern.cms.dbloader.model.config.VersionAlias;


@Getter @Setter
public class Maps {

	@XmlElement(name = "TAG")
	private Set<MapTag> tags;

	@XmlElement(name="VERSION_ALIAS")
	private Set<VersionAlias> versionAliases;

	@XmlElement(name="KEY")
	private Set<Key> key;

	@XmlElement(name="KEY_ALIAS")
	private Set<KeyAlias> keyAlias;

}
