package org.cern.cms.dbloader.model.construct.ext;

import java.math.BigInteger;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.model.construct.Part;


@Entity
@Table(name = "ASSEMBLY_PART_CURRENT_STEPS")
@Getter @Setter @ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
public class AssemblyCurrentStep {
    
    @Id
    @Column(name = "PART_ID")
    private BigInteger id;
    
    @OneToOne
    @JoinColumn(name = "PART_ID")
    private Part part;
    
    @ManyToOne
    @JoinColumn(name = "APR_ID")
    private AssemblyProcess assemblyProcess;

    @ManyToOne
    @JoinColumn(name = "ASD_ID")
    private AssemblyStepDefiniton stepDefinition;
    
    @OneToOne
    @JoinColumn(name = "ASS_ID")
    private AssemblyStep step;
    
    @Basic
    @Column(name = "ASD_NUMBER")
    private Integer number;
        
}