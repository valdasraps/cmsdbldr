package org.cern.cms.dbloader.tests;

import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.XmlManager;
import org.cern.cms.dbloader.model.config.Key;
import org.cern.cms.dbloader.model.xml.Configuration;
import org.cern.cms.dbloader.model.xml.Root;
import org.cern.cms.dbloader.model.xml.map.Maps;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;

/**
 * Created by aisi0860 on 5/26/17.
 */
public class ConfigXMLTest extends TestBase {

    @Test
    public void testAddAlias() throws Exception {

        XmlManager xmlm = injector.getInstance(XmlManager.class);
        Root root = xmlm.unmarshal(new File("src/test/xml/addAlias_1235599712-448068.xml"));

        //Doesn't exist
        Assert.assertNull(root.getDatasets());
        Assert.assertNull(root.getMaps().getKey());


        Assert.assertNotNull(root.getMaps());
        Assert.assertNotNull(root.getMaps().getKeyAlias());
        Assert.assertNotNull(root.getMaps().getKeyAlias().getKey());
        Assert.assertNotNull(root.getMaps().getKeyAlias().getKey().getVersionAlias());
        Assert.assertNotNull(root.getMaps().getKeyAlias().getKey().getConfig());


        Assert.assertEquals("022-al-gena-test", root.getMaps().getKeyAlias().getName());

        Key key = root.getMaps().getKeyAlias().getKey();
        Assert.assertEquals("022-gena-test", key.getName());
        Assert.assertEquals("GENA-TEST-022", key.getVersionAlias().getName());
        Assert.assertEquals("PIXEL_DETECTOR_CONFIG", key.getVersionAlias().getKoc().getExtensionTable());
        Assert.assertEquals("Pixel Detector Configuration", key.getVersionAlias().getKoc().getName());
        Assert.assertEquals(17, key.getConfig().size());

        Configuration config = root.getMaps().getKeyAlias().getKey().getConfig().get(0);
        Assert.assertEquals("CMS-PIXEL-ROOT",config.getPart().getName());
        Assert.assertEquals("Detector ROOT", config.getPart().getKindOfPartName());
        Assert.assertEquals("PIXEL_DETECTOR_CONFIG", config.getKoc().getExtensionTable());
        Assert.assertEquals("Pixel Detector Configuration", config.getKoc().getName());
        Assert.assertEquals("GENA-TEST-022", config.getDatasets().getVersion());

        config = root.getMaps().getKeyAlias().getKey().getConfig().get(1);
        Assert.assertEquals("CMS-PIXEL-ROOT",config.getPart().getName());
        Assert.assertEquals("Detector ROOT", config.getPart().getKindOfPartName());
        Assert.assertEquals("PIXEL_TTC_PARAMETERS", config.getKoc().getExtensionTable());
        Assert.assertEquals("TTC Configuration Parameters", config.getKoc().getName());
        Assert.assertEquals("3", config.getDatasets().getVersion());

    }

    @Test
    public void testAddVersionAlias() throws Exception {

        XmlManager xmlm = injector.getInstance(XmlManager.class);
        Root root = xmlm.unmarshal(new File("src/test/xml/addVersionAlias_1235599712-422015.xml"));

        //Doesn't exist
        Assert.assertNull(root.getDatasets());
        Assert.assertNull(root.getMaps().getKeyAlias());

        //Does exist
        Assert.assertNotNull(root.getMaps());
        Assert.assertNotNull(root.getMaps().getVersionAlias());
        Assert.assertNotNull(root.getMaps().getVersionAlias().getConfig());
        Assert.assertEquals(1, root.getMaps().getVersionAlias().getConfig().size());
        Assert.assertEquals("GENA-TEST-022", root.getMaps().getVersionAlias().getName());

        Configuration config = root.getMaps().getVersionAlias().getConfig().get(0);
        Assert.assertEquals("CMS-PIXEL-ROOT",config.getPart().getName());
        Assert.assertEquals("Detector ROOT", config.getPart().getKindOfPartName());
        Assert.assertEquals("PIXEL_DETECTOR_CONFIG", config.getKoc().getExtensionTable());
        Assert.assertEquals("Pixel Detector Configuration", config.getKoc().getName());
        Assert.assertEquals("GENA-TEST-022", config.getDatasets().getVersion());
    }

    @Test
    public void testMakeKey() throws Exception {

        XmlManager xmlm = injector.getInstance(XmlManager.class);
        Root root = xmlm.unmarshal(new File("src/test/xml/makeKey_1235599712-423246.xml"));

        //Doesn't exist
        Assert.assertNull(root.getDatasets());
        Assert.assertNull(root.getMaps().getKeyAlias());
        Assert.assertNull(root.getMaps().getVersionAlias());

        //Does exist
        Assert.assertNotNull(root.getMaps());
        Assert.assertNotNull(root.getMaps().getKey());
        Assert.assertEquals("022-gena-test", root.getMaps().getKey().getName());
        Assert.assertEquals("VGhlIHBvc2l0aXZlIFogYXhpcyBiYXJyZWwgaGFzIGJlZW4gdHVybmVkIG9mZg", root.getMaps().getKey().getComment());
        Assert.assertEquals(17, root.getMaps().getKey().getConfig().size());

        Configuration config = root.getMaps().getKey().getConfig().get(0);
        Assert.assertEquals("CMS-PIXEL-ROOT",config.getPart().getName());
        Assert.assertEquals("Detector ROOT", config.getPart().getKindOfPartName());
        Assert.assertEquals("PIXEL_DETECTOR_CONFIG", config.getKoc().getExtensionTable());
        Assert.assertEquals("Pixel Detector Configuration", config.getKoc().getName());
        Assert.assertEquals("GENA-TEST-022", config.getDatasets().getVersion());

        config = root.getMaps().getKey().getConfig().get(16);
        Assert.assertEquals("CMS-PIXEL-ROOT",config.getPart().getName());
        Assert.assertEquals("Detector ROOT", config.getPart().getKindOfPartName());
        Assert.assertEquals("FED_CONFIGURATION", config.getKoc().getExtensionTable());
        Assert.assertEquals("Pixel FED Configuration", config.getKoc().getName());
        Assert.assertEquals("296", config.getDatasets().getVersion());
    }

}
