package org.cern.cms.dbloader.model.construct;

import java.math.BigInteger;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.DeleteableBase;
import org.cern.cms.dbloader.model.serial.map.AttrCatalog;

@Entity
@Table(name = "PART_TO_ATTR_RLTNSHPS")
@Getter @Setter
@ToString
public class PartToAttrRltSh extends DeleteableBase {
    
    @Id
    @Column(name = "RELATIONSHIP_ID")
    private BigInteger id;
    
    @Column(name = "DISPLAY_NAME")
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "KIND_OF_PART_ID", nullable = false)
    private KindOfPart kop;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ATTR_CATALOG_ID", nullable = false)
    private AttrCatalog attrCatalog;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "partToAttrRtlSh")
    private Set<PartAttrList> partAttrList;
    
}
