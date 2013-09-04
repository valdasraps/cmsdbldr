package org.cern.cms.dbloader.model.construct;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.DeleteableBase;

@Entity
@Table(name="SUBDETECTORS")
@Getter @Setter
@ToString
@EqualsAndHashCode(callSuper=false, of={"id"})
public class Subdetector extends DeleteableBase {

	@Id
	@Column(name="SUBDETECTOR_ID")
	private Long id;

	@Basic
	@Column(name="SUBDETECTOR_NAME")
	private String name;
	
}
