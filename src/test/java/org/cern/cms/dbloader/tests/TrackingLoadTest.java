package org.cern.cms.dbloader.tests;

import java.util.Collections;
import java.util.List;
import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;


import static junit.framework.TestCase.*;
import org.cern.cms.dbloader.model.construct.ext.Request;

public class TrackingLoadTest extends TestBase {

//    @Test
//    public void loadRequestXml() throws Throwable {
//
//        FilesManager fm = injector.getInstance(FilesManager.class);
//
//        DbLoader loader = new DbLoader(pm);
//
//        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/request_1.xml"))) {
//
//            loader.loadArchive(injector, fb);
//
//        }
//
//        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
//            Session session = sm.getSession();
//
//            // Tower checks
//
//            Request req = (Request) session.createCriteria(Request.class)
//                            .add(Restrictions.eq("name", "Some request"))
//                            .createCriteria("location")
//                            .add(Restrictions.eq("name", "University of Iowa"))
//                            .uniqueResult();
//
//            assertEquals("Some comment", req.getComment());
//            assertEquals(DATE_FORMAT.parse("2018-09-06 00:00:00"), req.getDate());
//            assertEquals("OPEN", req.getStatus());
//            assertEquals("Artiom Poluden", req.getPerson());
//            assertEquals("University of Iowa", req.getLocation().getName());
//            assertEquals("University of Iowa", req.getLocation().getInstitution().getName());
//            assertNotNull(req.getInsertTime());
//            assertEquals("CMS_TST_PRTTYPE_TEST_WRITER", req.getInsertUser());
//            assertEquals(2, req.getItems().size());
//
//            List <AuditLog> alogs = getAuditLogs("request_1.xml");
//
//            assertNotNull(alogs);
//            assertTrue(alogs.size() > 0);
//            AuditLog alog = alogs.get(0);
//
//            assertNotNull(alog.getInsertTime());
//            assertNotNull(alog.getInsertUser());
//            assertNotNull(alog.getLastUpdateTime());
//            assertNotNull(alog.getLastUpdateUser());
//            assertEquals((Integer) 2, alog.getDatasetRecordCount());
//            assertEquals("TEST", alog.getSubdetectorName());
//            assertEquals("[CONSTRUCT]", alog.getKindOfConditionName());
//            assertEquals("[REQUEST]", alog.getExtensionTableName());
//
//        }
//
//    }
    
    @Test
    public void loadShipmentXml() throws Throwable {

        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);

        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/shipment_1.xml"))) {

            loader.loadArchive(injector, fb);

        }

//        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
//            Session session = sm.getSession();
//
//            // Tower checks
//
//            Request req = (Request) session.createCriteria(Request.class)
//                            .add(Restrictions.eq("name", "Some request"))
//                            .createCriteria("location")
//                            .add(Restrictions.eq("name", "University of Iowa"))
//                            .uniqueResult();
//
//            assertEquals("Some comment", req.getComment());
//            assertEquals(DATE_FORMAT.parse("2018-09-06 00:00:00"), req.getDate());
//            assertEquals("OPEN", req.getStatus());
//            assertEquals("Artiom Poluden", req.getPerson());
//            assertEquals("University of Iowa", req.getLocation().getName());
//            assertEquals("University of Iowa", req.getLocation().getInstitution().getName());
//            assertNotNull(req.getInsertTime());
//            assertEquals("CMS_TST_PRTTYPE_TEST_WRITER", req.getInsertUser());
//            assertEquals(2, req.getItems().size());
//
//            List <AuditLog> alogs = getAuditLogs("request_1.xml");
//
//            assertNotNull(alogs);
//            assertTrue(alogs.size() > 0);
//            AuditLog alog = alogs.get(0);
//
//            assertNotNull(alog.getInsertTime());
//            assertNotNull(alog.getInsertUser());
//            assertNotNull(alog.getLastUpdateTime());
//            assertNotNull(alog.getLastUpdateUser());
//            assertEquals((Integer) 2, alog.getDatasetRecordCount());
//            assertEquals("TEST", alog.getSubdetectorName());
//            assertEquals("[CONSTRUCT]", alog.getKindOfConditionName());
//            assertEquals("[REQUEST]", alog.getExtensionTableName());
//
//        }

    }
    
    private List<AuditLog> getAuditLogs(String fileName) throws Exception {

        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            List<AuditLog> alogs = (List<AuditLog>) session.createCriteria(AuditLog.class)
                    .add(Restrictions.eq("archiveFileName", fileName))
                    .addOrder(Order.desc("insertTime"))
                    .list();

            return  alogs;
        }
    }
}
