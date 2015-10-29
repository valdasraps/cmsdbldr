package org.cern.cms.dbloader.model.condition;

import java.math.BigInteger;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.DeleteableBase;
import org.cern.cms.dbloader.model.xml.map.AttrCatalog;

@Entity
@Table(name="COND_TO_ATTR_RLTNSHPS")
@EqualsAndHashCode(callSuper=false, of={"id"})
@SequenceGenerator(name = "ANY_RLTNSHP_HISTORY_ID_SEQ", schema="CMS_GEM_CORE_COND",sequenceName = "ANY_RLTNSHP_HISTORY_ID_SEQ", allocationSize = 20)
@Getter @Setter
@ToString(exclude={"condAttrList"})
public class CondToAttrRltSh extends DeleteableBase {
    
    @Id
    @GeneratedValue(generator = "ANY_RLTNSHP_HISTORY_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @Column(name = "RELATIONSHIP_ID")
    private BigInteger id;
    
    @Column(name = "DISPLAY_NAME")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KIND_OF_CONDITION_ID", nullable = false)
    private KindOfCondition koc;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTR_CATALOG_ID", nullable = false)
    private AttrCatalog attrCatalog;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "condToAttrRtlSh")
    private Set<CondAttrList> condAttrList;
    
}
