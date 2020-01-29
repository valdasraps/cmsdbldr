package org.cern.cms.dbloader.model.condition;


import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName("Channel")
public class ChannelBaseJson extends ChannelBase {

}
