package org.cern.cms.dbloader.model.construct;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.DeleteableBase;
import org.hibernate.annotations.Type;

@Entity
@Table(name="KINDS_OF_PARTS")
@Getter @Setter
@ToString
@EqualsAndHashCode(callSuper=false, of={"id"})
public class KindOfPart extends DeleteableBase {
	
	@Id
	@Column(name="KIND_OF_PART_ID")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="SUBDETECTOR_ID")
	private Subdetector subdetector;
	
	@Basic
	@Type(type="true_false")
	@Column(name="IS_IMAGINARY_PART")
	private Boolean imaginaryPart;
	
	@Basic
	@Column(name="DISPLAY_NAME")
	private String name;
	
	@Basic
	@Type(type="true_false")
	@Column(name="IS_DETECTOR_PART")
	private Boolean detectorPart;
	
	@Basic
	@Column(name="EXTENSION_TABLE_NAME")
	private String extensionTable;

	@Basic
	@Column(name="LPNAME")
	private String lpName;
	
}
