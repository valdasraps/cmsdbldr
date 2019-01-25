package org.cern.cms.dbloader.model.config;

import lombok.Getter;
import lombok.Setter;
import org.cern.cms.dbloader.model.ConfigBase;
import org.cern.cms.dbloader.model.condition.KindOfCondition;
import org.cern.cms.dbloader.model.construct.Part;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigInteger;
import java.util.Set;

/**
 * Created by aisi0860 on 6/1/17.
 */

@Entity()
@Table(name = "CONFIG_TYPE_KOC_PART_MAPS")
@Getter
@Setter
public class KeyTypeKOCPart extends ConfigBase {

    @Id
    @GeneratedValue(generator = "CONFIG_TYPE_KOC_MAP_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "CONFIG_TYPE_KOC_MAP_ID_SEQ", sequenceName = "CONFIG_TYPE_KOC_MAP_ID_SEQ", allocationSize = 20)
    @Column(name = "CONFIG_TYPE_KOC_MAP_ID")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONFIG_KEY_TYPE_ID", nullable=false)
    private KeyType keyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KIND_OF_CONDITION_ID", nullable=false)
    private KindOfCondition koc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="PART_ID", nullable = false)
    private Part part;

    @OneToMany(mappedBy = "keyTypeKOCPart")
    private Set<KeyDataset> keyVerMap;

    @Column(name = "KOC_ORDER")
    private String kocOrder;

    @Basic
    @Column(name="IS_RECORD_DELETED")
    @Type(type="true_false")
    @XmlTransient
    private Boolean deleted = false;

}
