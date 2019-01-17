package org.cern.cms.dbloader.model.serial.map;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import org.cern.cms.dbloader.model.config.Key;
import org.cern.cms.dbloader.model.config.KeyAlias;
import org.cern.cms.dbloader.model.config.VersionAlias;


@Getter @Setter
@JsonRootName("Maps")
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Maps {

	@XmlElement(name = "TAG")
	@JsonProperty("Tags")
	private Set<MapTag> tags;

	@XmlElement(name="VERSION_ALIAS")
	@JsonProperty("VersionAliases")
	private Set<VersionAlias> versionAliases;

	@XmlElement(name="KEY")
	@JsonProperty("Key")
	private Set<Key> key;

	@XmlElement(name="KEY_ALIAS")
	@JsonProperty("KeyAlias")
	private Set<KeyAlias> keyAlias;

}
