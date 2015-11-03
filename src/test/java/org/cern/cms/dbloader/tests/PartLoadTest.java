package org.cern.cms.dbloader.tests;

import java.util.Collections;

import static junit.framework.TestCase.assertTrue;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.app.PartApp;
import org.cern.cms.dbloader.manager.CondHbmManager;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.manager.XmlManager;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.xml.Root;
import org.junit.Test;

public class PartLoadTest extends TestBase {
    
    @Test
    public void loadPartsXml() throws Exception {
        
        AuditLog alog = new AuditLog();
        
        XmlManager xmlm = injector.getInstance(XmlManager.class);
        PartApp partApp = injector.getInstance(PartApp.class);
        
        try (HbmManager hbm = injector.getInstance(CondHbmManager.class)) {
            for (DataFile df: FilesManager.getFiles(Collections.singletonList("src/test/xml/01_construct.xml"))) {

                Root root = xmlm.unmarshal(df.getData());
                assertTrue(partApp.handleData(df, hbm, root, alog));
                
            }
        }
                
    }
    
}
