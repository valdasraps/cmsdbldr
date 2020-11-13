package org.cern.cms.dbloader.model.construct;

import java.math.BigInteger;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.DeleteableBase;
import org.cern.cms.dbloader.model.condition.CondToPart;
import org.hibernate.annotations.Type;

@Entity
@Table(name="KINDS_OF_PARTS")
@Getter @Setter
@ToString(of = {"id","name"})
@EqualsAndHashCode(callSuper=false, of={"id"})
public class KindOfPart extends DeleteableBase {
	
	@Id
	@Column(name="KIND_OF_PART_ID")
	private BigInteger id;
	
	@ManyToOne
	@JoinColumn(name="SUBDETECTOR_ID")
	private Subdetector subdetector;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy = "kop")
	private Set<CondToPart> kopToPart;
	
	@Basic
	@Type(type="true_false")
	@Column(name="IS_IMAGINARY_PART")
	private Boolean imaginaryPart;
	
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
