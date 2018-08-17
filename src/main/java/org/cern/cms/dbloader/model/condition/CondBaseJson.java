package org.cern.cms.dbloader.model.condition;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonRootName("Data")
public class CondBaseJson extends CondBase {
}
