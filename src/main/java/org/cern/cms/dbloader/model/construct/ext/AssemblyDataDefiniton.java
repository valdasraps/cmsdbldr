package org.cern.cms.dbloader.model.construct.ext;

import java.math.BigInteger;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.model.condition.KindOfCondition;


@Entity
@Table(name = "ASSEMBLY_DATA_DEFINITIONS", uniqueConstraints = @UniqueConstraint(columnNames = {"ADD_NAME", "ADD_APD_ID"}))
@Getter @Setter @ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
public class AssemblyDataDefiniton {
    
    @Id
    @Column(name = "ADD_ID")
    private BigInteger id;
    
    @Basic
    @Column(name = "ADD_NUMBER")
    private Integer number;
    
    @ManyToOne
    @JoinColumn(name = "ADD_APD_ID")
    private AssemblyPartDefiniton assemblyPartDefinition;

    @ManyToOne
    @JoinColumn(name = "ADD_KOC_ID")
    private KindOfCondition kindOfCondition;

    @Basic
    @Column(name = "ADD_NAME")
    private String name;
    
    @Basic
    @Column(name = "ADD_DESCR")
    private String description;

}