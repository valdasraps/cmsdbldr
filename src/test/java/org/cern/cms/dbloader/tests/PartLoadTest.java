package org.cern.cms.dbloader.tests;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.construct.PartAttrList;
import org.cern.cms.dbloader.model.construct.PartTree;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.managemnt.UploadStatus;
import org.cern.cms.dbloader.model.serial.map.PositionSchema;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.junit.Test;

import javax.management.modelmbean.XMLParseException;

import static junit.framework.TestCase.*;

public class PartLoadTest extends TestBase {

    @Test
    public void loadPartsXml() throws Throwable {

        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);

        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/01_construct.xml"))) {

            loader.loadArchive(injector, fb, pm.getOperatorAuth());

        }

        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            // Tower checks

            Part tower = (Part) session.createCriteria(Part.class)
                            .add(Restrictions.eq("barcode", "123000000001"))
                            .createCriteria("kindOfPart")
                            .add(Restrictions.eq("name", "TEST Tower"))
                            .uniqueResult();

            assertEquals("TEST Tower 01", tower.getComment());
//            assertEquals(DATE_FORMAT.parse("2012-10-17 10:04:56 GMT"), tower.getInstalledDate());
            assertEquals("IBM", tower.getManufacturer().getName());
            assertEquals("University of Iowa", tower.getLocation().getName());
            assertEquals("University of Iowa", tower.getLocation().getInstitution().getName());
            assertNull(tower.getSerialNumber());
            assertNull(tower.getVersion());
            assertNull(tower.getName());
            assertNull(tower.getRemovedDate());
            assertNull(tower.getInstalledUser());
            assertNull(tower.getRemovedUser());
            assertNotNull(tower.getInsertTime());
            assertEquals("Alpha", tower.getInsertUser());
