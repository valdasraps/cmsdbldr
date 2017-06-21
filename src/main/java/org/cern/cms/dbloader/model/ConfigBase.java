package org.cern.cms.dbloader.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

/**
 * Created by aisi0860 on 6/7/17.
 */
@MappedSuperclass
@Setter
@Getter
public abstract class ConfigBase {

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CREATE_TIMESTAMP")
    @XmlTransient
    private Date CreateTime;

    @Basic
    @Column(name="CREATED_BY_USER")
    @XmlTransient
    private String CreateUser;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="RECORD_INSERTION_TIME", nullable=false)
    @XmlTransient
    private Date insertTime;

    @Basic
    @Column(name="RECORD_INSERTION_USER", nullable=false)
    @XmlTransient
    private String insertUser;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="RECORD_DEL_FLAG_TIME")
    @XmlTransient
    private Date recordDelTime;

    @Basic
    @Column(name="RECORD_DEL_FLAG_USER")
    @XmlTransient
    private String recordDelUser;

}
