package org.cern.cms.dbloader.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.model.ConfigBase;
import org.cern.cms.dbloader.model.DeleteableBase;
import org.cern.cms.dbloader.model.EntityBase;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.math.BigInteger;

/**
 * Created by aisi0860 on 6/1/17.
 */


@Entity
@Table(name = "CONFIG_KEY_TYPE")
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class KeyType extends ConfigBase {

    @Id
    @XmlAttribute(name = "id")
    @GeneratedValue(generator = "CONFIG_KEY_TYPE_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "CONFIG_KEY_TYPE_ID_SEQ", sequenceName = "CONFIG_KEY_TYPE_ID_SEQ", allocationSize = 20)
    @Column(name = "CONFIG_KEY_TYPE_ID")
    private BigInteger id;

    @Column(name = "NAME", nullable = false)
    private String name;

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
