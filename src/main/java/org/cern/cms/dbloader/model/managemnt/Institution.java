package org.cern.cms.dbloader.model.managemnt;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Getter;
import lombok.Setter;

import org.cern.cms.dbloader.model.DeleteableBase;

@Entity
@Table(name="INSTITUTIONS")
@Getter @Setter
public class Institution extends DeleteableBase {
	
	@Id
	@Column(name="INSTITUTION_ID")
        @GeneratedValue(generator = "ANY_INSTITUTION_ID_SEQ", strategy = GenerationType.SEQUENCE)
        @SequenceGenerator(name = "ANY_INSTITUTION_ID_SEQ", sequenceName = "ANY_INSTITUTION_ID_SEQ", allocationSize = 20)
	@XmlTransient
	private BigInteger id;
	
	@Column(name="INSTITUTE_CODE")
	@XmlTransient
	private Integer instituteCode;
	
	@Column(name="NAME")
	@XmlTransient
	private String name;
	
	@Column(name="TOWN")
	@XmlTransient
	private String town;
	
	@Column(name="COUNTRY")
	@XmlTransient
	private String country;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "institution")
	private Set<Location> locations = new HashSet<Location>();
}
