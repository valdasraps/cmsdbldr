package org.cern.cms.dbloader.model.condition;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.*;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.manager.xml.DateAdapter;
import org.cern.cms.dbloader.model.DeleteableBase;

@Entity
@Table(name = "COND_RUNS")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
@JsonIgnoreProperties({"id"})
@JsonRootName(value = "Run")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Run extends DeleteableBase {

    public static enum RunMode {
        
        AUTO_INC_NUMBER,
        SEQUENCE_NUMBER
        
    }
    
    @Id
    @Column(name = "COND_RUN_ID")
    @GeneratedValue(generator = "COND_RUN_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "COND_RUN_ID_SEQ", sequenceName = "COND_RUN_ID_SEQ", allocationSize = 20)
    @XmlTransient
    private BigInteger id;

    @Basic
    @Column(name = "RUN_NUMBER")
    @XmlElement(name = "RUN_NUMBER")
    @JsonProperty("RunNumber")
    private BigInteger number;

    @Basic
    @Column(name = "RUN_NAME")
    @XmlElement(name = "RUN_NAME")
    @JsonProperty("RunName")
    private String name;

    @Basic
    @Column(name = "RUN_TYPE")
    @XmlElement(name = "RUN_TYPE")
    @JsonProperty("RunType")
    private String runType;

    @Basic
    @Column(name = "LOCATION")
    @XmlElement(name = "LOCATION")
    @JsonProperty("Location")
    private String location;

    @Basic
    @Column(name = "INITIATED_BY_USER")
    @XmlElement(name = "INITIATED_BY_USER")
    @JsonProperty("InitiatedByUser")
    private String initiatedByUser;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "RUN_BEGIN_TIMESTAMP")
    @XmlElement(name = "RUN_BEGIN_TIMESTAMP")
    @XmlJavaTypeAdapter(DateAdapter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @JsonProperty("RunBeginTimestamp")
    private Date beginTime;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "RUN_END_TIMESTAMP")
    @XmlElement(name = "RUN_END_TIMESTAMP")
    @XmlJavaTypeAdapter(DateAdapter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @JsonProperty("RunEndTimestamp")
    private Date endTime;

    @Transient
    @XmlAttribute(name = "mode")
    @JsonProperty("Mode")
    @Enumerated(EnumType.STRING)
    private RunMode mode;

    @Transient
    @XmlAttribute(name = "sequence")
    @JsonProperty("Sequence")
    private String sequence;
}
