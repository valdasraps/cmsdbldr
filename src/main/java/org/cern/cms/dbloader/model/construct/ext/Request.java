package org.cern.cms.dbloader.model.construct.ext;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.math.BigInteger;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.manager.xml.DateAdapter;
import org.cern.cms.dbloader.model.EntityBase;
import org.cern.cms.dbloader.model.managemnt.Location;

/**
 * Tracking Request model class.
 * @author valdo
 */
@Entity
@Table(name = "REQUESTS", uniqueConstraints = @UniqueConstraint(columnNames = {"REQ_NAME", "REQ_LOCATION_ID"}))
@Getter @Setter @ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
@JsonIgnoreProperties({ 
    "id",
    "location"
})
@JsonRootName("REQUEST")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Request extends EntityBase {
    
    @Id
    @Column(name = "REQ_ID")
    @XmlElement(name = "ID")
    @GeneratedValue(generator = "ANY_REQUESTS_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ANY_REQUESTS_ID_SEQ", sequenceName = "ANY_REQUESTS_ID_SEQ", allocationSize = 20)
    @JsonProperty("Id")
    private BigInteger id;

    @Basic
    @XmlElement(name = "NAME")
    @Column(name = "REQ_NAME")
    @JsonProperty("Name")
    private String name;

    @Basic
    @Column(name = "REQ_STATUS")
    @XmlElement(name = "STATUS")
    @JsonProperty("Status")
    private String status;

    @Basic
    @Column(name = "REQ_PERSON")
    @XmlElement(name = "PERSON")
    @JsonProperty("Person")
    private String person;

    @Basic
    @Temporal(TemporalType.DATE)
    @Column(name = "REQ_DATE")
    @XmlElement(name = "DATE")
    @XmlJavaTypeAdapter(DateAdapter.class)
    @JsonProperty("Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="UTC")
    private Date date;

    @Transient
    @XmlElement(name = "LOCATION")
    @JsonProperty("Location")
    private String locationName;

    @Transient
    @XmlElement(name = "INSTITUTION")
    @JsonProperty("Institution")
    private String institutionName;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "REQ_LOCATION_ID")
    private Location location;

}
