package org.cern.cms.dbloader.tests;

import java.util.Arrays;
import java.util.Collections;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;

import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.CondManager;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.managemnt.UploadStatus;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

public class ChannelLoadZipTest extends TestBase {
       
    @Test
    public void channelHandlerTest() throws Exception {
        CondManager cm = injector.getInstance(CondManager.class);
        
        for (String ext: Arrays.asList("TEST_CHANNELS", "TEST_COORDINATES")) {
            
            ChannelEntityHandler tc = cm.getChannelHandler(ext);
            if (tc == null) {
                cm.registerChannelEntityHandler(ext);
                tc = cm.getChannelHandler(ext);
            }

            TestCase.assertNotNull(tc);

            Class<? extends ChannelBase> c = tc.getEntityClass().getC();
            assertEquals(c.getSuperclass(), ChannelBase.class);
        }

        ChannelEntityHandler tc = cm.getChannelHandler("NOT_THERE");
        TestCase.assertNull(tc);
        
        try {
            cm.registerChannelEntityHandler("NOT_THERE");
            TestCase.fail("Should have failed...");
        } catch (Exception ex) {
            // OK!
        }

        tc = cm.getChannelHandler("NOT_THERE");
        TestCase.assertNull(tc);
        
    }
    
    @Test
    public void successExampleTest() throws Throwable {
        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);
        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/channels/channels.zip"))) {
            loader.loadArchive(injector, fb);
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
                loader.loadArchive(injector, fb);
            }
            TestCase.fail("Should have failed here...");
        } catch (Exception ex) {
            // OK!
        }
        
        CondManager cm = injector.getInstance(CondManager.class);
        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            Long count = (Long) session.createCriteria(cm.getChannelHandler("TEST_CHANNELS").getEntityClass().getC())
                .setProjection(Projections.count("id"))
                .uniqueResult();
            assertEquals((Long) 1000L, count);
            
            count = (Long) session.createCriteria(cm.getChannelHandler("TEST_COORDINATES").getEntityClass().getC())
                .setProjection(Projections.count("id"))
                .uniqueResult();
            assertEquals((Long) 3L, count);
            
        }
        
    }
    
}


