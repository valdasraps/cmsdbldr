package org.cern.cms.dbloader.model.construct.ext;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.model.construct.KindOfPart;
import org.hibernate.annotations.Type;


@Entity
@Table(name = "ASSEMBLY_PART_DEFINITIONS", uniqueConstraints = @UniqueConstraint(columnNames = {"APD_ASD_ID", "APD_NAME"}))
@Getter @Setter @ToString(exclude = {"attributeDefinitions","dataDefinitions"})
@EqualsAndHashCode(callSuper = false, of = {"id"})
public class AssemblyPartDefiniton {
    
    @Id
    @Column(name = "APD_ID")
    private BigInteger id;
    
    @Basic
    @Column(name = "APD_NUMBER")
    private Integer number;
    
    @ManyToOne
    @JoinColumn(name = "APD_ASD_ID")
    private AssemblyStepDefiniton stepDefinition;

    @ManyToOne
    @JoinColumn(name = "APD_KOP_ID")
    private KindOfPart kindOfPart;
    
    @Basic
    @Column(name = "APD_TYPE")
    @Enumerated(EnumType.STRING)
    private AssemblyPartType type;
    
    @Basic
    @Column(name="APD_IS_NEW")
    @Type(type="true_false")
    private Boolean newPart;
    
    @Basic
    @Column(name = "APD_NAME")
    private String name;
    
    @Basic
    @Column(name = "APD_DESCR")
    private String description;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "partDefinition", cascade = CascadeType.ALL)
    private List<AssemblyAttributeDefiniton> attributeDefinitions;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "partDefinition", cascade = CascadeType.ALL)
    private List<AssemblyDataDefiniton> dataDefinitions;
    
    @Transient
    public boolean isProductType() {
        return type == AssemblyPartType.PRODUCT;
    }
    
    @Transient
    public boolean isComponentType() {
        return type == AssemblyPartType.COMPONENT;
    }

    @Transient
    public boolean isJigType() {
        return type == AssemblyPartType.JIG;
    }

}