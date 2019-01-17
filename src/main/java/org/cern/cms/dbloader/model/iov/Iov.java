package org.cern.cms.dbloader.model.iov;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.*;
import org.cern.cms.dbloader.model.condition.Dataset;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "COND_IOVS")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
@XmlAccessorType(XmlAccessType.FIELD)
@AttributeOverrides({
    @AttributeOverride(name = "lastUpdateTime", column = @Column(name = "RECORD_DEL_FLAG_TIME")),
    @AttributeOverride(name = "lastUpdateUser", column = @Column(name = "RECORD_DEL_FLAG_USER"))
})
@JsonIgnoreProperties({"tags", "datasets"})
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonRootName("Iov")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Iov extends IovBase {

    @Id
    @Column(name = "COND_IOV_RECORD_ID")
    @GeneratedValue(generator = "CNDIOV_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "CNDIOV_ID_SEQ", sequenceName = "CNDIOV_ID_SEQ", allocationSize = 20)
    @XmlAttribute(name = "id", required = true)
    @JsonProperty("Id")
    private BigInteger id;

    @Basic
    @Column(name = "INTERVAL_OF_VALIDITY_BEGIN")
    @XmlElement(name = "INTERVAL_OF_VALIDITY_BEGIN", required = true)
    @JsonProperty("IovBegin")
    private BigInteger iovBegin;

    @Basic
    @Column(name = "INTERVAL_OF_VALIDITY_END")
    @XmlElement(name = "INTERVAL_OF_VALIDITY_END", required = true)
    @JsonProperty("IovEnd")
    private BigInteger iovEnd;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "iovs")
    @XmlTransient
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "iovs")
    @XmlTransient
    private Set<Dataset> datasets = new HashSet<>();
    
}
