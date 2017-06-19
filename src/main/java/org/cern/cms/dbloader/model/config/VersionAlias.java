package org.cern.cms.dbloader.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.model.ConfigBase;
import org.cern.cms.dbloader.model.condition.KindOfCondition;
import org.cern.cms.dbloader.model.xml.Configuration;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by aisi0860 on 5/24/17.
 */

@Entity
@Table(name="CONFIG_VERSION_ALIASES")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
public class VersionAlias extends ConfigBase {

    @Id
    @Column(name="CONFIG_VERSION_ALIAS_ID")
//    @XmlAttribute(name = "id")
    @GeneratedValue(generator = "CONFIG_VER_ALIAS_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "CONFIG_VER_ALIAS_ID_SEQ", sequenceName = "CONFIG_VER_ALIAS_ID_SEQ", allocationSize = 20)
    private BigInteger id;

    @Basic
    @Column(name="NAME", nullable=false)
    @XmlElement(name="NAME")
    private String name;

    @Basic
    @Column(name="COMMENT_DESCRIPTION")
    @XmlElement(name="COMMENT_DESCRIPTION")
    private String comment;
//pagalbos
    @Basic
    @Transient
    @XmlElement(name="KIND_OF_CONDITION")
    private KindOfCondition koc;


    @OneToMany(mappedBy = "versionAlias")
    private Set<VersionAliasDataset> versionAliasMap;

    @Basic
    @Column(name="IS_RECORD_DELETED")
    @Type(type="true_false")
    @XmlTransient
    private Boolean deleted = false;

    @Transient
    @XmlElement(name="CONFIGURATION")
    private Set<Configuration> config;
//    private List<Configuration> config = new ArrayList<>();

}

