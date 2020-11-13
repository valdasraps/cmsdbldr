package org.cern.cms.dbloader.model.serial.map;

import java.math.BigInteger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;

import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ATTR_BASES")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "EXTENSION_TABLE_NAME")
@Getter @Setter
@EqualsAndHashCode(callSuper = false, of = {"id"})
@ToString(of = {"id","extTableName","attrCatalog","deleted"})
public abstract class AttrBase {

    @Id
    @Column(name = "ATTRIBUTE_ID")
    private BigInteger id;

    @Column(name = "EXTENSION_TABLE_NAME")
    private String extTableName;
    
    @Column(name="IS_RECORD_DELETED")
    @Type(type="true_false")
    private Boolean deleted = false;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ATTR_CATALOG_ID", nullable = false)
    private AttrCatalog attrCatalog;
    
}
