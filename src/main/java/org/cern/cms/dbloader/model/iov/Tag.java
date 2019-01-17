package org.cern.cms.dbloader.model.iov;

import java.math.BigInteger;
import java.util.HashSet;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.DeleteableBase;

@Entity
@Table(name = "COND_TAGS")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
@XmlAccessorType(XmlAccessType.FIELD)
@AttributeOverrides({
    @AttributeOverride(name = "lastUpdateTime", column = @Column(name = "RECORD_DEL_FLAG_TIME")),
    @AttributeOverride(name = "lastUpdateUser", column = @Column(name = "RECORD_DEL_FLAG_USER"))
})
@JsonIgnoreProperties({"parentTag", "iovs"})
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonRootName("Tag")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tag extends DeleteableBase {

    @Id
    @Column(name = "COND_TAG_ID")
    @GeneratedValue(generator = "CNDTAG_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "CNDTAG_ID_SEQ", sequenceName = "CNDTAG_ID_SEQ", allocationSize = 20)
    @XmlAttribute(name = "id", required = true)
    @JsonProperty("Id")
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "COND_TAG_PARENT_ID")
    @XmlTransient
    private Tag parentTag;

    @Basic
    @Column(name = "TAG_NAME")
    @XmlElement(name = "TAG_NAME", required = true)
    @JsonProperty("TagName")
    private String name;

    @Basic
    @Column(name = "DETECTOR_NAME")
    @XmlElement(name = "DETECTOR_NAME")
    @JsonProperty("DetectorName")
    private String detectorName;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "COND_IOV2TAG_MAPS",
            joinColumns = {
                @JoinColumn(name = "COND_TAG_ID")}, // FK to src PK
            inverseJoinColumns = {
                @JoinColumn(name = "COND_IOV_RECORD_ID")}) // FK to target PK
    @XmlTransient
    private Set<Iov> iovs = new HashSet<>();

}
