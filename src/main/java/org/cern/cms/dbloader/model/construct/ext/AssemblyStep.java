package org.cern.cms.dbloader.model.construct.ext;

import java.math.BigInteger;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.*;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElementWrapper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.EntityBase;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.managemnt.Location;

@Entity
@Table(name = "ASSEMBLY_STEPS", uniqueConstraints = @UniqueConstraint(columnNames = {"ASS_ASD_ID", "ASS_PART_ID"}))
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false, of = {"id"})
@JsonIgnoreProperties({ 
    "id",
    "stepDefinition",
    "location"
})
@JsonPropertyOrder({"", ""})
@JsonRootName("AssemblyStep")
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssemblyStep extends EntityBase {
    
    
    @Id
    @XmlTransient
    @Column(name = "ASS_ID")
    @GeneratedValue(generator = "ANY_ASSEMBLY_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ANY_ASSEMBLY_ID_SEQ", sequenceName = "ANY_ASSEMBLY_ID_SEQ")
    private BigInteger id;
    
    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "ASS_ASD_ID")
    private AssemblyStepDefiniton stepDefinition;
    
    @ManyToOne
    @JoinColumn(name = "ASS_PART_ID")
    @XmlElement(name = "PART")
    @JsonProperty("Product")
    private Part part;
    
    @Transient
    @XmlElement(name = "NUMBER")
    @JsonProperty("Number")
    private Integer number;

    @Basic
    @XmlElement(name = "STATUS")
    @Column(name = "ASS_STATUS")
    @JsonProperty("Status")
    @Enumerated(EnumType.STRING)
    private AssemblyStepStatus status;
    
    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "ASS_LOCATION_ID")
    private Location location;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "step", cascade = CascadeType.ALL)
    @XmlElementWrapper(name = "ASSEMBLY_PARTS")
    @XmlElement(name = "ASSEMBLY_PART", type = AssemblyPart.class)
    @JsonProperty("AssemblyParts")
    private List<AssemblyPart> assemblyParts = new ArrayList<>();
    
}