//            assertEquals(pm.getOperatorAuth().getOperatorValue(), tower.getInsertUser());
            // <PRODUCTION_DATE>2012-10-16</PRODUCTION_DATE>
            assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2012-10-16"), tower.getProductionDate());
            // <BATCH_NUMBER>1.A.1</BATCH_NUMBER>
            assertEquals("1.A.1", tower.getBatchNumber());

            assertEquals(new BigInteger("1000"), tower.getPartTree().getParentPartTree().getPartId());

            Part serial01 = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("serialNumber", "serial 01"))
                    .createCriteria("kindOfPart")
                    .add(Restrictions.eq("name", "TEST Pack"))
                    .uniqueResult();

            assertEquals("Different User", serial01.getInsertUser());

            // Serial packs
            for (String serialNumber: new String[] {"serial 02", "serial 03"}) {

                Part pack = (Part) session.createCriteria(Part.class)
                                .add(Restrictions.eq("serialNumber", serialNumber))
                                .createCriteria("kindOfPart")
                                .add(Restrictions.eq("name", "TEST Pack"))
                                .uniqueResult();

                assertNull(pack.getComment());
                assertNull(pack.getInstalledDate());
                assertNull(pack.getManufacturer());
                assertEquals("University of Iowa", pack.getLocation().getName());
                assertEquals("University of Iowa", pack.getLocation().getInstitution().getName());
                assertEquals(serialNumber, pack.getSerialNumber());
                assertNull(pack.getBarcode());
                assertNull(pack.getVersion());
                assertNull(pack.getName());
                assertNull(pack.getRemovedDate());
                assertNull(pack.getInstalledUser());
                assertNull(pack.getRemovedUser());
                assertNotNull(pack.getInsertTime());
                assertEquals("Ignas", pack.getInsertUser());

                assertEquals(tower.getId(), pack.getPartTree().getParentPartTree().getPartId());

                List<Part> children = (List<Part>) session.createCriteria(Part.class)
                                .add(Subqueries.propertyIn("id",
                                    DetachedCriteria.forClass(PartTree.class)
                                        .setProjection(Projections.property("id"))
                                        .createCriteria("parentPartTree")
                                            .add(Restrictions.eq("id", pack.getId()))
                                ))
                                .list();

                assertEquals(3, children.size());

                for (Part child: children) {

                    assertNull(child.getComment());
                    assertNull(child.getInstalledDate());
                    assertNull(child.getManufacturer());
                    if ("B03".equals(child.getName())) {
                        assertEquals("FNAL", pack.getLocation().getName());
                        assertEquals("FNAL", pack.getLocation().getInstitution().getName());
                    } else {
                        assertEquals("University of Iowa", pack.getLocation().getName());
                        assertEquals("University of Iowa", pack.getLocation().getInstitution().getName());
                    }
                    assertNull(child.getSerialNumber());
                    assertNull(child.getBarcode());
                    assertNull(child.getVersion());
                    assertTrue(Pattern.matches("B0[1-9]", child.getName()));
                    assertNull(child.getRemovedDate());
                    assertNull(child.getInstalledUser());
                    assertNull(child.getRemovedUser());
                    assertNotNull(child.getInsertTime());
                    assertEquals("Ignas", child.getInsertUser());

                    assertEquals(pack.getId(), child.getPartTree().getParentPartTree().getPartId());

                    PartAttrList attrList = (PartAttrList) session.createCriteria(PartAttrList.class)
                                    .add(Restrictions.eq("part.id", child.getId()))
                                    .uniqueResult();

                    PositionSchema attr = (PositionSchema) session.createCriteria(PositionSchema.class)
                                    .add(Restrictions.eq("id", attrList.getAttrBase().getId()))
                                    .uniqueResult();

                    assertEquals("TEST Position", attr.getAttrCatalog().getName());
                    assertTrue(Pattern.matches("[1-9]", attr.getName()));

                    assertEquals("B0".concat(attr.getName()), child.getName());

                }

            }
            List <AuditLog> alogs = getAuditLogs("01_construct.xml");

            assertNotNull(alogs);
            assertTrue(alogs.size() > 0);
            AuditLog alog = alogs.get(0);

            assertNotNull(alog.getInsertTime());
            assertNotNull(alog.getInsertUser());
            assertNotNull(alog.getLastUpdateTime());
            assertNotNull(alog.getLastUpdateUser());
            assertEquals((Integer) 13, alog.getDatasetRecordCount());
            assertEquals("TEST", alog.getSubdetectorName());
            assertEquals("[CONSTRUCT]", alog.getKindOfConditionName());
            assertEquals("[PARTS]", alog.getExtensionTableName());

        }

    }

    /*
     * This test check or exist 2 same  not deleted catalogs.
     * If exist, it should throw an expection.
     *
     */
    @Test
    public void NonUniqueResultExceptionTest () throws Throwable {

        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);


        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/05_construct.xml"))) {

            try{
                loader.loadArchive(injector, fb, pm.getOperatorAuth());
                fail("Found catalogs dublicate. Should fail here.");
            }catch (XMLParseException ex){
                // OK!
            }

            List<AuditLog> alogs = getAuditLogs("05_construct.xml");

            assertNotNull(alogs);
            assertTrue(alogs.size() > 0);
            AuditLog alog = alogs.get(0);

            assertNotNull(alog.getInsertTime());
            assertNotNull(alog.getInsertUser());
            assertEquals("05_construct.xml", alog.getArchiveFileName());
            assertEquals("05_construct.xml", alog.getDataFileName());
            assertEquals("TEST", alog.getSubdetectorName());
            assertEquals( UploadStatus.Failure, alog.getStatus());

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

    @Test
    public void loadPartwithInsertUser() throws Throwable {

        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);

        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/13_addAttributes_with_insertionUser.xml"))) {

            loader.loadArchive(injector, fb, pm.getOperatorAuth());

        }

        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            // Tower checks

            Part prt = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("barcode", "AivarasTest"))
                    .createCriteria("kindOfPart")
                    .add(Restrictions.eq("name", "GEM Foil"))
                    .uniqueResult();


            assertEquals("Vavukas", prt.getInsertUser());
            assertEquals("GEM Foil attribute Test", prt.getName());
            assertNull(prt.getSerialNumber());
            assertNull(prt.getVersion());
            assertNull(prt.getRemovedDate());
            assertNull(prt.getInstalledUser());
            assertNull(prt.getRemovedUser());
            assertNotNull(prt.getInsertTime());

            PartAttrList attrList = (PartAttrList) session.createCriteria(PartAttrList.class)
                    .add(Restrictions.eq("part.id", prt.getId()))
                    .uniqueResult();

          assertEquals("Vavukas", prt.getInsertUser());
        }

    }

/*
Test check if Part has attribute, after that we upload xml file to mark attribute as deleted and check again.
 */
    @Test
    public void markAttributeAsDeleted() throws Throwable {

        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);

        // Check if Part has attribute. It should.
        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            Part prt = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("barcode", "Part with attribute"))
                    .createCriteria("kindOfPart")
                    .add(Restrictions.eq("name", "GEM Foil"))
                    .uniqueResult();

            PartAttrList attrList = (PartAttrList) session.createCriteria(PartAttrList.class)
                    .add(Restrictions.eq("part.id", prt.getId()))
                    .add(Restrictions.eq("deleted", Boolean.FALSE))
                    .uniqueResult();

            assertEquals("Vavukas", attrList.getInsertUser());
            assertEquals("TEST Foil Position", attrList.getPartToAttrRtlSh().getName());
            assertNotNull(attrList);
        }

        // Upload XML file and mark attribute as deleted
        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/14_markAttributeDeleted.xml"))) {

            loader.loadArchive(injector, fb, pm.getOperatorAuth());

        }

        // Check if Part does not have attributes anymore
        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            Part prt = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("barcode", "Part with attribute"))
                    .createCriteria("kindOfPart")
                    .add(Restrictions.eq("name", "GEM Foil"))
                    .uniqueResult();

            PartAttrList attrList = (PartAttrList) session.createCriteria(PartAttrList.class)
                    .add(Restrictions.eq("part.id", prt.getId()))
                    .add(Restrictions.eq("deleted", Boolean.FALSE))
                    .uniqueResult();

             assertNull(attrList);
        }
    }

}
