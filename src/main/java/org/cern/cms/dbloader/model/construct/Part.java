package org.cern.cms.dbloader.model.construct;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.manager.xml.DateAdapter;
import org.cern.cms.dbloader.manager.xml.PartDetailsBaseProxyAdapter;
import org.cern.cms.dbloader.model.DeleteableBase;
import org.cern.cms.dbloader.model.managemnt.Location;
import org.cern.cms.dbloader.model.serial.map.Attribute;

@Entity
@Table(name = "PARTS", uniqueConstraints = @UniqueConstraint(columnNames = {"BARCODE", "SERIAL_NUMBER", "KIND_OF_PART_ID"}))
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
@JsonIgnoreProperties({ "kindOfPart",
        "partTree",
        "manufacturer",
        "insertTime",
        "lastUpdateTime",
        "lastUpdateUser",
        "deleted",
        "id",
        "version",
        "installedUser",
        "removedUser",
        "removedDate",
        "mode",
        "serialNumber",
        "location"
})
//@JsonPropertyOrder({"", ""})
@JsonRootName("PART")
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Part extends DeleteableBase {

    @Id
    @Column(name = "PART_ID")
    @XmlElement(name = "PART_ID")
    @GeneratedValue(generator = "ANY_CONSTR_PART_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ANY_CONSTR_PART_ID_SEQ", sequenceName = "ANY_CONSTR_PART_ID_SEQ", allocationSize = 20)
    @JsonProperty("Id")
    private BigInteger id;

    @Transient
    @XmlAttribute(name = "mode")
    private String mode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KIND_OF_PART_ID")
    @XmlTransient
    private KindOfPart kindOfPart;

    @Transient
    @XmlElement(name = "KIND_OF_PART")
    // @JsonProperty("KIND_OF_PART")
    @JsonProperty("KindOfPart")
    private String kindOfPartName;

    @XmlTransient
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private PartTree partTree;
    
    @XmlTransient
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "MANUFACTURER_ID", nullable = false)
    private Manufacturer manufacturer;

    @Transient
    @XmlElement(name = "MANUFACTURER")
    //@JsonProperty("MANUFACTURER")
    @JsonProperty("Manufacturer")
    private String manufacturerName;

    @Basic
    @XmlElement(name = "BARCODE")
    @Column(name = "BARCODE")
    // @JsonProperty("BARCODE")
    @JsonProperty("Barcode")
    private String barcode;

    @Basic
    @Column(name = "SERIAL_NUMBER")
    @XmlElement(name = "SERIAL_NUMBER")
    // @JsonProperty("SERIAL_NUMBER")
    @JsonProperty("SerialNumber")
    private String serialNumber;

    @Basic
    @Column(name = "VERSION")
    @XmlElement(name = "VERSION")
    // @JsonProperty("VERSION")
    @JsonProperty("Version")
    private String version;

    @Basic
    @Column(name = "NAME_LABEL")
    @XmlElement(name = "NAME_LABEL")
    // @JsonProperty("NAME_LABEL")
    @JsonProperty("NameLabel")
    private String name;
    
    @Basic
    @Temporal(TemporalType.DATE)
    @Column(name = "PRODUCTION_DATE")
    @XmlElement(name = "PRODUCTION_DATE")
    @XmlJavaTypeAdapter(DateAdapter.class)
    @JsonProperty("ProductionDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone="UTC")
    private Date productionDate;

    @Basic
    @Column(name = "BATCH_NUMBER")
    @XmlElement(name = "BATCH_NUMBER")
    // @JsonProperty("NAME_LABEL")
    @JsonProperty("BatchNumber")
    private String batchNumber;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INSTALLED_DATE")
    @XmlElement(name = "INSTALLED_DATE")
    @XmlJavaTypeAdapter(DateAdapter.class)
    // @JsonProperty("INSTALLED_DATE")
    @JsonProperty("InstalledDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="UTC")
    private Date installedDate;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REMOVED_DATE")
    @XmlElement(name = "REMOVED_DATE")
    // @JsonProperty("REMOVED_DATE")
    @JsonProperty("RemovedDate")
    @XmlJavaTypeAdapter(DateAdapter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="UTC")
    private Date removedDate;

    @Basic
    @Column(name = "INSTALLED_BY_USER")
    @XmlTransient
    private String installedUser;

    @Basic
    @Column(name = "REMOVED_BY_USER")
    @XmlTransient
    private String removedUser;

    @Transient
    @XmlElementWrapper(name = "PREDEFINED_ATTRIBUTES")
    @XmlElement(name = "ATTRIBUTE", type = Attribute.class)
    // @JsonProperty("PREDEFINED_ATTRIBUTES")
    @JsonProperty("PredifinedAttributes")
    private List<Attribute> attributes = new ArrayList<>();

    @Transient
    @XmlElement(name = "LOCATION")
    // @JsonProperty("LOCATION")
    @JsonProperty("Location")
    private String locationName;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "LOCATION_ID")
    private Location location;

    @Transient
    @XmlElement(name = "INSTITUTION")
    // @JsonProperty("INSTITUTION")
    @JsonProperty("Institution")
    private String institutionName;

    @Transient
    @XmlElement(name = "RECORD_INSERTION_USER")
    // @JsonProperty("RECORD_INSERTION_USER")
    @JsonProperty("RecordInsertUser")
    private String insertUser;

    @Transient
    @XmlElementWrapper(name = "CHILDREN")
    @XmlElement(name = "PART", type = Part.class)
    // @JsonProperty("CHILDREN")
    @JsonProperty("Children")
    private List<Part> children = new ArrayList<>();

    @Transient
    @XmlElement(name = "PART_EXTENSION")
    @XmlJavaTypeAdapter(value = PartDetailsBaseProxyAdapter.class)
    private PartDetailsBase partDetails;

    public void addChild(Part part) {
        this.children.add(part);
    }

    public void addAttributes(Attribute atr) {
        this.attributes.add(atr);
    }

}