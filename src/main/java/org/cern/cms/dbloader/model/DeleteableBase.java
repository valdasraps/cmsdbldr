package org.cern.cms.dbloader.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Type;

@MappedSuperclass
@Setter @Getter
public abstract class DeleteableBase extends EntityBase {

	@Basic
	@Column(name="IS_RECORD_DELETED")
	@Type(type="true_false")
	@XmlTransient
	private Boolean deleted = false;

}
