package org.cern.cms.dbloader.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.model.ConfigBase;
import org.cern.cms.dbloader.model.serial.Configuration;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by aisi0860 on 5/25/17.
 */


@Entity
@Table(name="CONFIG_KEY")
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class Key extends ConfigBase {

    @Id
    @Column(name="CONFIG_KEY_ID")
    @XmlAttribute(name = "id")
    @GeneratedValue(generator = "CONFIG_KEY_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "CONFIG_KEY_ID_SEQ", sequenceName = "CONFIG_KEY_ID_SEQ", allocationSize = 20)
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CONFIG_KEY_TYPE_ID", nullable = false)
    @XmlElement(name="CONFIG_KEY_TYPE_ID")
    private KeyType keyType;

    @Basic
    @Column(name="PROVENANCE")
    @XmlElement(name="PROVENANCE")
    private String provenance;

    @OneToMany(mappedBy = "key")
    private Set<KeyDataset> keyMap;

    @Basic
    @Column(name="NAME", nullable = false)
    @XmlElement(name="NAME")
    private String name;
//pagalbos
    @Basic
    @Transient
    @XmlElement(name="VERSION_ALIAS")
    private Set<VersionAlias> versionAlias;

    @Basic
    @Column(name="IS_RECORD_DELETED")
    @Type(type="true_false")
    @XmlTransient
    private Boolean deleted = false;

    @Basic
    @Column(name="COMMENT_DESCRIPTION")
    @XmlElement(name="COMMENT_DESCRIPTION")
    private String comment;

    @Transient
    @XmlElement(name="CONFIGURATION")
    private List<Configuration> config = new ArrayList<>();

}
