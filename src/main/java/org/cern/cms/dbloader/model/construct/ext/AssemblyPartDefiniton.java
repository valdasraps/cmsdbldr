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
import org.cern.cms.dbloader.model.construct.KindOfPart;
import org.hibernate.annotations.Type;


@Entity
@Table(name = "ASSEMBLY_PART_DEFINITIONS", uniqueConstraints = @UniqueConstraint(columnNames = {"APD_ASD_ID", "APD_NAME"}))
@Getter @Setter @ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
public class AssemblyPartDefiniton {
    
    public static enum AssemblyPartType {

        PRODUCT,
        COMPONENT,
        JIG

    }

    @Id
    @Column(name = "APD_ID")
    private BigInteger id;
    
    @Basic
    @Column(name = "APD_NUMBER")
    private Integer number;
    
    @ManyToOne
    @JoinColumn(name = "APD_ASD_ID")
    private AssemblyStepDefiniton assemblyStepDefinition;

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

}