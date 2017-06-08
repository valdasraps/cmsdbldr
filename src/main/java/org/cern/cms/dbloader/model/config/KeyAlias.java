package org.cern.cms.dbloader.model.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.model.ConfigBase;
import org.cern.cms.dbloader.model.DeleteableBase;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.math.BigInteger;

/**
 * Created by aisi0860 on 5/26/17.
 */

@Entity
@Table(name="CONFIG_KEY_ALIASES")
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class KeyAlias extends ConfigBase {

    @Id
    @Column(name="CONFIG_KEY_ALIAS_ID")
    @XmlAttribute(name = "id")
    @GeneratedValue(generator = "CONFIG_KEY_ALIAS_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "CONFIG_KEY_ALIAS_ID_SEQ", sequenceName = "CONFIG_KEY_ALIAS_ID_SEQ", allocationSize = 20)
    private BigInteger id;

    @Basic
    @Column(name="NAME", nullable=false)
    @XmlElement(name="NAME")
    private String name;
//pagalbos
    @Basic
    @Transient
    @Column(name="KEY", nullable=false)
    @XmlElement(name="KEY")
    private Key key;

    @Basic
    @Column(name="IS_RECORD_DELETED")
    @Type(type="true_false")
    @XmlTransient
    private Boolean deleted = false;

    @Basic
    @Column(name="COMMENT_DESCRIPTION")
    @XmlElement(name="COMMENT_DESCRIPTION")
    private String comment;

}
