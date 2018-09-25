package org.cern.cms.dbloader.tests;

import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;


public class ShipmentLoadTest extends TestBase {

    String shipment_component = "src/test/json/shipment_by_component.json";
    String shipment_request = "src/test/json/shipment_by_request.json";

    @Test
    public void testShipmentsLoadToDb() throws Throwable {

        FilesManager fm = injector.getInstance(FilesManager.class);
        String [] files = new String [] { shipment_component, shipment_request };
        DbLoader loader = new DbLoader(pm);

        for (FileBase fb: fm.getFiles(Arrays.asList(files))) {
            loader.loadArchive(injector, fb);
        }

        try (HbmManager hbm = injector.getInstance(HbmManager.class)) {
            Session session = hbm.getSession();
            try {

                Dataset ds_component = (Dataset) session.createCriteria(Dataset.class)
                        .add(Restrictions.eq("subversion", "3"))
                        .createCriteria("kindOfCondition")
                        .add(Restrictions.eq("name", "Tracker Shipments"))
                        .uniqueResult();

                Dataset ds_request = (Dataset) session.createCriteria(Dataset.class)
                        .add(Restrictions.eq("subversion", "4"))
                        .createCriteria("kindOfCondition")
                        .add(Restrictions.eq("name", "Tracker Shipment Items"))
                        .uniqueResult();

                assertEquals("Tracker Shipments", ds_component.getRun().getRunType());
                assertEquals("apoluden", ds_component.getCreatedByUser());
                assertEquals("3", ds_component.getSubversion());
                // assertEquals(DATE_FORMAT.parse("2011-06-16 21:00:00"), ds_component.getRun().getBeginTime());

                assertEquals("Tracker Shipment Items", ds_request.getRun().getRunType());
                assertEquals("apoluden", ds_request.getCreatedByUser());
                assertEquals("4", ds_request.getSubversion());
                // assertEquals(DATE_FORMAT.parse("2011-06-16 21:00:00"), ds_request.getRun().getBeginTime());

                AuditLog alog_component = (AuditLog) session.createCriteria(AuditLog.class)
                        .add(Restrictions.eq("archiveFileName", "shipment_by_component.json"))
                        .uniqueResult();

                AuditLog alog_request = (AuditLog) session.createCriteria(AuditLog.class)
                        .add(Restrictions.eq("archiveFileName", "shipment_by_request.json"))
                        .uniqueResult();

                assertEquals((Integer) 3, alog_component.getSubversion());
                assertEquals("Tracker Shipments", alog_component.getRunType());
                assertEquals((Integer) 200, alog_component.getRunNumber());
                assertEquals((Integer) 1, alog_component.getDatasetCount());
                assertEquals((Integer) 1, alog_component.getDatasetRecordCount());

                assertEquals((Integer) 4, alog_request.getSubversion());
                assertEquals("Tracker Shipment Items", alog_request.getRunType());
                assertEquals((Integer) 200, alog_request.getRunNumber());
                assertEquals((Integer) 1, alog_request.getDatasetCount());
                assertEquals((Integer) 1, alog_request.getDatasetRecordCount());

            } finally {
                session.close();
            }

        }
    }

}
