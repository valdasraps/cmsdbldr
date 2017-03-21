package org.cern.cms.dbloader.tests;

import java.math.BigInteger;
import java.util.Collections;
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
import org.cern.cms.dbloader.model.xml.map.PositionSchema;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.junit.Test;

import javax.management.modelmbean.XMLParseException;

import static junit.framework.TestCase.*;

public class PartLoadTest extends TestBase {

    @Test
    public void loadPartsXml() throws Exception {

        DbLoader loader = new DbLoader(pm);

        for (FileBase fb: FilesManager.getFiles(Collections.singletonList("src/test/xml/01_construct.xml"))) {

            loader.loadArchive(injector, fb);

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
            assertNotNull(tower.getInsertTime());
            assertEquals("CMS_TST_PRTTYPE_TEST_WRITER", tower.getInsertUser());

            assertEquals(new BigInteger("1000"), tower.getPartTree().getParentPartTree().getPartId());

            // Serial packs

            for (String serialNumber: new String[] {"serial 01", "serial 02", "serial 03"}) {

                Part pack = (Part) session.createCriteria(Part.class)
                                .add(Restrictions.eq("serialNumber", serialNumber))
                                .createCriteria("kindOfPart")
                                .add(Restrictions.eq("name", "TEST Pack"))
                                .uniqueResult();

                assertNull(pack.getComment());
                assertNull(pack.getInstalledDate());
                assertNull(pack.getManufacturer());
                assertNull(pack.getLocation());
                assertEquals(serialNumber, pack.getSerialNumber());
                assertNull(pack.getBarcode());
                assertNull(pack.getVersion());
                assertNull(pack.getName());
                assertNull(pack.getRemovedDate());
                assertNull(pack.getInstalledUser());
                assertNull(pack.getRemovedUser());
                assertNotNull(pack.getInsertTime());
                assertEquals("CMS_TST_PRTTYPE_TEST_WRITER", pack.getInsertUser());

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
                    assertNull(child.getLocation());
                    assertNull(child.getSerialNumber());
                    assertNull(child.getBarcode());
                    assertNull(child.getVersion());
                    assertTrue(Pattern.matches("B0[1-9]", child.getName()));
                    assertNull(child.getRemovedDate());
                    assertNull(child.getInstalledUser());
                    assertNull(child.getRemovedUser());
                    assertNotNull(child.getInsertTime());
                    assertEquals("CMS_TST_PRTTYPE_TEST_WRITER", child.getInsertUser());

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

            AuditLog alog = (AuditLog) session.createCriteria(AuditLog.class)
                            .add(Restrictions.eq("archiveFileName", "01_construct.xml"))
                            .uniqueResult();

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
    public void NonUniqueResultExceptionTest () throws Exception {

        DbLoader loader = new DbLoader(pm);

        for (FileBase fb: FilesManager.getFiles(Collections.singletonList("src/test/xml/05_construct.xml"))) {

            try{
                loader.loadArchive(injector, fb);
                fail("Found catalogs dublicate. Should fail here.");
            }catch (XMLParseException ex){
                // OK!
            }

        }
    }

}
