package org.cern.cms.dbloader.model.condition;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.model.ProxyBase;

@MappedSuperclass
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties({"id", "dataset"})
public abstract class CondBase extends ProxyBase<CondBase> {

    @Id
    @Column(name = "RECORD_ID")
    @GeneratedValue(generator = "ANY_COND_RECORD_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ANY_COND_RECORD_ID_SEQ", sequenceName = "ANY_COND_RECORD_ID_SEQ", allocationSize = 20)
    @XmlTransient
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "CONDITION_DATA_SET_ID")
    @XmlTransient
    private Dataset dataset;

}
