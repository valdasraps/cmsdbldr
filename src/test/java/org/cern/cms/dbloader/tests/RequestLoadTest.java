package org.cern.cms.dbloader.tests;

import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.manager.JsonManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.Run;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;

public class RequestLoadTest extends TestBase {

    String request_open = "src/test/json/request_open.json";
    String request_close = "src/test/json/request_close.json";

    @Test
    public void testRequestsUploadToDb() throws Throwable {
        FilesManager fm = injector.getInstance(FilesManager.class);
        String [] files = new String [] { request_open, request_close };
        DbLoader loader = new DbLoader(pm);

        for (FileBase fb: fm.getFiles(Arrays.asList(files))) {
            loader.loadArchive(injector, fb);
        }

        try (HbmManager hbm = injector.getInstance(HbmManager.class)) {
            Session session = hbm.getSession();
            try {

                // Test run
                Dataset ds_open = (Dataset) session.createCriteria(Dataset.class)
                        .add(Restrictions.eq("subversion", "1"))
                        .createCriteria("kindOfCondition")
                        .add(Restrictions.eq("name", "Tracker Requests"))
                        .uniqueResult();

                Dataset ds_close = (Dataset) session.createCriteria(Dataset.class)
                        .add(Restrictions.eq("subversion", "2"))
                        .createCriteria("kindOfCondition")
                        .add(Restrictions.eq("name", "Tracker Requests"))
                        .uniqueResult();

                System.out.println();
                assertEquals("Tracker Requests", ds_open.getRun().getRunType());
                assertEquals("Test TRQ request", ds_open.getComment());
                assertEquals("apoluden", ds_open.getCreatedByUser());
                assertEquals("1", ds_open.getSubversion());
                // assertEquals(DATE_FORMAT.parse("2015-11-03 19:00:00"), ds_open.getRun().getBeginTime());

                assertEquals("Tracker Requests", ds_close.getRun().getRunType());
                assertEquals("Test TRQ request", ds_close.getComment());
                assertEquals("apoluden", ds_close.getCreatedByUser());
                assertEquals("2", ds_close.getSubversion());

                AuditLog alog_open = (AuditLog) session.createCriteria(AuditLog.class)
                        .add(Restrictions.eq("archiveFileName", "request_open.json"))
                        .uniqueResult();

                AuditLog alog_close = (AuditLog) session.createCriteria(AuditLog.class)
                        .add(Restrictions.eq("archiveFileName", "request_close.json"))
                        .uniqueResult();

                assertEquals((Integer) 1, alog_open.getSubversion());
                assertEquals("Tracker Requests", alog_open.getRunType());
                assertEquals((Integer) 10, alog_open.getRunNumber());
                assertEquals((Integer) 1, alog_open.getDatasetCount());
                assertEquals((Integer) 1, alog_open.getDatasetRecordCount());

                assertEquals((Integer) 2, alog_close.getSubversion());
                assertEquals("Tracker Requests", alog_open.getRunType());
                assertEquals((Integer) 10, alog_close.getRunNumber());
                assertEquals((Integer) 1, alog_close.getDatasetCount());
                assertEquals((Integer) 1, alog_close.getDatasetRecordCount());

            } finally {
                session.close();
            }

        }
    }
}
