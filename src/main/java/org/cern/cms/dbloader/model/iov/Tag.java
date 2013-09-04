package org.cern.cms.dbloader.model.iov;

import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.DeleteableBase;

@Entity
@Table(name="COND_TAGS")
@Getter @Setter
@ToString
@EqualsAndHashCode(callSuper=false, of={"id"})
@AttributeOverrides({
	@AttributeOverride(name="lastUpdateTime", column=@Column(name="RECORD_DEL_FLAG_TIME")),
	@AttributeOverride(name="lastUpdateUser", column=@Column(name="RECORD_DEL_FLAG_USER"))
})
public class Tag extends DeleteableBase {

	@Id
	@Column(name="COND_TAG_ID")
    @GeneratedValue(generator = "CNDTAG_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "CNDTAG_ID_SEQ", sequenceName = "CNDTAG_ID_SEQ", allocationSize = 20)
	@XmlAttribute(name = "id", required = false)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="COND_TAG_PARENT_ID")
	@XmlTransient
	private Tag parentTag;
	
	@Basic
	@Column(name="TAG_NAME")
	@XmlElement(name = "TAG_NAME")
	private String name;
	
	@Basic
	@Column(name="DETECTOR_NAME")
	@XmlElement(name = "DETECTOR_NAME")
	private String detectorName;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "COND_IOV2TAG_MAPS", 
		joinColumns = {	@JoinColumn(name = "COND_IOV_RECORD_ID") }, 
		inverseJoinColumns = { @JoinColumn(name = "COND_TAG_ID") })
	@XmlTransient
	private Set<Iov> iovs;
	
}
