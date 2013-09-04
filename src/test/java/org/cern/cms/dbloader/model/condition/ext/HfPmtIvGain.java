package org.cern.cms.dbloader.model.condition.ext;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.condition.CondBase;

@javax.persistence.Entity
@javax.persistence.Table(name="HF_PMT_IV_GAIN", schema="CMS_HCL_HCAL_CONDITION")
@javax.xml.bind.annotation.XmlRootElement(name="DATA")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
public class HfPmtIvGain extends CondBase {

	@javax.persistence.Basic
	@javax.persistence.Column(name="BIAS_VOLTS")
	@javax.xml.bind.annotation.XmlElement(name="BIAS_VOLTS")
	private Double biasVolts;
	
	@javax.persistence.Basic
	@javax.persistence.Column(name="CURRNT_AMPS")
	@javax.xml.bind.annotation.XmlElement(name="CURRNT_AMPS")
	private Double currntAmps;
	
	@javax.persistence.Basic
	@javax.persistence.Column(name="GAIN")
	@javax.xml.bind.annotation.XmlElement(name="GAIN")
	private Double gain;

}
