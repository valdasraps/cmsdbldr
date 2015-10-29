package org.cern.cms.dbloader.model.construct;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import org.cern.cms.dbloader.model.DeleteableBase;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="MANUFACTURERS")
@Getter @Setter
@ToString
public class Manufacturer extends DeleteableBase {
    
    @Id
    @Column(name="MANUFACTURER_ID")
    @XmlTransient
    @GeneratedValue(generator = "ANY_MNFCTR_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ANY_MNFCTR_ID_SEQ", sequenceName = "ANY_MNFCTR_ID_SEQ", allocationSize = 20)
    private BigInteger id;
    
    @Column(name="MANUFACTURER_NAME")
    private String name;
    
}
