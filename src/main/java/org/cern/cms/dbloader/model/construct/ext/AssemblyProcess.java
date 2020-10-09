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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.OneToOne;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.construct.KindOfPart;

@Entity
@Table(name = "ASSEMBLY_PROCESSES", uniqueConstraints = @UniqueConstraint(columnNames = {"APR_NAME"}))
@Getter @Setter @ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
public class AssemblyProcess {
    
    @Id
    @Column(name = "APR_ID")
    private BigInteger id;
    
    @OneToOne
    @JoinColumn(name = "APR_PRODUCT_KOP_ID")
    private KindOfPart kindOfPart;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assemblyProcess", cascade = CascadeType.ALL)
    private List<AssemblyStepDefiniton> steps;
    
    @Basic
    @Column(name = "APR_NAME")
    private String name;
    
    @Basic
    @Column(name = "APR_DESCR")
    private String description;

}