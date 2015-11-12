package org.cern.cms.dbloader.tests;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.app.CondApp;
import org.cern.cms.dbloader.manager.CondHbmManager;
import org.cern.cms.dbloader.manager.CondManager;
import org.cern.cms.dbloader.manager.CondXmlManager;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.model.OptId;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.xml.Root;
import org.junit.Test;

public class CondLoadTest extends TestBase {
    
    @Test
    public void printAndLoadExampleTest() throws Exception {
        
        CondManager condm = injector.getInstance(CondManager.class);
        CondEntityHandler ch = condm.getConditionHandler("IV");
        
        String exampleFile = "target/iv_example.xml";
        
        assertEquals(ch, condm.getConditionHandler(ch.getId()));
        assertEquals(ch, condm.getConditionHandler(new OptId(ch.getId().toString())));
        
        CondXmlManager xmlm = new CondXmlManager(ch, null);
        
        FileOutputStream out = new FileOutputStream(exampleFile);
        xmlm.printExample(pm, out);
        
        AuditLog alog = new AuditLog();
        CondApp condApp = injector.getInstance(CondApp.class);
        try (HbmManager hbm = injector.getInstance(CondHbmManager.class)) {
            for (DataFile df: FilesManager.getFiles(Collections.singletonList(exampleFile))) {

                Root root = xmlm.unmarshal(df.getData());
                assertTrue(condApp.handleData(df, hbm, root, alog));
                
            }
        }
                
    }
    
    @Test
    public void loadExampleTest() throws Exception {
        
        CondManager condm = injector.getInstance(CondManager.class);
        CondEntityHandler ch = condm.getConditionHandler("IV");
        
        CondXmlManager xmlm = new CondXmlManager(ch, null);
        
        AuditLog alog = new AuditLog();
        CondApp condApp = injector.getInstance(CondApp.class);
        try (HbmManager hbm = injector.getInstance(CondHbmManager.class)) {
            for (DataFile df: FilesManager.getFiles(Arrays.asList(
                    "src/test/xml/02_condition.xml", 
                    "src/test/xml/03_condition.xml"))) {

                Root root = xmlm.unmarshal(df.getData());
                assertTrue(condApp.handleData(df, hbm, root, alog));
                
            }
        }
                
    }
    
    @Test
    public void loadLobExampleTest() throws Exception {
        
        CondManager condm = injector.getInstance(CondManager.class);
        CondEntityHandler ch = condm.getConditionHandler("FILES");
        
        CondXmlManager xmlm = new CondXmlManager(ch, null);
        
        AuditLog alog = new AuditLog();
        CondApp condApp = injector.getInstance(CondApp.class);
        try (HbmManager hbm = injector.getInstance(CondHbmManager.class)) {
            for (DataFile df: FilesManager.getFiles(Collections.singletonList("src/test/lob/lob_test.zip"))) {

                Root root = xmlm.unmarshal(df.getData());
                assertTrue(condApp.handleData(df, hbm, root, alog));
                
            }
        }
                
    }
    
}
