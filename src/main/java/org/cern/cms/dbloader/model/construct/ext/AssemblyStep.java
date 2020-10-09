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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.EntityBase;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.managemnt.Location;

@Entity
@Table(name = "ASSEMBLY_STEPS", uniqueConstraints = @UniqueConstraint(columnNames = {"ASS_ASD_ID", "ASS_PRODUCT_ID"}))
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
@JsonRootName("ASSEMBLY_STEP")
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssemblyStep extends EntityBase {
    
    @Id
    @XmlTransient
    @Column(name = "ASS_ID")
    @GeneratedValue(generator = "ASS_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ASS_ID_SEQ", sequenceName = "ASS_ID_SEQ", allocationSize = 20)
    private BigInteger id;
    
    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "ASS_ASD_ID")
    private AssemblyStepDefiniton stepDefinition;
    
    @ManyToOne
    @JoinColumn(name = "ASS_PART_ID")
    @XmlElement(name = "PART")
    private Part part;
    
    @Transient
    @XmlElement(name = "NUMBER")
    @JsonProperty("Number")
    private Integer number;

    @Basic
    @XmlElement(name = "STATUS")
    @Column(name = "ASS_STATUS")
    @JsonProperty("Status")
    private String status;
    
    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "LOCATION_ID")
    private Location location;

}