package org.cern.cms.dbloader.model.condition;

import java.math.BigInteger;
import java.util.Date;

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

import lombok.Getter;
import lombok.Setter;

import org.cern.cms.dbloader.model.xml.map.AttrBase;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "COND_ATTR_LISTS")
@SequenceGenerator(name = "ANY_ATTR_LIST_REC_ID_SEQ", schema="CMS_GEM_CORE_ATTRIBUTE", sequenceName = "ANY_ATTR_LIST_REC_ID_SEQ", allocationSize = 20)
@Getter @Setter
public class CondAttrList {
      
    @Id
    @GeneratedValue(generator = "ANY_ATTR_LIST_REC_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @Column(name="ATTR_LIST_RECORD_ID")
    private BigInteger id;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "RELATIONSHIP_ID", nullable = false)
    private CondToAttrRltSh condToAttrRtlSh;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTRIBUTE_ID", nullable = false)
    private AttrBase attrBase;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="CONDITION_DATA_SET_ID", nullable=false)
    private Dataset dataset;
    
    @Column(name="IS_RECORD_DELETED")
    @Type(type="true_false")
    private Boolean deleted = false;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="RECORD_INSERTION_TIME", nullable=false)
    private Date insertTime;
    
    @Column(name="RECORD_INSERTION_USER", nullable=false)
    private String insertUser;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="RECORD_DEL_FLAG_TIME", nullable=false)
    private Date insertTimeFlag;
    
    @Column(name="RECORD_DEL_FLAG_USER", nullable=false)
    private String insertUserFlag;
    
}
