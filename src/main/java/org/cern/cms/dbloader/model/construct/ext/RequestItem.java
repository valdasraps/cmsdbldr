package org.cern.cms.dbloader.model.construct.ext;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.model.EntityBase;
import org.cern.cms.dbloader.model.construct.KindOfPart;

/**
 * Request Item class for tracking.
 * @author valdo
 */
@Entity
@Table(name = "REQUEST_ITEMS", uniqueConstraints = @UniqueConstraint(columnNames = {"RQI_REQ_ID", "RQI_KIND_OF_PART_ID"}))
@Getter @Setter @ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
@JsonIgnoreProperties({ 
    "id",
    "kindOfPart",
    "request"
})
@JsonRootName("ITEM")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class RequestItem extends EntityBase {
 
    @Id
    @Column(name = "RQI_ID")
    @XmlElement(name = "ID")
    @GeneratedValue(generator = "ANY_REQUEST_ITM_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ANY_REQUEST_ITM_ID_SEQ", sequenceName = "ANY_REQUEST_ITM_ID_SEQ")
    @JsonProperty("Id")
    private BigInteger id;

    @Basic
    @XmlElement(name = "QUANTITY")
    @Column(name = "RQI_QUANTITY")
    @JsonProperty("Quantity")
    private Integer quantity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RQI_KIND_OF_PART_ID")
    @XmlTransient
    private KindOfPart kindOfPart;

    @Transient
    @XmlElement(name = "KIND_OF_PART")
    @JsonProperty("KindOfPart")
    private String kindOfPartName;

    @Transient
    @XmlElement(name = "KIND_OF_PART_ID")
    @JsonProperty("KindOfPartId")
    private BigInteger kindOfPartId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RQI_REQ_ID")
    @XmlTransient
    private Request request;
    
}
