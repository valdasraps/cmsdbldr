package org.cern.cms.dbloader.model.construct.ext;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "ASSEMBLY_STEP_DEFINITIONS", uniqueConstraints = @UniqueConstraint(columnNames = {"ASD_APR_ID", "ASD_NAME"}))
@Getter @Setter @ToString(exclude = {"partDefinitions"})
@EqualsAndHashCode(callSuper = false, of = {"id"})
public class AssemblyStepDefiniton {
    
    @Id
    @Column(name = "ASD_ID")
    private BigInteger id;
    
    @Basic
    @Column(name = "ASD_NUMBER")
    private Integer number;
    
    @ManyToOne
    @JoinColumn(name = "ASD_APR_ID")
    private AssemblyProcess process;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "stepDefinition", cascade = CascadeType.ALL)
    private List<AssemblyPartDefiniton> partDefinitions;

    @Basic
    @Column(name = "ASD_NAME")
    private String name;
    
    @Basic
    @Column(name = "ASD_DESCR")
    private String description;

}