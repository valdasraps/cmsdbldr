package org.cern.cms.dbloader.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Setter @Getter
@JsonIgnoreProperties({ "insertTime", "insertUser", "lastUpdateTime", "lastUpdateUser"})
public abstract class EntityBase {

	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="RECORD_INSERTION_TIME", nullable=false)
	@XmlTransient
	private Date insertTime;
	
	@Basic
	@Column(name="RECORD_INSERTION_USER", nullable=false)
	@XmlTransient
	private String insertUser;

	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="RECORD_LASTUPDATE_TIME")
	@XmlTransient
	private Date lastUpdateTime;
	
	@Basic
	@Column(name="RECORD_LASTUPDATE_USER")
	@XmlTransient
	private String lastUpdateUser;
	
	@Basic
	@Column(name="COMMENT_DESCRIPTION")
	@XmlElement(name="COMMENT_DESCRIPTION")
	@JsonProperty("CommentDescription")
	private String comment;
	
}
