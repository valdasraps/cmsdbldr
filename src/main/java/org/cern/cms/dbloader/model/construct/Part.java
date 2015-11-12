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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.manager.xml.DateAdapter;
import org.cern.cms.dbloader.model.DeleteableBase;
import org.cern.cms.dbloader.model.managemnt.Location;
import org.cern.cms.dbloader.model.xml.map.Attribute;

@Entity
@Table(name = "PARTS", uniqueConstraints = @UniqueConstraint(columnNames = {"BARCODE", "SERIAL_NUMBER", "KIND_OF_PART_ID"}))
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
public class Part extends DeleteableBase {

    @Id
    @Column(name = "PART_ID")
    @XmlElement(name = "PART_ID")
    @GeneratedValue(generator = "ANY_CONSTR_PART_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ANY_CONSTR_PART_ID_SEQ", sequenceName = "ANY_CONSTR_PART_ID_SEQ", allocationSize = 20)
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
    private String manufacturerName;

    @Basic
    @XmlElement(name = "BARCODE")
    @Column(name = "BARCODE")
    private String barcode;

    @Basic
    @Column(name = "SERIAL_NUMBER")
    @XmlElement(name = "SERIAL_NUMBER")
    private String serialNumber;

    @Basic
    @Column(name = "VERSION")
    @XmlElement(name = "VERSION")
    private String version;

    @Basic
    @Column(name = "NAME_LABEL")
    @XmlElement(name = "NAME_LABEL")
    private String name;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INSTALLED_DATE")
    @XmlElement(name = "INSTALLED_DATE")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date installedDate;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REMOVED_DATE")
    @XmlElement(name = "REMOVED_DATE")
    @XmlJavaTypeAdapter(DateAdapter.class)
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
    private List<Attribute> attributes = new ArrayList<>();

    @Transient
    @XmlElement(name = "LOCATION")
    private String locationName;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "LOCATION_ID")
    private Location location;

    @Transient
    @XmlElement(name = "INSTITUTION")
    private String institutionName;

    @Transient
    @XmlElement(name = "RECORD_INSERTION_USER")
    private String insertUser;

    @Transient
    @XmlElementWrapper(name = "CHILDREN")
    @XmlElement(name = "PART", type = Part.class)
    private List<Part> children = new ArrayList<>();

}
