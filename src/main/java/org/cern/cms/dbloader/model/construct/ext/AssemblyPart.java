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
import javax.persistence.Transient;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.construct.Part;

@Entity
@Table(name = "ASSEMBLY_PARTS", uniqueConstraints = @UniqueConstraint(columnNames = {"ASP_APD_ID","ASP_ASS_ID","ASP_PART_ID"}))
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
//@JsonIgnoreProperties({ "kindOfPart",
//        "partTree",
//        "manufacturer",
//        "insertTime",
//        "lastUpdateTime",
//        "lastUpdateUser",
//        "deleted",
//        "id",
//        "version",
//        "installedUser",
//        "removedUser",
//        "removedDate",
//        "mode",
//        "serialNumber",
//        "location"
//})
//@JsonPropertyOrder({"", ""})
@JsonRootName("ASSEMBLY_PART")
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssemblyPart {
    
    @Id
    @XmlTransient
    @Column(name = "ASP_ID")
    @GeneratedValue(generator = "ASP_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ASP_ID_SEQ", sequenceName = "ASS_ID_SEQ", allocationSize = 20)
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
    private String number;
    
    @ManyToOne
    @JoinColumn(name = "ASP_PART_ID")
    @XmlElement(name = "PART")
    private Part part;

}