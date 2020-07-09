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
import org.cern.cms.dbloader.model.construct.ext.Request.RequestStatus;

public class TrackRequestLoadTest extends TestBase {

    @Test
    public void loadRequest1Xml() throws Throwable {

        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);

        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/request_1.xml"))) {

            loader.loadArchive(injector, fb);

        }

        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            // Tower checks
            
            {
                Request req = (Request) session.createCriteria(Request.class)
                                .add(Restrictions.eq("name", "Request 1"))
                                .createCriteria("location")
                                .add(Restrictions.eq("name", "904"))
                                .uniqueResult();

                assertEquals("Requesting packs and boards to 904", req.getComment());
                //assertEquals(DATE_FORMAT.parse("2019-01-21 00:00:00"), req.getDate());
                assertEquals(RequestStatus.OPEN, req.getStatus());
                assertEquals("Artiom Poluden", req.getPerson());
                assertEquals("904", req.getLocation().getName());
                assertEquals("CERN", req.getLocation().getInstitution().getName());
                assertNotNull(req.getInsertTime());
                assertEquals(pm.getOperatorValue(), req.getInsertUser());
                assertEquals(2, req.getItems().size());
            }

            {
                Request req = (Request) session.createCriteria(Request.class)
                                .add(Restrictions.eq("name", "Request 1"))
                                .createCriteria("location")
                                .add(Restrictions.eq("name", "MIF Baltupiai"))
                                .uniqueResult();

                assertEquals("Requesting some too", req.getComment());
                //assertEquals(DATE_FORMAT.parse("2019-01-20 00:00:00 GMT"), req.getDate());
                assertEquals(RequestStatus.OPEN, req.getStatus());
                assertEquals("Artiom Poluden", req.getPerson());
                assertEquals("MIF Baltupiai", req.getLocation().getName());
                assertEquals("Vilnius University", req.getLocation().getInstitution().getName());
                assertNotNull(req.getInsertTime());
                assertEquals(pm.getOperatorValue(), req.getInsertUser());
                assertEquals(2, req.getItems().size());
            }
            
            List <AuditLog> alogs = getAuditLogs("request_1.xml");

            assertNotNull(alogs);
            assertTrue(alogs.size() > 0);
            AuditLog alog = alogs.get(0);

            assertNotNull(alog.getInsertTime());
            assertNotNull(alog.getInsertUser());
            assertNotNull(alog.getLastUpdateTime());
            assertNotNull(alog.getLastUpdateUser());
            assertEquals((Integer) 2, alog.getDatasetRecordCount());
            assertEquals("TEST", alog.getSubdetectorName());
            assertEquals("[CONSTRUCT]", alog.getKindOfConditionName());
            assertEquals("[REQUEST]", alog.getExtensionTableName());

        }

    }
    
    @Test
    public void loadRequest2Xml() throws Throwable {

        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);

        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/request_2.xml"))) {

            loader.loadArchive(injector, fb);

        }

        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            // Tower checks
            
            {
                Request req = (Request) session.createCriteria(Request.class)
                                .add(Restrictions.eq("name", "Request 1"))
                                .createCriteria("location")
                                .add(Restrictions.eq("name", "904"))
                                .uniqueResult();

                assertEquals("Requesting packs and boards to 904", req.getComment());
                //assertEquals(DATE_FORMAT.parse("2019-01-21 00:00:00"), req.getDate());
                assertEquals(RequestStatus.OPEN, req.getStatus());
                assertEquals("Artiom Poluden", req.getPerson());
                assertEquals("904", req.getLocation().getName());
                assertEquals("CERN", req.getLocation().getInstitution().getName());
                assertNotNull(req.getInsertTime());
                assertEquals(pm.getOperatorValue(), req.getInsertUser());
                assertEquals(2, req.getItems().size());
            }

            {
                Request req = (Request) session.createCriteria(Request.class)
                                .add(Restrictions.eq("name", "Request 1"))
                                .createCriteria("location")
                                .add(Restrictions.eq("name", "MIF Baltupiai"))
                                .uniqueResult();

                assertEquals("No need after all", req.getComment());
                //assertEquals(DATE_FORMAT.parse("2019-01-20 00:00:00 GMT"), req.getDate());
                assertEquals(RequestStatus.CANCELED, req.getStatus());
                assertEquals("Artiom Poluden", req.getPerson());
                assertEquals("MIF Baltupiai", req.getLocation().getName());
                assertEquals("Vilnius University", req.getLocation().getInstitution().getName());
                assertNotNull(req.getInsertTime());
                assertEquals(pm.getOperatorValue(), req.getInsertUser());
                assertEquals(2, req.getItems().size());
            }
            
            List <AuditLog> alogs = getAuditLogs("request_1.xml");

            assertNotNull(alogs);
            assertTrue(alogs.size() > 0);
            AuditLog alog = alogs.get(0);

            assertNotNull(alog.getInsertTime());
            assertNotNull(alog.getInsertUser());
            assertNotNull(alog.getLastUpdateTime());
            assertNotNull(alog.getLastUpdateUser());
            assertEquals((Integer) 2, alog.getDatasetRecordCount());
            assertEquals("TEST", alog.getSubdetectorName());
            assertEquals("[CONSTRUCT]", alog.getKindOfConditionName());
            assertEquals("[REQUEST]", alog.getExtensionTableName());

        }

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
