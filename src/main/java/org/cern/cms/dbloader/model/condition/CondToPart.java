package org.cern.cms.dbloader.model.condition;

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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.DeleteableBase;
import org.cern.cms.dbloader.model.construct.KindOfPart;

@Entity
@Table(name = "COND_TO_PART_RLTNSHPS")
@SequenceGenerator(name = "ANY_RLTNSHP_HISTORY_ID_SEQ", sequenceName = "ANY_RLTNSHP_HISTORY_ID_SEQ", allocationSize = 20)
@Getter @Setter
@ToString(exclude = {"kop", "koc"})
public class CondToPart extends DeleteableBase {
    
    @Id
    @GeneratedValue(generator = "ANY_RLTNSHP_HISTORY_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @Column(name = "RELATIONSHIP_ID")
    private BigInteger id;
    
    @Column(name = "DISPLAY_NAME")
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KIND_OF_PART_ID", nullable=false)
    private KindOfPart kop;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KIND_OF_CONDITION_ID", nullable=false)
    private KindOfCondition koc;
    
}
