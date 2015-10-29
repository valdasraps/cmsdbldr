package org.cern.cms.dbloader.model.iov;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.annotations.Type;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Setter @Getter
public abstract class IovBase {

	@Basic
	@Column(name="IS_RECORD_DELETED")
	@Type(type="true_false")
	@XmlTransient
	private Boolean deleted = false;
	
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
	
}
