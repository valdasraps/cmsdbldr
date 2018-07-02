package org.cern.cms.dbloader.model.serial;

import javax.xml.bind.annotation.XmlElement;

import org.cern.cms.dbloader.model.condition.KindOfCondition;
import org.cern.cms.dbloader.model.condition.Run;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Header {

	@XmlElement(name="TYPE", required = true)
	private KindOfCondition kindOfCondition;
	
	@XmlElement(name="RUN")
	private Run run;
	
	@XmlElement(name = "HINTS")
	private Hint hint;
	
}
