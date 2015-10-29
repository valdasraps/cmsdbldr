package org.cern.cms.dbloader.model.construct;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.Type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "PHYSICAL_PARTS_TREE")
@Getter @Setter
@ToString(exclude = "parentPartTree")
@EqualsAndHashCode(of = "partId")
public class PartTree {
    
    @Id
    @Column(name = "PART_ID")
    private BigInteger partId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="PART_PARENT_ID", nullable = false)
    private PartTree parentPartTree;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RELATIONSHIP_ID", nullable = false)
    private PartRelationship relationship;
    
    @Basic
    @Column(name="IS_RECORD_DELETED")
    @Type(type="true_false")
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
