package org.cern.cms.dbloader.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.model.ConfigBase;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigInteger;

/**
 * Created by aisi0860 on 6/5/17.
 */
@Entity
@Table(name="CONFIG_KEY_ALIAS_MAPS")
@Getter
@Setter
@ToString
public class KeyAliasKey extends ConfigBase {

    @Id
    @GeneratedValue(generator = "CONFIG_KEY_ALIAS_MAP_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "CONFIG_KEY_ALIAS_MAP_ID_SEQ", sequenceName = "CONFIG_KEY_ALIAS_MAP_ID_SEQ", allocationSize = 20)
    @Column(name="CONFIG_KEY_ALIAS_MAP_ID")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CONFIG_KEY_ALIAS_ID", nullable = false)
    private KeyAlias keyAlias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CONFIG_KEY_ID", nullable = false)
    private Key key;

    @Basic
    @Column(name="IS_MOVED_TO_HISTORY", nullable = false)
    @Type(type="true_false")
    @XmlTransient
    private Boolean movedToHistory = false;

    @Basic
    @Column(name="IS_RECORD_DELETED")
    @Type(type="true_false")
    @XmlTransient
    private Boolean deleted = false;

}
