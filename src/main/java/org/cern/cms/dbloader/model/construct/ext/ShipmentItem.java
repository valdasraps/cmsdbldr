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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.manager.xml.ChannelBaseProxyAdapter;
import org.cern.cms.dbloader.model.EntityBase;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.serial.part.PartAssembly;
import org.hibernate.annotations.Type;

/**
 * Request Item class for tracking.
 * @author valdo
 */
@Entity
@Table(name = "SHIPMENT_ITEMS", uniqueConstraints = @UniqueConstraint(columnNames = {"SHI_SHP_ID", "SHI_PART_ID"}))
@Getter @Setter @ToString(exclude = { "shipment" })
@EqualsAndHashCode(callSuper = false, of = {"id"})
@JsonIgnoreProperties({ 
    "id",
    "part",
    "requestItem"
})
@JsonRootName("ITEM")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class ShipmentItem extends EntityBase {
 
    @Id
    @Column(name = "SHI_ID")
    @XmlElement(name = "ID")
    @GeneratedValue(generator = "ANY_SHIPMENT_ITM_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ANY_SHIPMENT_ITM_ID_SEQ", sequenceName = "ANY_SHIPMENT_ITM_ID_SEQ", allocationSize = 20)
    @JsonProperty("Id")
    private BigInteger id;

    @Transient
    @XmlElement(name = "REQUEST_NAME")
    @JsonProperty("RequestName")
    private String requestName;
    
    @Transient
    @XmlElement(name = "REQUEST_ID")
    @JsonProperty("RequestId")
    private BigInteger requestId;
    
    @Basic
    @Column(name="SHI_IS_PAID")
    @Type(type="true_false")
    @XmlElement(name = "SHI_IS_PAID")
    @JsonProperty("paid")
    private Boolean paid = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHI_RQI_ID")
    @XmlTransient
    private RequestItem requestItem;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHI_SHP_ID")
    @XmlTransient
    private Shipment shipment;

    // Part definitions
    
    @ManyToOne
    @JoinColumn(name = "SHI_PART_ID")
    @XmlElement(name = "PART")
    private Part part;
    
    @Transient
    @XmlElement(name = "CHANNEL")
    @XmlJavaTypeAdapter(value = ChannelBaseProxyAdapter.class)
    @JsonProperty("Channel")
    private ChannelBase channel;

    @Transient
    @XmlElement(name = "PART_ASSEMBLY")
    @JsonProperty("PartAssembly")
    private PartAssembly partAssembly;
    
}
