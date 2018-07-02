package org.cern.cms.dbloader.model.xml;

import javax.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class ChannelUpdate {

    @XmlElement(name = "EXTENSION_TABLE_NAME", required = true)
    private String extensionTableName;
    
    @XmlElement(name = "FILE", required = true)
    private String fileName;
    
}
