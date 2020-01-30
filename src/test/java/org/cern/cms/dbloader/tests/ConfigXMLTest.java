package org.cern.cms.dbloader.tests;

import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.XmlManager;
import org.cern.cms.dbloader.model.config.Key;
import org.cern.cms.dbloader.model.config.VersionAlias;
import org.cern.cms.dbloader.model.serial.Configuration;
import org.cern.cms.dbloader.model.serial.Root;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by aisi0860 on 5/26/17.
 */
public class ConfigXMLTest extends TestBase {

    @Test
    public void testAddAlias() throws Exception {

        XmlManager xmlm = injector.getInstance(XmlManager.class);
        Root root = xmlm.unmarshal(new File("src/test/xml/10_addAlias.xml"));

        //Doesn't exist
        Assert.assertNull(root.getDatasets());
        Assert.assertNull(root.getMaps().getKey());


        Assert.assertNotNull(root.getMaps());
        Assert.assertNotNull(root.getMaps().getKeyAlias());
        Key key = root.getMaps().getKeyAlias().iterator().next().getKey().iterator().next();
        Assert.assertNotNull(key);
        Assert.assertNotNull(key.getVersionAlias());
        Assert.assertNotNull(key.getConfig());


        Assert.assertEquals("022-al-gena-test", root.getMaps().getKeyAlias().iterator().next().getName());

        Assert.assertEquals("022-gena-test", key.getName());
        Assert.assertEquals("GENA-TEST-023", key.getVersionAlias().iterator().next().getName());
        Assert.assertEquals("TEST_FILES", key.getVersionAlias().iterator().next().getKoc().getExtensionTable());
        Assert.assertEquals("FILES", key.getVersionAlias().iterator().next().getKoc().getName());
        Assert.assertEquals(1, key.getConfig().size());

        Configuration config = root.getMaps().getKeyAlias().iterator().next().getKey().iterator().next().getConfig().get(0);
        Assert.assertEquals("ROOT",config.getPart().getName());
        Assert.assertEquals("TEST ROOT", config.getPart().getKindOfPartName());
        Assert.assertEquals("TEST_FILES", config.getKoc().getExtensionTable());
        Assert.assertEquals("FILES", config.getKoc().getName());
        Assert.assertEquals("CONFIG_VERSION", config.getDataset().getVersion());

    }

    @Test
    public void testAddVersionAlias() throws Exception {

        XmlManager xmlm = injector.getInstance(XmlManager.class);
        Root root = xmlm.unmarshal(new File("src/test/xml/08_addVersionAlias.xml"));

        //Doesn't exist
        Assert.assertNull(root.getDatasets());
        Assert.assertNull(root.getMaps().getKeyAlias());

        //Does exist
        Assert.assertNotNull(root.getMaps());
        Assert.assertNotNull(root.getMaps().getVersionAliases().iterator().next());
        VersionAlias versionAlias = root.getMaps().getVersionAliases().iterator().next();
        Assert.assertNotNull(versionAlias.getConfig());
        Assert.assertEquals(1, versionAlias.getConfig().size());
        Assert.assertEquals("GENA-TEST-023", root.getMaps().getVersionAliases().iterator().next().getName());

        Configuration config = root.getMaps().getVersionAliases().iterator().next().getConfig().iterator().next();
        Assert.assertEquals("ROOT",config.getPart().getName());
        Assert.assertEquals("TEST ROOT", config.getPart().getKindOfPartName());
        Assert.assertEquals("TEST_FILES", config.getKoc().getExtensionTable());
        Assert.assertEquals("FILES", config.getKoc().getName());
        Assert.assertEquals("CONFIG_VERSION", config.getDataset().getVersion());
    }

    @Test
    public void testMakeKey() throws Exception {

        XmlManager xmlm = injector.getInstance(XmlManager.class);
        Root root = xmlm.unmarshal(new File("src/test/xml/09_addKeys.xml"));

        //Doesn't exist
        Assert.assertNull(root.getDatasets());
        Assert.assertNull(root.getMaps().getKeyAlias());

        //Does exist
        Assert.assertNotNull(root.getMaps());
        Assert.assertNotNull(root.getMaps().getKey());
        Assert.assertEquals(1, root.getMaps().getKey().size());
        Key key = root.getMaps().getKey().iterator().next();
        Assert.assertEquals("022-gena-test", key.getName());
        Assert.assertEquals("VGhlIHBvc2l0aXZlIFogYXhpcyBiYXJyZWwgaGFzIGJlZW4gdHVybmVkIG9mZg", key.getComment());
        Assert.assertEquals(1, key.getConfig().size());

        Configuration config = key.getConfig().get(0);
        Assert.assertEquals("ROOT",config.getPart().getName());
        Assert.assertEquals("TEST ROOT", config.getPart().getKindOfPartName());
        Assert.assertEquals("TEST_FILES", config.getKoc().getExtensionTable());
        Assert.assertEquals("FILES", config.getKoc().getName());
        Assert.assertEquals("CONFIG_VERSION", config.getDataset().getVersion());

    }

}
