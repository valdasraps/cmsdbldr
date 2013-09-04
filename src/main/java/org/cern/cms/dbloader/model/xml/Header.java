package org.cern.cms.dbloader.model.xml;

import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.condition.KindOfCondition;
import org.cern.cms.dbloader.model.condition.Run;

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
