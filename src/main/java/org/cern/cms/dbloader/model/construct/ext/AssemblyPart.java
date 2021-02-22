package org.cern.cms.dbloader.model.construct.ext;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.*;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElementWrapper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.construct.Part;

@Entity
@Table(name = "ASSEMBLY_PARTS", uniqueConstraints = @UniqueConstraint(columnNames = {"ASP_APD_ID","ASP_ASS_ID","ASP_PART_ID"}))
@Getter
@Setter
@ToString(exclude = {"step","assemblyData"})
@EqualsAndHashCode(callSuper = false, of = {"id"})
@JsonIgnoreProperties({ 
    "id",
    "step",
    "partDefinition",
    "productType",
    "componentType",
    "jigType"
})
@JsonPropertyOrder({"", ""})
@JsonRootName("AssemblyPart")
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssemblyPart {
    
    @Id
    @XmlTransient
    @Column(name = "ASP_ID")
    @GeneratedValue(generator = "ANY_ASSEMBLY_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ANY_ASSEMBLY_ID_SEQ", sequenceName = "ANY_ASSEMBLY_ID_SEQ", allocationSize = 20)
    private BigInteger id;
    
    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "ASP_ASS_ID")
    private AssemblyStep step;
    
    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "ASP_APD_ID")
    private AssemblyPartDefiniton partDefinition;
    
    @Transient
    @XmlElement(name = "NUMBER")
    @JsonProperty("Number")
    private Integer number;
    
    @ManyToOne
    @JoinColumn(name = "ASP_PART_ID")
    @XmlElement(name = "PART")
    @JsonProperty("Part")
    private Part part;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assemblyPart", cascade = CascadeType.ALL)
    @XmlElementWrapper(name = "ASSEMBLY_DATA")
    @XmlElement(name = "ASSEMBLY_DATA", type = AssemblyData.class)
    @JsonProperty("AssemblyData")
    private List<AssemblyData> assemblyData = new ArrayList<>();
    
    @Transient
    @XmlTransient
    public boolean isProductType() {
        return partDefinition.isProductType();
    }
    
    @Transient
    @XmlTransient
    public boolean isComponentType() {
        return partDefinition.isComponentType();
    }

    @Transient
    @XmlTransient
    public boolean isJigType() {
        return partDefinition.isJigType();
    }
    
    public AssemblyData findAssemblyData(AssemblyDataDefiniton dataDef) {
        if (dataDef.getPartDefinition().equals(getPartDefinition())) {
            for (AssemblyData adata: getAssemblyData()) {
                if (adata.getDataDefinition().equals(dataDef)) {
                    return adata;
                }
            }
        }
        return null;
    }

}