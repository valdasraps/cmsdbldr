package org.cern.cms.dbloader.model.construct.ext;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.manager.xml.DateAdapter;
import org.cern.cms.dbloader.model.EntityBase;
import org.cern.cms.dbloader.model.managemnt.Location;
import org.hibernate.annotations.Type;

/**
 * Tracking Request model class.
 * @author valdo
 */
@Entity
@Table(name = "SHIPMENTS", uniqueConstraints = @UniqueConstraint(columnNames = {"SHP_TRACKING_NUMBER"}))
@Getter @Setter @ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
@JsonIgnoreProperties({ "id", "fromLocation", "toLocation" })
@JsonRootName("SHIPMENT")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class Shipment extends EntityBase {
    
    public static enum ShipmentStatus {
        
        PACKAGING,
        SHIPPED,
        RECEIVED,
        CANCELED
        
    }
    
    @Id
    @Column(name = "SHP_ID")
    @XmlElement(name = "ID")
    @GeneratedValue(generator = "ANY_SHIPMENTS_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ANY_SHIPMENTS_ID_SEQ", sequenceName = "ANY_SHIPMENTS_ID_SEQ", allocationSize = 20)
    @JsonProperty("Id")
    private BigInteger id;

    @Basic
    @XmlElement(name = "COMPANY_NAME")
    @Column(name = "SHP_COMPANY_NAME")
    @JsonProperty("CompanyName")
    private String companyName;

    @Basic
    @XmlElement(name = "TRACKING_NUMBER")
    @Column(name = "SHP_TRACKING_NUMBER")
    @JsonProperty("TrackingNumber")
    private String trackingNumber;
    
    @Basic
    @Column(name = "SHP_STATUS")
    @XmlElement(name = "STATUS")
    @JsonProperty("Status")
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    @Basic
    @Column(name = "SHP_PERSON")
    @XmlElement(name = "PERSON")
    @JsonProperty("Person")
    private String person;

    @Basic
    @Temporal(TemporalType.DATE)
    @Column(name = "SHP_DATE")
    @XmlElement(name = "DATE")
    @XmlJavaTypeAdapter(DateAdapter.class)
    @JsonProperty("Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="UTC")
    private Date date;

    @Transient
    @XmlElement(name = "FROM_LOCATION")
    @JsonProperty("FromLocation")
    private String fromLocationName;

    @Transient
    @XmlElement(name = "FROM_INSTITUTION")
    @JsonProperty("FromInstitution")
    private String fromInstitutionName;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "SHP_FROM_LOCATION_ID")
    private Location fromLocation;
    
    @Transient
    @XmlElement(name = "TO_LOCATION")
    @JsonProperty("ToLocation")
    private String toLocationName;

    @Transient
    @XmlElement(name = "TO_INSTITUTION")
    @JsonProperty("ToInstitution")
    private String toInstitutionName;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "SHP_TO_LOCATION_ID")
    private Location toLocation;
    
    @Basic
    @Column(name="PAID_BY_INSTITUTION")
    @XmlElement(name = "PAID_BY_INSTITUTION")
    @JsonProperty("paidByInstitution")
    @Type(type="true_false")
    private Boolean paidByInstitution = false;
    
    @Basic
    @Column(name="EDH_LINK")
    @XmlElement(name = "EDH_LINK")
    @JsonProperty("EDHLink")
    private String edhLink;
    
    @Basic
    @Column(name="BUDGET_CODE")
    @XmlElement(name = "BUDGET_CODE")
    @JsonProperty("budgetCode")
    private String budgetCode;

    @Basic
    @Column(name="FULL_ADDRESS")
    @XmlElement(name = "FULL_ADDRESS")
    @JsonProperty("fullAddress")
    private String fullAddress;
    
    @XmlElementWrapper(name="ITEMS")
    @XmlElement(name="ITEM", type = ShipmentItem.class)
    @JsonProperty("Items")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shipment", cascade = CascadeType.ALL)
    private List<ShipmentItem> items;
    
}
