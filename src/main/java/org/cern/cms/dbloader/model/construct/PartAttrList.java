package org.cern.cms.dbloader.model.construct;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;
import lombok.EqualsAndHashCode;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.serial.map.AttrBase;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PART_ATTR_LISTS")
@Getter @Setter
@EqualsAndHashCode(callSuper = false, of = {"id"})
@ToString(of = {"id","part","attrBase","deleted"})
public class PartAttrList {

    @Id
    @GeneratedValue(generator = "ANY_ATTR_LIST_REC_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ANY_ATTR_LIST_REC_ID_SEQ",  sequenceName = "ANY_ATTR_LIST_REC_ID_SEQ", allocationSize = 20)
    @Column(name = "ATTR_LIST_RECORD_ID")
    @XmlTransient
    private BigInteger id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTRIBUTE_ID", nullable = false)
    @XmlTransient
    private AttrBase attrBase;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PART_ID", nullable = false)
    @XmlTransient
    private Part part;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "RELATIONSHIP_ID", nullable = false)
    @XmlTransient
    private PartToAttrRltSh partToAttrRtlSh;
    
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
    
}
