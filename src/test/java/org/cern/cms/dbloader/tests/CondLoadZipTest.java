package org.cern.cms.dbloader.tests;

import java.util.Collections;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.fail;

import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.managemnt.UploadStatus;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

public class CondLoadZipTest extends TestBase {
       
    @Test
    public void successExampleTest() throws Exception {
        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);
        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/zip/loading.zip"))) {

            loader.loadArchive(injector, fb);

        }
        
        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            String [] versions = new String[] {
                "2000001", "2000002", "2000003"
            };
            
            for (String version: versions) {
                
                Dataset ds = (Dataset) session.createCriteria(Dataset.class)
                            .add(Restrictions.eq("version", version))
                            .createCriteria("kindOfCondition")
                                .add(Restrictions.eq("name", "IV"))
                            .uniqueResult();

                assertEquals((Long) 1L, ds.getSetNumber());

                AuditLog alog = (AuditLog) session.createCriteria(AuditLog.class)
                    .add(Restrictions.eq("archiveFileName", "loading.zip"))
                        .add(Restrictions.eq("version", version))
                    .uniqueResult();

                assertEquals(UploadStatus.Success, alog.getStatus());
            }            
        }
                
    }

    @Test
    public void failureExampleTest() throws Exception {

        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);
        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/zip/not_loading.zip"))) {

            try {
                
                loader.loadArchive(injector, fb);
                fail("Should have failed here!");
                
            } catch (Exception ex) {
            
                // OK!
            
            }

        }
        
        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();
            
            String [] versions = new String[] {
                "2000004", "2000005", "2000006"
            };
            
            for (String version: versions) {
                
                Dataset ds = (Dataset) session.createCriteria(Dataset.class)
                            .add(Restrictions.eq("version", version))
                            .createCriteria("kindOfCondition")
                                .add(Restrictions.eq("name", "IV"))
                            .uniqueResult();

                assertNull(ds);

                AuditLog alog = (AuditLog) session.createCriteria(AuditLog.class)
                    .add(Restrictions.eq("archiveFileName", "not_loading.zip"))
                    .add(Restrictions.eq("version", version))
                    .uniqueResult();

                assertEquals(UploadStatus.Success, alog.getStatus());
            }            

            AuditLog alog = (AuditLog) session.createCriteria(AuditLog.class)
                .add(Restrictions.eq("archiveFileName", "not_loading.zip"))
                .add(Restrictions.eq("dataFileName", "06-data_1.xml"))
                .uniqueResult();

            assertEquals(UploadStatus.Failure, alog.getStatus());
        
        }
                
    }
    
}


