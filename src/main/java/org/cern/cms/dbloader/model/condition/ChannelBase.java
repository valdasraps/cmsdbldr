package org.cern.cms.dbloader.model.condition;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@Getter @Setter
@ToString
@EqualsAndHashCode(callSuper=false, of={"id"})
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ChannelBase {

	@Id
	@Column(name="CHANNEL_MAP_ID")
	@XmlTransient
	private Long id;
	
	@Transient
	@XmlElement(name = "EXTENSION_TABLE_NAME")
	private String extensionTableName;
	
}
