package org.cern.cms.dbloader.model.config;

import lombok.Getter;
import lombok.Setter;
import org.cern.cms.dbloader.model.ConfigBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigInteger;

/**
 * Created by aisi0860 on 6/1/17.
 */
@Entity
@Table(name = "CONFIG_KEY_VERSION_MAPS")
@Getter @Setter
@AttributeOverrides({
    @AttributeOverride(name = "recordDelTime", column = @Column(name = "RECORD_LASTUPDATE_TIME")),
    @AttributeOverride(name = "recordDelUser", column = @Column(name = "RECORD_LASTUPDATE_USER"))
})
public class KeyDataset extends ConfigBase {

    @Id
    @GeneratedValue(generator = "CONFIG_KEY_VERSION_MAP_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "CONFIG_KEY_VERSION_MAP_ID_SEQ", sequenceName = "CONFIG_KEY_VERSION_MAP_ID_SEQ", allocationSize = 20)
    @Column(name = "CONFIG_KEY_VERSION_MAP_ID")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CONDITION_DATA_SET_ID", nullable=false)
    private Dataset dataset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUBVERSION_COND_DATA_SET_ID", nullable=false)
    private Dataset subDataset;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="CONFIG_KEY_ID", nullable=false)
    private Key key;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="CONFIG_TYPE_KOC_MAP_ID")
    private KeyTypeKOCPart keyTypeKOCPart;

    @Basic
    @Column(name="IS_MOVED_TO_HISTORY", nullable = false)
    @Type(type="true_false")
    @XmlTransient
    private Boolean movedToHistory = false;

}
