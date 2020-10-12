package org.cern.cms.dbloader.tests;

import java.util.Collections;

import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.junit.Test;


public class AssemblyZipTest extends TestBase {
    
    @Test
    public void step01Test() throws Throwable {
        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);
        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/zip/assembly_1.zip"))) {

            loader.loadArchive(injector, fb, pm.getOperatorAuth());

        }
        
//        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
//            Session session = sm.getSession();
//
//            Dataset ds = (Dataset) session.createCriteria(Dataset.class)
//                        .add(Restrictions.eq("version", "CSV.2"))
//                        .createCriteria("kindOfCondition")
//                            .add(Restrictions.eq("name", "IV"))
//                        .uniqueResult();
//
//            assertNotNull(ds);
//
//            AuditLog alog = (AuditLog) session.createCriteria(AuditLog.class)
//                .add(Restrictions.eq("archiveFileName", "08-data.zip"))
//                    .add(Restrictions.eq("version", "CSV.2"))
//                .uniqueResult();
//
//            assertEquals(UploadStatus.Success, alog.getStatus());
//
//        }
                
    }
    
}


