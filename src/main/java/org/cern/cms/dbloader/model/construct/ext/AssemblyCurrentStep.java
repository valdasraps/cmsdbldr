package org.cern.cms.dbloader.model.construct.ext;

import java.math.BigInteger;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ASSEMBLY_PART_CURRENT_STEPS")
@Getter @Setter @ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
public class AssemblyCurrentStep {
    
    @Id
    @Column(name = "PART_ID")
    private BigInteger partId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APR_ID")
    private AssemblyProcess assemblyProcess;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASD_ID")
    private AssemblyStepDefiniton stepDefinition;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASS_ID")
    private AssemblyStep step;
    
    @Basic
    @Column(name = "ASD_NUMBER")
    private Integer number;
        
}