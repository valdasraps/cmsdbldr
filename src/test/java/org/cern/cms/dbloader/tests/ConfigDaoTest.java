package org.cern.cms.dbloader.tests;

import jdk.nashorn.internal.runtime.regexp.joni.Config;
import lombok.Data;
import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.KindOfCondition;
import org.cern.cms.dbloader.model.config.*;
import org.cern.cms.dbloader.model.construct.KindOfPart;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.xml.Configuration;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by aisi0860 on 6/12/17.
 */
public class ConfigDaoTest extends TestBase {

    /* This test has to load VersionAlias xml file.
       If Version Alias already exist - exception.
       If Dataset not define - exception.
       If Part not found - exception.
       If KindOfCondition not found - exception.
       If Dataset, Part, KindOfCondition found and VersionAlias not found - write to DB.
     */
    @Test
    public void loadVersionAliasXml() throws Exception {

//        SessionManager ssm = injector.getInstance(SessionManager.class);
//        ssm.getSession();

        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);

        for (FileBase fb : fm.getFiles(Collections.singletonList("src/test/xml/08_addVersionAlias.xml"))) {

            loader.loadArchive(injector, fb);

        }

        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            // Check VersionAlias

            VersionAlias versionAlias = (VersionAlias) session.createCriteria(VersionAlias.class)
                    .add(Restrictions.eq("name", "GENA-TEST-023"))
                    .uniqueResult();

            Assert.assertEquals("GENA-TEST-023", versionAlias.getName());
            Assert.assertEquals("The most informative Comment Description", versionAlias.getComment());

            KindOfCondition koc1 = (KindOfCondition) session.createCriteria(KindOfCondition.class)
                    .add(Restrictions.eq("name", "FILES"))
                    .add(Restrictions.eq("extensionTable", "TEST_FILES"))
                    .uniqueResult();

            Assert.assertNotNull(koc1);

            Part part1 = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("name", "ROOT"))
                    .uniqueResult();

            Assert.assertNotNull(part1);

            Dataset dataset1 = (Dataset) session.createCriteria(Dataset.class)
                    .add(Restrictions.eq("version", "CONFIG_VERSION"))
                    .add(Restrictions.eq("extensionTable", "CONFIG_DATA"))
                    .uniqueResult();
            Assert.assertNotNull(dataset1);

            Assert.assertEquals(dataset1.getKindOfCondition().getId(), koc1.getId());
            Assert.assertEquals(dataset1.getPart().getId(), part1.getId());

        }
    }

    @Test
    public void loadKeysXml() throws Exception {

//        SessionManager ssm = injector.getInstance(SessionManager.class);
//        ssm.getSession();

        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);

        for (FileBase fb : fm.getFiles(Collections.singletonList("src/test/xml/09_addKeys.xml"))) {

            loader.loadArchive(injector, fb);

        }

        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            Key key = (Key) session.createCriteria(Key.class)
                    .add(Restrictions.eq("name", "022-gena-test"))
                    .uniqueResult();

            Assert.assertNotNull(key);
            Assert.assertEquals("022-gena-test", key.getName());
            Assert.assertEquals("CMS_TST_PRTTYPE_TEST_WRITER", key.getInsertUser());
            Assert.assertEquals("VGhlIHBvc2l0aXZlIFogYXhpcyBiYXJyZWwgaGFzIGJlZW4gdHVybmVkIG9mZg", key.getComment());

            KeyType keyType = (KeyType) session.createCriteria(KeyType.class)
                    .add(Restrictions.eq("name", "default"))
                    .uniqueResult();

            Assert.assertNotNull(keyType);
            Assert.assertEquals("default", keyType.getName());

            //check relation
            Assert.assertEquals(keyType, key.getKeyType());



            KeyDataset keyDataset = (KeyDataset) session.createCriteria(KeyDataset.class)
                    .add(Restrictions.eq("key", key))
                    .uniqueResult();

            Assert.assertEquals(keyDataset.getKey(), key);


            KeyTypeKOCPart keyTypeKOCPart = (KeyTypeKOCPart) session.createCriteria(KeyTypeKOCPart.class)
                    .add(Restrictions.eq("keyType", keyType))
                    .uniqueResult();

            Assert.assertEquals(keyType, keyTypeKOCPart.getKeyType());
            Assert.assertEquals(keyDataset.getKeyTypeKOCPart(), keyTypeKOCPart);

            Assert.assertEquals(keyDataset.getDataset().getPart(), keyTypeKOCPart.getPart());
            Assert.assertEquals(keyDataset.getDataset().getKindOfCondition(), keyTypeKOCPart.getKoc());

        }
    }

    @Test
    public void loadKeyAlias() throws Exception {

//        SessionManager ssm = injector.getInstance(SessionManager.class);
//        ssm.getSession();

        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);

        for (FileBase fb : fm.getFiles(Collections.singletonList("src/test/xml/10_addAlias.xml"))) {

            loader.loadArchive(injector, fb);

        }

        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            KeyAlias keyAlias = (KeyAlias) session.createCriteria(KeyAlias.class)
                    .add(Restrictions.eq("name", "022-al-gena-test"))
                    .uniqueResult();

            Assert.assertFalse(keyAlias.getDeleted());

            Key key = (Key) session.createCriteria(Key.class)
                    .add(Restrictions.eq("name", "022-gena-test"))
                    .uniqueResult();

//            Assert.assertEquals("9999980", key.getId());

            KeyAliasKey keyAliasKey = (KeyAliasKey) session.createCriteria(KeyAliasKey.class)
                    .add(Restrictions.eq("keyAlias",  keyAlias))
                    .uniqueResult();

            Assert.assertEquals(keyAliasKey.getKey(), key);

            KeyAliasVersionAlias keyAliasVersionAlias = (KeyAliasVersionAlias) session.createCriteria(KeyAliasVersionAlias.class)
                    .add(Restrictions.eq("keyAlias",  keyAlias))
                    .uniqueResult();


            Assert.assertEquals("GENA-TEST-023", keyAliasVersionAlias.getVersionAlias().getName());

        }
    }
}