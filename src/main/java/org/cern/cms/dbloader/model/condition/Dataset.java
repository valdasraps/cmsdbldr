package org.cern.cms.dbloader.model.condition;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
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

import org.cern.cms.dbloader.manager.xml.ChannelBaseAdapter;
import org.cern.cms.dbloader.manager.xml.CondBaseAdapter;
import org.cern.cms.dbloader.manager.xml.DateAdapter;
import org.cern.cms.dbloader.model.DeleteableBase;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.iov.Iov;
import org.cern.cms.dbloader.model.serial.map.Attribute;
import org.cern.cms.dbloader.model.serial.part.PartAssembly;

@Entity
@Table(name = "COND_DATA_SETS")
@Getter
@Setter
@AttributeOverrides({
    @AttributeOverride(name = "lastUpdateTime", column = @Column(name = "RECORD_DEL_FLAG_TIME")),
    @AttributeOverride(name = "lastUpdateUser", column = @Column(name = "RECORD_DEL_FLAG_USER"))
})
@ToString(exclude = {"iovs","data","attributes"})
@EqualsAndHashCode(callSuper = false, of = {"id"})
@JsonIgnoreProperties({"channelMap", "run", "iovs"})
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonRootName("DataSet")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dataset extends DeleteableBase {

    @Id
    @Column(name = "CONDITION_DATA_SET_ID")
    @XmlAttribute(name = "id", required = false)
    @GeneratedValue(generator = "COND_DATA_SET_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "COND_DATA_SET_ID_SEQ", sequenceName = "COND_DATA_SET_ID_SEQ", allocationSize = 20)
    @JsonProperty("Id")
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "PART_ID")
    @XmlElement(name = "PART")
    private Part part;

    @ManyToOne
    @JoinColumn(name = "CHANNEL_MAP_ID")
    @XmlTransient
    private ChannelMap channelMap;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name = "COND_RUN_ID", nullable = false)
    @XmlTransient
    private Run run;

    @ManyToOne
    @JoinColumn(name = "KIND_OF_CONDITION_ID")
    private KindOfCondition kindOfCondition;

    @ManyToOne
    @JoinColumn(name = "AGGREGATED_COND_DATA_SET_ID")
    private Dataset aggregatedDataset;

    @Basic
    @Column(name = "VERSION")
    @XmlElement(name = "VERSION")
    @JsonProperty("Version")
    private String version;

    @Basic
    @Column(name = "SUBVERSION")
    @XmlElement(name = "SUBVERSION")
    @JsonProperty("Subversion")
    private String subversion;

    @Basic
    @Column(name = "EXTENSION_TABLE_NAME")
    @XmlElement(name = "EXTENSION_TABLE_NAME")
    @JsonProperty("ExtensionTableName")
    private String extensionTable;

    @Basic
    @Column(name = "DATA_FILE_NAME")
    @XmlElement(name = "DATA_FILE_NAME")
    private String dataFilename;

    @Basic
    @Column(name = "IMAGE_FILE_NAME")
    @XmlElement(name = "IMAGE_FILE_NAME")
    @JsonProperty("ImageFileName")
    private String imageFilename;

    @Basic
    @Column(name = "CREATED_BY_USER")
    @XmlElement(name = "CREATED_BY_USER")
    @JsonProperty("CreatedByUser")
    private String createdByUser;

    @Basic
    @Column(name = "CREATE_TIMESTAMP")
    @XmlElement(name = "CREATE_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlJavaTypeAdapter(DateAdapter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="Europe/Vilnius")
    @JsonProperty("CreateTimestamp")
    private Date createTimestamp;

    @Basic
    @Column(name = "NUMBER_OF_EVENTS_IN_SET")
    @XmlElement(name = "NUMBER_OF_EVENTS_IN_SET")
    @JsonProperty("NumberOfEventsInSet")
    private Long eventsInSet;

    @Basic
    @Column(name = "SET_NUMBER")
    @XmlElement(name = "SET_NUMBER")
    @JsonProperty("SetNumber")
    private Long setNumber;

    @Basic
    @Column(name = "SET_STATUS")
    @XmlElement(name = "SET_STATUS")
    @JsonProperty("SetStatus")
    private Long setStatus;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SET_BEGIN_TIMESTAMP")
    @XmlElement(name = "SET_BEGIN_TIMESTAMP")
    @XmlJavaTypeAdapter(DateAdapter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="Europe/Vilnius")
    @JsonProperty("SetBeginTimestamp")
    private Date setBeginTime;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SET_END_TIMESTAMP")
    @XmlElement(name = "SET_END_TIMESTAMP")
    @XmlJavaTypeAdapter(DateAdapter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="Europe/Vilnius")
    @JsonProperty("SetEndTime")
    private Date setEndTime;

    @Transient
    @XmlElement(name = "CHANNEL")
    @XmlJavaTypeAdapter(value = ChannelBaseAdapter.class)
    @JsonProperty("Channel")
    private ChannelBase channel;

    @Transient
    @XmlElement(name = "PART_ASSEMBLY")
    @JsonProperty("PartAssembly")
    private PartAssembly partAssembly;

    @Transient
    @XmlElement(name = "DATA")
    @XmlJavaTypeAdapter(value = CondBaseAdapter.class)
    @JsonProperty("Data")
    private List<? extends CondBase> data;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dataset", cascade = CascadeType.ALL)
    private Set<CondAttrList> attrList;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "COND_DATASET2IOV_MAPS",
            joinColumns = {
                @JoinColumn(name = "CONDITION_DATA_SET_ID")},
            inverseJoinColumns = {
                @JoinColumn(name = "COND_IOV_RECORD_ID")})
    @XmlTransient
    private Set<Iov> iovs = new HashSet<>();
    
    @Transient
    @XmlElementWrapper(name = "PREDEFINED_ATTRIBUTES")
    @XmlElement(name = "ATTRIBUTE", type = Attribute.class)
    @JsonProperty("PredefinedAttributes")
    private List<Attribute> attributes = new ArrayList<>();

    @Transient
    @XmlElement(name = "INITIATED_BY_USER")
    @JsonProperty("InitiatedByUser")
    private String initiatedByUser;

}
