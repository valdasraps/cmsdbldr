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
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.model.condition.Dataset;


@Entity
@Table(name = "ASSEMBLY_DATA", uniqueConstraints = @UniqueConstraint(columnNames = {"AED_ADD_ID","AED_ASP_ID","AED_DATA_SET_ID"}))
@Getter
@Setter
@ToString(exclude = {"assemblyPart"})
@EqualsAndHashCode(callSuper = false, of = {"id"})
@JsonIgnoreProperties({ 
    "id",
    "step",
    "dataDefinition",
    "dataset"
})
@JsonPropertyOrder({"", ""})
@JsonRootName("AssemblyData")
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssemblyData {
    
    @Id
    @XmlTransient
    @Column(name = "AED_ID")
    @GeneratedValue(generator = "AED_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "AED_ID_SEQ", sequenceName = "AED_ID_SEQ", allocationSize = 20)
    private BigInteger id;
    
    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "AED_ASP_ID")
    private AssemblyPart assemblyPart;
    
    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "AED_ADD_ID")
    private AssemblyDataDefiniton dataDefinition;
    
    @Transient
    @XmlElement(name = "NUMBER")
    @JsonProperty("Number")
    private Integer number;

    @Transient
    @XmlElement(name = "DATA_FILE_NAME")
    @JsonProperty("DataFilename")
    private String dataFilename;
    
    @XmlTransient
    @OneToOne
    @JoinColumn(name = "AED_DATA_SET_ID")
    private Dataset dataset;

}