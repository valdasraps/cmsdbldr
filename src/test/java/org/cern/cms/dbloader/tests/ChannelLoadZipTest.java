package org.cern.cms.dbloader.tests;

import java.util.Arrays;
import java.util.Collections;
import junit.framework.TestCase;

import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.DynamicEntityGenerator;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.managemnt.UploadStatus;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNull;

public class ChannelLoadZipTest extends TestBase {
       
    @Test
    public void channelHandlerTest() throws Exception {
        DynamicEntityGenerator enG = injector.getInstance(DynamicEntityGenerator.class);
        
        for (String ext: Arrays.asList("TEST_CHANNELS", "TEST_COORDINATES")) {
            
            ChannelEntityHandler tc = enG.getChannelHandler(ext);
            if (tc == null) {
                enG.registerChannelEntityHandler(ext);
                tc = enG.getChannelHandler(ext);
            }

            assertNotNull(tc);

            Class<? extends ChannelBase> c = tc.getEntityClass().getC();
            assertEquals(c.getSuperclass(), ChannelBase.class);
        }

        ChannelEntityHandler tc = enG.getChannelHandler("NOT_THERE");
        TestCase.assertNull(tc);
        
        try {
            enG.registerChannelEntityHandler("NOT_THERE");
            TestCase.fail("Should have failed...");
        } catch (Exception ex) {
            // OK!
        }

        tc = enG.getChannelHandler("NOT_THERE");
        TestCase.assertNull(tc);
        
    }
    
    @Test
    public void successExampleTest() throws Throwable {
        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);
        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/channels/channels.zip"))) {
            loader.loadArchive(injector, fb, pm.getOperatorAuth());
        }
        
        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            AuditLog alog = (AuditLog) session.createCriteria(AuditLog.class)
                .add(Restrictions.eq("archiveFileName", "channels.zip"))
                .add(Restrictions.eq("dataFileName", "channels.zip"))
                .uniqueResult();

            assertEquals(UploadStatus.Success, alog.getStatus());

            alog = (AuditLog) session.createCriteria(AuditLog.class)
                .add(Restrictions.eq("archiveFileName", "channels.zip"))
                .add(Restrictions.eq("dataFileName", "testChannels.xml"))
                .uniqueResult();

            assertEquals(UploadStatus.Success, alog.getStatus());
            assertEquals((Integer) 2, alog.getDatasetCount());
            assertEquals((Integer) 1003, alog.getDatasetRecordCount());
            
        }
        
        // Second load should fail...
        try {
            for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/channels/channels.zip"))) {
                loader.loadArchive(injector, fb, pm.getOperatorAuth());
            }
            TestCase.fail("Should have failed here...");
        } catch (Exception ex) {
            // OK!
        }
        
        DynamicEntityGenerator enG = injector.getInstance(DynamicEntityGenerator.class);
        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            Long count = (Long) session.createCriteria(enG.getChannelHandler("TEST_CHANNELS").getEntityClass().getC())
                .setProjection(Projections.count("id"))
                .uniqueResult();
            assertEquals((Long) 1000L, count);

            count = (Long) session.createCriteria(enG.getChannelHandler("TEST_COORDINATES").getEntityClass().getC())
                .setProjection(Projections.count("id"))
                .uniqueResult();
            assertEquals((Long) 3L, count);

        }
        
    }

    /*
        Load Part and channel together in one XML file.
        PART and CHANNEL should pass
        PART and PartAttribute should fail
     */

    @Test
    public void PartwithChannels() throws Throwable {
        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);
        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/15_channelWithPart.xml"))) {
            loader.loadArchive(injector, fb, pm.getOperatorAuth());
        }

        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            Dataset ds = (Dataset) session.createCriteria(Dataset.class)
                    .add(Restrictions.eq("version", "JUN_7_2020"))
                    .createCriteria("kindOfCondition")
                    .add(Restrictions.eq("name", "IV"))
                    .uniqueResult();

            assertNotNull("Not found", ds);
            assertNull("Part Assembly should be null", ds.getPartAssembly());
            assertNotNull("Channel Map should be found", ds.getChannelMap());
            assertEquals("Hybrid serial", ds.getPart().getSerialNumber());
        }

        // Second load should fail...
        try {
            for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/16_channelWithPartAndPartAssembly.xml"))) {
                loader.loadArchive(injector, fb, pm.getOperatorAuth());
            }
            TestCase.fail("Should have failed here...");
        } catch (Exception ex) {
            // OK!
        }



    }
    
}


