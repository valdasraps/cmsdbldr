package org.cern.cms.dbloader.model.construct;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.manager.xml.DateAdapter;
import org.cern.cms.dbloader.model.DeleteableBase;

@Entity
@Table(name="PARTS")
@Getter @Setter
@ToString
@EqualsAndHashCode(callSuper=false, of={"id"})
public class Part extends DeleteableBase {

	@Id
	@Column(name="PART_ID")
	@XmlTransient
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="KIND_OF_PART_ID")
	@XmlTransient
	private KindOfPart kindOfPart;
	
	@Transient
	@XmlElement(name="KIND_OF_PART")
	private String kindOfPartName;
	
	@Basic
	@Column(name="BARCODE")
	@XmlElement(name="BARCODE")
	private String barcode;
	
	@Basic
	@Column(name="SERIAL_NUMBER")
	@XmlElement(name="SERIAL_NUMBER")
	private String serialNumber;
	
	@Basic
	@Column(name="VERSION")
	@XmlElement(name="VERSION")
	private String version;

	@Basic
	@Column(name="NAME_LABEL")
	@XmlElement(name="NAME_LABEL")
	private String name;

	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "INSTALLED_DATE")
	@XmlElement(name = "INSTALLED_DATE")
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date installedDate;
	
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="REMOVED_DATE")
	@XmlElement(name = "REMOVED_DATE")
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date removedDate;

	@Basic
	@Column(name="INSTALLED_BY_USER")
	@XmlTransient
	private String installedUser;

	@Basic
	@Column(name="REMOVED_BY_USER")
	@XmlTransient
	private String removedUser;
	
}
