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
public class Run extends DeleteableBase {

    @Id
    @Column(name = "COND_RUN_ID")
    @GeneratedValue(generator = "COND_RUN_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "COND_RUN_ID_SEQ", sequenceName = "COND_RUN_ID_SEQ", allocationSize = 20)
    @XmlTransient
    private BigInteger id;

    @Basic
    @Column(name = "RUN_NUMBER")
    @XmlElement(name = "RUN_NUMBER")
    private String number;

    @Basic
    @Column(name = "RUN_NAME")
    @XmlElement(name = "RUN_NAME")
    private String name;

    @Basic
    @Column(name = "RUN_TYPE")
    @XmlElement(name = "RUN_TYPE")
    private String runType;

    @Basic
    @Column(name = "LOCATION")
    @XmlElement(name = "LOCATION")
    private String location;

    @Basic
    @Column(name = "INITIATED_BY_USER")
    @XmlElement(name = "INITIATED_BY_USER")
    private String initiatedByUser;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "RUN_BEGIN_TIMESTAMP")
    @XmlElement(name = "RUN_BEGIN_TIMESTAMP")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date beginTime;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "RUN_END_TIMESTAMP")
    @XmlElement(name = "RUN_END_TIMESTAMP")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date endTime;

    @Transient
    @XmlAttribute(name = "mode")
    private String mode;

}
