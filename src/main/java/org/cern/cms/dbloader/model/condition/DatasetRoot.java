package org.cern.cms.dbloader.model.condition;

import java.util.List;
import org.cern.cms.dbloader.model.serial.Elements;
import org.cern.cms.dbloader.model.serial.Header;
import org.cern.cms.dbloader.model.serial.map.Maps;

public interface DatasetRoot {
    
    public Header getHeader();
    public Elements getElements();
    public Maps getMaps();
    public List<Dataset> getDatasets();
    
}
