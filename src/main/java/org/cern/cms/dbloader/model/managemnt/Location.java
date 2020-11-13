package org.cern.cms.dbloader.model.managemnt;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import org.cern.cms.dbloader.model.DeleteableBase;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="LOCATIONS")
@Getter @Setter
@ToString(of = {"id", "name"})
public class Location extends DeleteableBase {
   
	@Id
	@Column(name="LOCATION_ID")
        @GeneratedValue(generator = "ANY_LOCATION_ID_SEQ", strategy = GenerationType.SEQUENCE)
        @SequenceGenerator(name = "ANY_LOCATION_ID_SEQ", sequenceName = "ANY_LOCATION_ID_SEQ", allocationSize = 20)
	@XmlTransient
	private BigInteger id;
	
	@XmlTransient
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="INSTITUTION_ID", nullable=false)
	private Institution institution;
	
	@Column(name = "LOCATION_NAME")
	@XmlTransient
	private String name;
	
}
