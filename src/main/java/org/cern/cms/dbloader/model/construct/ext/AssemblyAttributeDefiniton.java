package org.cern.cms.dbloader.model.construct.ext;

import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.model.serial.map.AttrBase;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "ASSEMBLY_ATTRIBUTE_DEFINITIONS", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"AAD_APD_ID", "AAD_ATTRIBUTE_ID", "AAD_SET_STATUS"}))
@Getter @Setter @ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
public class AssemblyAttributeDefiniton {
    
    @Id
    @Column(name = "AAD_ID")
    private BigInteger id;
    
    @ManyToOne
    @JoinColumn(name = "AAD_APD_ID")
    private AssemblyPartDefiniton partDefinition;

    @ManyToOne
    @JoinColumn(name = "AAD_ATTRIBUTE_ID")
    private AttrBase attribute;
    
    @Basic
    @Column(name="AAD_GET_ACTION")
    @Type(type="true_false")
    private Boolean getAction;

    @Basic
    @Column(name="AAD_SET_ACTION")
    @Type(type="true_false")
    private Boolean setAction;
    
    @Basic
    @Column(name = "AAD_SET_STATUS")
    @Enumerated(EnumType.STRING)
    private AssemblyStepStatus stepStatus;

}