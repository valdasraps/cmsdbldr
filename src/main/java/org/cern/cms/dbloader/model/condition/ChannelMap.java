package org.cern.cms.dbloader.model.condition;

import java.math.BigInteger;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.Type;

@Entity
@Table(name="CHANNEL_MAPS_BASE")
@Getter @Setter
@ToString
@EqualsAndHashCode(callSuper=false, of={"id"})
public class ChannelMap {

	@Id
	@Column(name="CHANNEL_MAP_ID")
	@XmlTransient
	private BigInteger id;
	
	@Basic
	@Column(name="IS_RECORD_DELETED")
	@Type(type="true_false")
	@XmlTransient
	private Boolean deleted = false;
	
	@Basic
	@Column(name="EXTENSION_TABLE_NAME")
	@XmlElement(name="EXTENSION_TABLE_NAME")
	private String extensionTableName;
	
}
