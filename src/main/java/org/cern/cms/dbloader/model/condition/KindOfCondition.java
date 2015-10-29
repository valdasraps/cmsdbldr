package org.cern.cms.dbloader.model.condition;

import java.math.BigInteger;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.DeleteableBase;
import org.cern.cms.dbloader.model.construct.KindOfPart;

@Entity
@Table(name="KINDS_OF_CONDITIONS")
@Getter @Setter
@ToString
@EqualsAndHashCode(callSuper=false, of={"id"})
public class KindOfCondition extends DeleteableBase {
	
	@Id
	@Column(name="KIND_OF_CONDITION_ID")
	@XmlTransient
	private BigInteger id;
	
	@Basic
	@Column(name="NAME")
	@XmlElement(name="NAME")
	private String name;

	@Basic
	@Column(name="EXTENSION_TABLE_NAME")
	@XmlElement(name="EXTENSION_TABLE_NAME", required = true)
	private String extensionTable;

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="COND_TO_PART_RLTNSHPS", joinColumns={@JoinColumn(name="KIND_OF_CONDITION_ID")}, inverseJoinColumns={@JoinColumn(name="KIND_OF_PART_ID")})
    @XmlTransient
	private Set<KindOfPart> kindsOfParts;
	
}
