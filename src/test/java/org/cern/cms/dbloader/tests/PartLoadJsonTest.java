package org.cern.cms.dbloader.tests;

import junit.framework.TestCase;
import org.cern.cms.dbloader.app.PartApp;
import org.cern.cms.dbloader.dao.PartDao;
import org.cern.cms.dbloader.handler.AuditLogHandler;
import org.cern.cms.dbloader.manager.XmlManager;
import org.cern.cms.dbloader.manager.file.ArchiveFile;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.model.construct.PartTree;
import org.cern.cms.dbloader.model.serial.Root;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.JsonManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.serial.map.Attribute;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PartLoadJsonTest extends TestBase {

    JsonManager jmanager;
    // case0
    String constr01Path = "src/test/json/01_construct.json";
    // case1
    String constr05Path = "src/test/json/05_construct.json";
    // case2
    String constr07Path = "src/test/json/07_construct.json";
    // case3
    String constr06Path = "src/test/json/06_construct.json"; // same as constr01Path

    @Before
    public void setUp() {
        this.jmanager = new JsonManager();
    }

    /*
        Serializing Part bean to JSON string case0

        IMPORTANT! Root fields that annotated with @Transient
        filled by hand
    */
    @Ignore
    public void testBeanToJsonCase0() throws Throwable {

        Integer val = 1;
        Root root = new Root();
        Part rootPart = new Part();
        rootPart.setKindOfPartName("TEST ROOT");
        rootPart.setName("ROOT");
        root.setParts(new ArrayList<Part>(){{
            add(rootPart);
        }});

        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();
            // Get Tower
            Part tower = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("barcode", "123000000001"))
                    .createCriteria("kindOfPart")
                    .add(Restrictions.eq("name", "TEST Tower"))
                    .uniqueResult();

            tower.setManufacturerName(tower.getManufacturer().getName());
            tower.setKindOfPartName(tower.getKindOfPart().getName());
            tower.setLocationName(tower.getLocation().getName());
            tower.setInstitutionName("University of Iowa");

            // Serial packs : sibling   s
            // Tower {Pack { child} }
            for (String serialNumber: new String[] {"serial 01", "serial 02", "serial 03"}) {

                Part pack = (Part) session.createCriteria(Part.class)
                        .add(Restrictions.eq("serialNumber", serialNumber))
                        .createCriteria("kindOfPart")
                        .add(Restrictions.eq("name", "TEST Pack"))
                        .uniqueResult();

                pack.setKindOfPartName(pack.getKindOfPart().getName());
                List<Part> children = (List<Part>) session.createCriteria(Part.class)
                        .add(Subqueries.propertyIn("id",
                                DetachedCriteria.forClass(PartTree.class)
                                        .setProjection(Projections.property("id"))
                                        .createCriteria("parentPartTree")
                                        .add(Restrictions.eq("id", pack.getId()))
                        ))
                        .list();

                for (Part child: children) {
                    // add siblings to pack
                    child.setKindOfPartName(child.getKindOfPart().getName());
                    Attribute attr = new Attribute();
                    attr.setName("TEST Position");
                    attr.setValue(val.toString());
                    child.setAttributes( new ArrayList<Attribute>() {{
                        add(attr);
                    }});
                    pack.addChild(child);
                    val++;
                }
                // Adding siblings to tower
                tower.addChild(pack);
            }
            rootPart.addChild(tower);
            String jsonString = this.jmanager.<Root>serialiaze(root);
            System.out.println(jsonString);
        }
    }

    /*
        Serializing Part bean to JSON string case1

        IMPORTANT! Root fields that annotated with @Transient
        filled by hand
    */
    @Ignore
    public void testBeanToJsonStringCase1() throws Throwable {

        Root root = new Root();
        Part rootPart = new Part();
        rootPart.setKindOfPartName("TEST ROOT");
        rootPart.setName("ROOT");
        root.setParts(new ArrayList<Part>(){{
            add(rootPart);
        }});

        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();
            // Get Tower
            Part tower = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("barcode", "123000000001"))
                    .createCriteria("kindOfPart")
                    .add(Restrictions.eq("name", "TEST Tower"))
                    .uniqueResult();

            tower.setManufacturerName(tower.getManufacturer().getName());
            tower.setKindOfPartName(tower.getKindOfPart().getName());
            tower.setLocationName(tower.getLocation().getName());
            tower.setInstitutionName("University of Iowa");
            Attribute attr = new Attribute();
            attr.setName("Duplicate catalog");
            attr.setValue("1");
            tower.addAttributes(attr);
            rootPart.addChild(tower);
            String jsonString = this.jmanager.<Root>serialiaze(root);
            System.out.println(jsonString);
        }
    }

    /*
        Serializing Part bean to JSON string case2

        IMPORTANT! Root fields that annotated with @Transient
        filled by hand
    */
    @Test
    public void testBeanToJsonStringCase2() throws Throwable {

        Root root = new Root();
        Part rootPart = new Part();
        rootPart.setKindOfPartName("TEST ROOT");
        rootPart.setName("ROOT");
        root.setParts(new ArrayList<Part>(){{
            add(rootPart);
        }});

        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();
            // Get Chamber
            Part chamber = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("barcode", "00001"))
                    .createCriteria("kindOfPart")
                    .add(Restrictions.eq("name", "GEM Chamber"))
                    .uniqueResult();
            chamber.setKindOfPartName(chamber.getKindOfPart().getName());
            chamber.setLocationName(chamber.getLocation().getName());
            //  Packs : siblings
            String[] serials = {
                    "PCB-VIII-RO-S-0004",
                    "PCB-VIII-DR-S-0005",
                    "FOIL-B13-S-0140",
                    "FOIL-B13-S-0138",
                    "FOIL-B13-S-0147"};

            for (String serialNumber: serials) {

                Part pack = (Part) session.createCriteria(Part.class)
                        .add(Restrictions.eq("serialNumber", serialNumber))
                        .uniqueResult();

                pack.setKindOfPartName(pack.getKindOfPart().getName());
                // Adding siblings to tower
                chamber.addChild(pack);
            }
            rootPart.addChild(chamber);
            String jsonString = this.jmanager.<Root>serialiaze(root);
            System.out.println(jsonString);
        }
    }


    /*
        Test JSON case0 map to Part class
     */
    @Test
    public void testJsonToBeanCase0() throws ParseException, IOException {
        File partJson = new File(this.constr01Path);
        Root root = this.jmanager.deserialize(partJson);

        assertNotNull(root);
        assertEquals(1, root.getParts().size());

        // ROOT Part
        Part rootPart = root.getParts().get(0);
        assertEquals("TEST ROOT", rootPart.getKindOfPartName());
        assertEquals("ROOT", rootPart.getName());
        assertEquals(1 , rootPart.getChildren().size());

        // Tower Part
        Part tower = rootPart.getChildren().get(0);
        assertEquals("TEST Tower", tower    .getKindOfPartName());
        assertEquals("123000000001", tower.getBarcode());
        assertEquals("TEST Tower 01", tower.getComment());
        assertEquals(DATE_FORMAT.parse("2012-10-17 10:04:56"), tower.getInstalledDate());
        assertEquals("IBM", tower.getManufacturerName());
        assertEquals("University of Iowa", tower.getLocationName());
        assertEquals("University of Iowa", tower.getInstitutionName());
        assertNull(tower.getSerialNumber());
        assertNull(tower.getVersion());
        assertNull(tower.getName());
        assertNull(tower.getRemovedDate());
        assertNull(tower.getInstalledUser());
        assertNull(tower.getRemovedUser());
        assertEquals("CMS_TST_PRTTYPE_TEST_WRITER", tower.getInsertUser());
        assertEquals(3 , tower.getChildren().size());

        // Serial Parts
        String[] serials = {"serial 01", "serial 02", "serial 03"};
        List<Part> packs = tower.getChildren();
        for (Part pack : packs) {
            assertNull(pack.getComment());
            assertNull(pack.getInstalledDate());
            assertNull(pack.getManufacturer());
            assertNull(pack.getLocation());
            // assertEquals(serials[packs.indexOf(pack)], pack.getSerialNumber());
            assertNull(pack.getBarcode());
            assertNull(pack.getVersion());
            assertNull(pack.getName());
            assertNull(pack.getRemovedDate());
            assertNull(pack.getInstalledUser());
            assertNull(pack.getRemovedUser());
            assertEquals("CMS_TST_PRTTYPE_TEST_WRITER", pack.getInsertUser());
            assertEquals(3, pack.getChildren().size());

            // Child Parts
            for (Part child : pack.getChildren()) {
                assertNull(child.getComment());
                assertNull(child.getInstalledDate());
                assertNull(child.getManufacturer());
                assertNull(child.getLocation());
                assertNull(child.getSerialNumber());
                assertNull(child.getBarcode());
                assertNull(child.getVersion());
                assertTrue(Pattern.matches("B0[1-9]", child.getName()));
                assertNull(child.getRemovedDate());
                assertNull(child.getInstalledUser());
                assertNull(child.getRemovedUser());
                assertEquals("CMS_TST_PRTTYPE_TEST_WRITER", child.getInsertUser());
            }
        }
    }

    /*
        Test JSON case1 map to Part class
     */
    @Test
    public void testJsonToBeanCase1() throws ParseException, IOException {
        File partJson = new File(this.constr05Path);
        Root root = this.jmanager.deserialize(partJson);

        assertNotNull(root);
        assertEquals(1, root.getParts().size());

        // ROOT Part
        Part rootPart = root.getParts().get(0);
        assertEquals("TEST ROOT", rootPart.getKindOfPartName());
        assertEquals("ROOT", rootPart.getName());
        assertEquals(1 , rootPart.getChildren().size());

        // Tower Part
        Part tower = rootPart.getChildren().get(0);
        assertEquals("TEST Tower", tower    .getKindOfPartName());
        assertEquals("123000000001", tower.getBarcode());
        assertEquals("TEST Tower 01", tower.getComment());
        assertEquals(DATE_FORMAT.parse("2012-10-17 10:04:56"), tower.getInstalledDate());
        assertEquals("IBM", tower.getManufacturerName());
        assertEquals("University of Iowa", tower.getLocationName());
        assertEquals("University of Iowa", tower.getInstitutionName());
        assertNull(tower.getSerialNumber());
        assertNull(tower.getVersion());
        assertNull(tower.getName());
        assertNull(tower.getRemovedDate());
        assertNull(tower.getInstalledUser());
        assertNull(tower.getRemovedUser());
        assertEquals(1, tower.getAttributes().size());
        assertEquals("Duplicate catalog", tower.getAttributes().get(0).getName());
        assertEquals("1", tower.getAttributes().get(0).getValue());
    }

    /*
        Test JSON case2 map to Part class
     */
    @Test
    public void testJsonToBeanCase2() throws IOException {
        File partJson = new File(this.constr07Path);
        Root root = this.jmanager.deserialize(partJson);

        assertNotNull(root);
        assertEquals(1, root.getParts().size());

        // Chamber Part
        Part chamber = root.getParts().get(0);
        assertEquals("GEM Chamber", chamber.getKindOfPartName());
        assertEquals("GE1/1-X-S-CERN-0001", chamber.getName());
        assertEquals("GE1/1-X-S-CERN-0001", chamber.getSerialNumber());
        assertEquals("904", chamber.getLocationName());
        assertEquals("00001", chamber.getBarcode());
        assertEquals(5 , chamber.getChildren().size());
        for (Part child : chamber.getChildren()) {
            // assertTrue(Pattern.matches("[A-Z]+-[A-Z_0-9]+-[A-Z]+-S-[0-9]+", child.getSerialNumber()));
            assertNotNull(child.getKindOfPartName());
            if (child.getAttributes().size() > 0) {
                assertNotNull(child.getAttributes().get(0).getName());
                assertNotNull(child.getAttributes().get(0).getValue());
            }
        }
    }

    /*
        Test JSON case0 data upload to DB
     */
    @Test
    public void testJsonCase0UploadToDb() throws Exception {
        // TODO clear DB
        File partJson = new File(this.constr01Path);
        Root root = this.jmanager.deserialize(partJson);

        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();
            PartDao dao = rf.createPartDao(sm);
            Part tower = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("barcode", "123000000001"))
                    .createCriteria("kindOfPart")
                    .add(Restrictions.eq("name", "TEST Tower"))
                    .uniqueResult();
            assertNull(tower);
            // TODO implement AuditLog
            dao.savePart(root, null); // AuditLog ???
            // TODO check tower
            tower = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("barcode", "123000000001"))
                    .createCriteria("kindOfPart")
                    .add(Restrictions.eq("name", "TEST Tower"))
                    .uniqueResult();
            assertNotNull(tower);
            assertEquals("TEST Tower", tower    .getKindOfPartName());
            assertEquals("123000000001", tower.getBarcode());
            assertEquals("TEST Tower 01", tower.getComment());
            assertEquals(DATE_FORMAT.parse("2012-10-17 10:04:56"), tower.getInstalledDate());
            assertEquals("IBM", tower.getManufacturer().getName());
            assertEquals("University of Iowa", tower.getLocation().getName());
            assertEquals("University of Iowa", tower.getLocation().getInstitution().getName());
            assertNull(tower.getSerialNumber());
            assertNull(tower.getVersion());
            assertNull(tower.getName());
            assertNull(tower.getRemovedDate());
            assertNull(tower.getInstalledUser());
            assertNull(tower.getRemovedUser());
            TestCase.assertNotNull(tower.getInsertTime());
            assertEquals("CMS_TST_PRTTYPE_TEST_WRITER", tower.getInsertUser());
            assertEquals(3 , tower.getChildren().size());

            String[] serials = {"serial 01", "serial 02", "serial 03"};
            for (String serialNumber: serials) {

                Part pack = (Part) session.createCriteria(Part.class)
                        .add(Restrictions.eq("serialNumber", serialNumber))
                        .createCriteria("kindOfPart")
                        .add(Restrictions.eq("name", "TEST Pack"))
                        .uniqueResult();

                assertNull(pack.getComment());
                assertNull(pack.getInstalledDate());
                assertNull(pack.getManufacturer());
                assertNull(pack.getLocation());
                assertEquals(serials[tower.getChildren().indexOf(pack)], pack.getSerialNumber());
                assertNull(pack.getBarcode());
                assertNull(pack.getVersion());
                assertNull(pack.getName());
                assertNull(pack.getRemovedDate());
                assertNull(pack.getInstalledUser());
                assertNull(pack.getRemovedUser());
                TestCase.assertNotNull(pack.getInsertTime());
                assertEquals("CMS_TST_PRTTYPE_TEST_WRITER", pack.getInsertUser());
                assertEquals(3, pack.getChildren().size());

                List<Part> children = (List<Part>) session.createCriteria(Part.class)
                        .add(Subqueries.propertyIn("id",
                                DetachedCriteria.forClass(PartTree.class)
                                        .setProjection(Projections.property("id"))
                                        .createCriteria("parentPartTree")
                                        .add(Restrictions.eq("id", pack.getId()))
                        ))
                        .list();

                for (Part child: children) {
                    assertNull(child.getComment());
                    assertNull(child.getInstalledDate());
                    assertNull(child.getManufacturer());
                    assertNull(child.getLocation());
                    assertNull(child.getSerialNumber());
                    assertNull(child.getBarcode());
                    assertNull(child.getVersion());
                    assertTrue(Pattern.matches("B0[1-9]", child.getName()));
                    assertNull(child.getRemovedDate());
                    assertNull(child.getInstalledUser());
                    assertNull(child.getRemovedUser());
                    TestCase.assertNotNull(child.getInsertTime());
                    assertEquals("CMS_TST_PRTTYPE_TEST_WRITER", child.getInsertUser());
                }
            }
        }
    }

    /*
        Test JSON case1 data upload to Db
     */
    @Ignore
    public void testJsonCase1UploadToDb() throws Exception {
        // TODO clear DB
        File partJson = new File(this.constr05Path);
        Root root = this.jmanager.deserialize(partJson);
        PartApp app = injector.getInstance(PartApp.class);
        try(SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();
            PartDao dao = rf.createPartDao(sm);
            Part tower = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("barcode", "123000000001"))
                    .createCriteria("kindOfPart")
                    .add(Restrictions.eq("name", "TEST Tower"))
                    .uniqueResult();
            assertNull(tower);
            // TODO implement AuditLog
            dao.savePart(root, null);
            tower = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("barcode", "123000000001"))
                    .createCriteria("kindOfPart")
                    .add(Restrictions.eq("name", "TEST Tower"))
                    .uniqueResult();
            assertNotNull(tower);
            assertEquals("TEST Tower", tower    .getKindOfPartName());
            assertEquals("123000000001", tower.getBarcode());
            assertEquals("TEST Tower 01", tower.getComment());
            assertEquals(DATE_FORMAT.parse("2012-10-17 10:04:56"), tower.getInstalledDate());
            assertEquals("IBM", tower.getManufacturer().getName());
            assertEquals("University of Iowa", tower.getLocation().getName());
            assertEquals("University of Iowa", tower.getLocation().getInstitution().getName());
            assertNull(tower.getSerialNumber());
            assertNull(tower.getVersion());
            assertNull(tower.getName());
            assertNull(tower.getRemovedDate());
            assertNull(tower.getInstalledUser());
            assertNull(tower.getRemovedUser());
            TestCase.assertNotNull(tower.getInsertTime());
            assertEquals(1, tower.getAttributes().size());
            assertEquals("Duplicate catalog", tower.getAttributes().get(0).getName());
            assertEquals("1", tower.getAttributes().get(0).getValue());
        }
    }

    /*
        Test JSON case2 data upload to Db
     */
    @Test
    public void testJsonCase2UploadToDb() throws Exception {
        // TODO
        File partJson = new File(this.constr07Path);
        Root root = this.jmanager.deserialize(partJson);
        PartApp app = injector.getInstance(PartApp.class);
        try(SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();
            PartDao dao = rf.createPartDao(sm);
            Part chamber = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("barcode", "00001"))
                    .createCriteria("kindOfPart")
                    .add(Restrictions.eq("name", "GEM Chamber"))
                    .uniqueResult();

            DataFile data = new DataFile(new XmlManager(), new JsonManager(), new ArchiveFile(partJson), partJson);
            AuditLogHandler dataLog = rf.createAuditDao(data);
            assertNull(chamber);
            dao.savePart(root, dataLog.getLog());
            chamber = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("barcode", "00001"))
                    .createCriteria("kindOfPart")
                    .add(Restrictions.eq("name", "GEM Chamber"))
                    .uniqueResult();
            assertNotNull(chamber);
            assertEquals("GEM Chamber", chamber.getKindOfPartName());
            assertEquals("GE1/1-X-S-CERN-0001", chamber.getName());
            assertEquals("GE1/1-X-S-CERN-0001", chamber.getSerialNumber());
            assertEquals("904", chamber.getLocationName());
            assertEquals("00001", chamber.getBarcode());
            // assertEquals(5 , chamber.getChildren().size());
            //  Packs : siblings
            String[] serials = {
                    "PCB-VIII-RO-S-0004",
                    "PCB-VIII-DR-S-0005",
                    "FOIL-B13-S-0140",
                    "FOIL-B13-S-0138",
                    "FOIL-B13-S-0147" };
            for (String serialNumber: serials) {

                Part child = (Part) session.createCriteria(Part.class)
                        .add(Restrictions.eq("serialNumber", serialNumber))
                        .uniqueResult();
                // assertTrue(Pattern.matches("\\s+-\\s+-\\s+-S-\\d+", chamber.getSerialNumber()));
                assertNotNull(chamber.getKindOfPartName());
                assertEquals(1, child.getAttributes().size());
                assertNotNull(child.getAttributes().get(0).getName());
                assertNotNull(child.getAttributes().get(0).getValue());

            }
        }
    }
}
