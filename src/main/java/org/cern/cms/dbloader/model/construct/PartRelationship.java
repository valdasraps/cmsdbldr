package org.cern.cms.dbloader.model.construct;

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

import org.cern.cms.dbloader.model.DeleteableBase;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "PART_TO_PART_RLTNSHPS")
@SequenceGenerator(name = "ANY_RLTNSHP_ID_SEQ", schema = "CMS_GEM_CORE_ATTRIBUTE",  sequenceName = "ANY_RLTNSHP_ID_SEQ", allocationSize = 20)
@Getter @Setter
@ToString(exclude = {"partKop", "parentKop"})
public class PartRelationship extends DeleteableBase {

    @Id
    @Column(name ="RELATIONSHIP_ID")
    @GeneratedValue(generator = "ANY_RLTNSHP_ID_SEQ", strategy = GenerationType.SEQUENCE)
    private BigInteger id;
    
    @Column(name = "PRIORITY_NUMBER")
    private Integer priority;
    
    @Column(name = "DISPLAY_NAME")
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="KIND_OF_PART_ID_CHILD")
    private KindOfPart partKop;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="KIND_OF_PART_ID")
    private  KindOfPart parentKop;

}   
  
