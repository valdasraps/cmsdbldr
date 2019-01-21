package org.cern.cms.dbloader.tests;

import java.util.Collections;
import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;


import static junit.framework.TestCase.*;
import org.cern.cms.dbloader.model.construct.ext.Shipment;
import org.cern.cms.dbloader.model.construct.ext.Shipment.ShipmentStatus;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TrackShipmentLoadTest extends TestBase {

    @Test
    public void loadShip1Xml() throws Throwable {

        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);
        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/shipment_1.xml"))) {

            loader.loadArchive(injector, fb);

        }

        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            {
                Shipment ship = (Shipment) session.createCriteria(Shipment.class)
                                .add(Restrictions.eq("trackingNumber", "SEAPS5000495784V2"))
                                .uniqueResult();

                assertEquals("Shipping requested parts", ship.getComment());
                //assertEquals(DATE_FORMAT.parse("2019-01-21 00:00:00"), req.getDate());
                assertEquals(ShipmentStatus.PACKAGING, ship.getStatus());
                assertEquals("Artiom Poluden", ship.getPerson());
                assertEquals("FNAL", ship.getFromLocation().getName());
                assertEquals("FNAL", ship.getFromLocation().getInstitution().getName());
                assertEquals("904", ship.getToLocation().getName());
                assertEquals("CERN", ship.getToLocation().getInstitution().getName());
                assertNotNull(ship.getInsertTime());
                assertEquals("CMS_TST_PRTTYPE_TEST_WRITER", ship.getInsertUser());
                assertEquals(3, ship.getItems().size());
            }
        }
    }

    @Test
    public void loadShip2Xml() throws Throwable {
        
        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);
        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/shipment_2.xml"))) {

            loader.loadArchive(injector, fb);

        }

        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            {
                Shipment ship = (Shipment) session.createCriteria(Shipment.class)
                                .add(Restrictions.eq("trackingNumber", "SEAPS5000495784V3"))
                                .uniqueResult();

                assertEquals("Shipping requested parts", ship.getComment());
                //assertEquals(DATE_FORMAT.parse("2019-01-21 00:00:00"), req.getDate());
                assertEquals(ShipmentStatus.PACKAGING, ship.getStatus());
                assertEquals("Artiom Poluden", ship.getPerson());
                assertEquals("FNAL", ship.getFromLocation().getName());
                assertEquals("FNAL", ship.getFromLocation().getInstitution().getName());
                assertEquals("904", ship.getToLocation().getName());
                assertEquals("CERN", ship.getToLocation().getInstitution().getName());
                assertNotNull(ship.getInsertTime());
                assertEquals("CMS_TST_PRTTYPE_TEST_WRITER", ship.getInsertUser());
                assertEquals(1, ship.getItems().size());
            }

        }
    }

}
