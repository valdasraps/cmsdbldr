package org.cern.cms.dbloader.model.serial;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.cern.cms.dbloader.model.condition.KindOfCondition;
import org.cern.cms.dbloader.model.condition.Run;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonRootName(value = "Header")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Header {

	@XmlElement(name="TYPE", required = true)
	@JsonProperty("Type")
	private KindOfCondition kindOfCondition;
	
	@XmlElement(name="RUN")
	@JsonProperty("Run")
	private Run run;
	
	@XmlElement(name = "HINTS")
	@JsonProperty("Hints")
	private Hint hint;
	
}
