package org.cern.cms.dbloader.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cern.cms.dbloader.model.ConfigBase;
import org.cern.cms.dbloader.model.DeleteableBase;
import org.cern.cms.dbloader.model.EntityBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigInteger;

/**
 * Created by aisi0860 on 6/1/17.
 */

@Entity
@Table(name="CONFIG_VERSION_ALIASES")
@Getter
@Setter
@ToString
@AttributeOverrides({
        @AttributeOverride(name = "recordDelTime", column = @Column(name = "RECORD_LASTUPDATE_TIME")),
        @AttributeOverride(name = "recordDelUser", column = @Column(name = "RECORD_LASTUPDATE_USER"))
})
public class VersionAliasDataset extends ConfigBase {

    @Id
    @GeneratedValue(generator = "CONFIG_VER_ALIAS_MAP_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "CONFIG_VER_ALIAS_MAP_ID_SEQ", sequenceName = "CONFIG_VER_ALIAS_MAP_ID_SEQ", allocationSize = 20)
    @Column(name="CONFIG_VERSION_ALIAS_MAP_ID")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUBVERSION_COND_DATA_SET_ID")
    private Dataset subDataset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CONDITION_DATA_SET_ID", nullable = false)
    private Dataset dataset;

    @ManyToOne(fetch = FetchType.LAZY)
//    @ManyToOne
    @JoinColumn(name="CONFIG_VERSION_ALIAS_ID")
    private VersionAlias versionAlias;

    @Basic
    @Column(name="IS_MOVED_TO_HISTORY", nullable = false)
    @Type(type="true_false")
    @XmlTransient
    private Boolean movedToHistory = false;

}
