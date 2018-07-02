package org.cern.cms.dbloader.model.xml.map;

import java.math.BigInteger;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.cern.cms.dbloader.model.DeleteableBase;
import org.cern.cms.dbloader.model.construct.PartToAttrRltSh;
import org.hibernate.annotations.Type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ATTR_CATALOGS")
@Getter @Setter
@EqualsAndHashCode(callSuper=false, of={"id"})
@ToString(exclude = {"partToAttrRelation", "attrBases"})
public class AttrCatalog extends DeleteableBase {

    @Id
    @Column(name = "ATTR_CATALOG_ID")
    private BigInteger id;
    
    @Column(name = "DISPLAY_NAME")
    private String name;
    
    @Column(name = "IS_UNIQUE_WITHIN_PARENT")
    @Type(type="true_false")
    private Boolean uniqueIndentified = false;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "attrCatalog")
    private Set<PartToAttrRltSh> partToAttrRelation;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "attrCatalog")
    private Set<AttrBase> attrBases;
}
