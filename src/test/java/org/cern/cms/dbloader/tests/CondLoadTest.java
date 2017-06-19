package org.cern.cms.dbloader.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.CondManager;
import org.cern.cms.dbloader.manager.CondXmlManager;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.model.OptId;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.iov.Iov;
import org.cern.cms.dbloader.model.iov.Tag;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

public class CondLoadTest extends TestBase {
    
    @Test
    public void printAndLoadExampleTest() throws Exception {

        FilesManager fm = injector.getInstance(FilesManager.class);
        CondManager condm = injector.getInstance(CondManager.class);
        CondEntityHandler ch = condm.getConditionHandler("IV");
        
        String exampleFile = "target/iv_example.xml";
        
        assertEquals(ch, condm.getConditionHandler(ch.getId()));
        assertEquals(ch, condm.getConditionHandler(new OptId(ch.getId().toString())));
        
        CondXmlManager xmlm = new CondXmlManager(ch, null);
        
        FileOutputStream out = new FileOutputStream(exampleFile);
        xmlm.printExample(pm, out);
        
        DbLoader loader = new DbLoader(pm);
        for (FileBase df: fm.getFiles(Collections.singletonList(exampleFile))) {

            loader.loadArchive(injector, df);

        }
                
    }
    
    @Test
    public void loadExampleTest() throws Exception {

        FilesManager fm = injector.getInstance(FilesManager.class);
        String [] files = new String [] {
            "src/test/xml/02_condition.xml"
            ,"src/test/xml/03_condition.xml"
            ,"src/test/xml/04_condition.xml"
        };
        
        DbLoader loader = new DbLoader(pm);
        for (FileBase fb: fm.getFiles(Arrays.asList(files))) {

            loader.loadArchive(injector, fb);

        }
            
        try (HbmManager hbm = injector.getInstance(HbmManager.class)) {
            Session session = hbm.getSession();
            try {
                
                // Check 02_condition.xml
                
                Dataset ds = (Dataset) session.createCriteria(Dataset.class)
                            .add(Restrictions.eq("version", "JUN_7_2011"))
                            .createCriteria("kindOfCondition")
                                .add(Restrictions.eq("name", "IV"))
                            .uniqueResult();

                assertEquals("TEST_CHANNELS", ds.getChannelMap().getExtensionTableName());
                assertEquals("1", ds.getRun().getNumber());
                assertEquals("1", ds.getRun().getRunType());
                assertEquals("Test TEST condition entry", ds.getComment());
                assertEquals("joshi", ds.getCreatedByUser());
                assertEquals("2", ds.getSubversion());
                Iov iov = ds.getIovs().iterator().next();
                assertEquals(new BigInteger("1"), iov.getIovBegin());
                assertEquals(new BigInteger("-1"), iov.getIovEnd());
                Tag tag = iov.getTags().iterator().next();
                assertEquals("Some Test Tag", tag.getName());
                assertEquals("TEST", tag.getDetectorName());
                assertEquals("This is some comment", tag.getComment());
                
                AuditLog alog = (AuditLog) session.createCriteria(AuditLog.class)
                                .add(Restrictions.eq("archiveFileName", "02_condition.xml"))
                                .uniqueResult();
                
                assertEquals("JUN_7_2011", alog.getVersion());
                assertEquals((Integer) 2, alog.getSubversion());
                assertEquals("1", alog.getRunType());
                assertEquals((Integer) 1, alog.getRunNumber());
                assertEquals((Integer) 1, alog.getDatasetCount());
                assertEquals((Integer) 3, alog.getDatasetRecordCount());
                assertEquals(new BigInteger("1"), alog.getIntervalOfValidityBegin());
                assertEquals(new BigInteger("-1"), alog.getIntervalOfValidityEnd());
                assertEquals("Some Test Tag", alog.getTagName());
                
                // Check all logs
                
                for (String file: files) {
                    File f = new File(file);
                    alog = (AuditLog) session.createCriteria(AuditLog.class)
                                    .add(Restrictions.eq("archiveFileName", f.getName()))
                                    .uniqueResult();

                    assertNotNull(alog.getInsertTime());
                    assertNotNull(alog.getInsertUser());
                    assertNotNull(alog.getLastUpdateTime());
                    assertNotNull(alog.getLastUpdateUser());
                    //assertEquals((Integer) 13, alog.getDatasetRecordCount());
                    assertEquals("TEST", alog.getSubdetectorName());
                    assertEquals("IV", alog.getKindOfConditionName());
                    assertEquals("TEST_IV", alog.getExtensionTableName());
                    
                }                
                
            } finally {            
                session.close();
            }
            
        }
                
    }
    
    @Test
    public void loadLobExampleTest() throws Exception {
        FilesManager fm = injector.getInstance(FilesManager.class);
        DbLoader loader = new DbLoader(pm);
        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/lob/lob_test.zip"))) {

            loader.loadArchive(injector, fb);

        }
                
    }
    
}
