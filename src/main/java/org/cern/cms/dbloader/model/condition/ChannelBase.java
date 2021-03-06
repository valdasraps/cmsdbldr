package org.cern.cms.dbloader.model.condition;

import java.math.BigInteger;
import javax.persistence.Basic;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.manager.json.ChannelDeserializer;
import org.cern.cms.dbloader.model.ProxyBase;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "CHANNEL_MAPS_BASE")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="EXTENSION_TABLE_NAME")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties({"id", "deleted"})
@JsonDeserialize(contentUsing = ChannelDeserializer.class)
public abstract class ChannelBase extends ProxyBase<ChannelBase> {

    @Id
    @Column(name = "CHANNEL_MAP_ID")
    @XmlTransient
    @GeneratedValue(generator = "ANY_COND_RECORD_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ANY_COND_RECORD_ID_SEQ", sequenceName = "ANY_COND_RECORD_ID_SEQ", allocationSize = 20)
    private BigInteger id;

    @Basic
    @Column(name = "EXTENSION_TABLE_NAME")
    @XmlElement(name = "EXTENSION_TABLE_NAME")
    @JsonProperty("ExtensionTableName")
    private String extensionTableName;

    @Basic
    @Column(name = "IS_RECORD_DELETED")
    @Type(type = "true_false")
    @XmlTransient
    private Boolean deleted = false;

}
