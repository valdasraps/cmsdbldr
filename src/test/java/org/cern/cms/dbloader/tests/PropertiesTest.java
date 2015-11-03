package org.cern.cms.dbloader.tests;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.cern.cms.dbloader.PropertiesManager;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.CLIPropertiesManager;
import org.cern.cms.dbloader.util.PropertiesException;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;

public class PropertiesTest extends TestBase {

    @Test
    public void checkFailingProperties() throws Exception {
        
        PropertiesManager pm1;
        final Reflections reflFail = new Reflections(ClasspathHelper.forPackage("properties.fail"), new ResourcesScanner());
        
        for (String pname: reflFail.getResources(Pattern.compile(".*\\.properties"))) {
            if (pname.contains("fail")) {
                pname = "/" + pname;
                try {
                    pm1 = new CLIPropertiesManager(load(pname), new String[] { "somefile.xml" });
                    fail(String.format("Did not fail while loading: %s", pname));
                } catch (PropertiesException ex) {
                    // OK!
                }
            }
        }
        
    }
    
    @Test
    public void checkTestProperties() throws Exception {
        
        assertEquals(2, pm.getArgs().size());
        assertTrue(pm.getArgs().contains("file1.xml"));
        assertTrue(pm.getArgs().contains("file2.xml"));
        
        assertEquals("channel-class", pm.getChannelClass());
        assertEquals("channel-desc", pm.getChannelDesc());
        
        assertFalse(pm.getCondDatasets().hasId());
        assertNull(pm.getCondDatasets().getId());
        assertEquals("cond-datasets", pm.getCondDatasets().getName());
        assertEquals(new BigInteger("12345"), pm.getCondDataset());

        assertTrue(pm.getConditionClass().hasId());
        assertEquals("12345", pm.getConditionClass().getName());
        assertEquals(new BigInteger("12345"), pm.getConditionClass().getId());
        
        assertFalse(pm.getConditionDesc().hasId());
        assertEquals("cond-desc", pm.getConditionDesc().getName());
        assertNull(pm.getConditionDesc().getId());
        
        assertFalse(pm.getConditionXml().hasId());
        assertEquals("cond-xml", pm.getConditionXml().getName());
        assertNull(pm.getConditionXml().getId());
        
        assertEquals("CMS_TST_CORE_ATTRIBUTE", pm.getCoreAttributeSchemaName());
        assertEquals("CMS_TST_CORE_COND", pm.getCoreConditionSchemaName());
        assertEquals("CMS_TST_CORE_COND.TABLE", pm.getCoreConditionTable("TABLE"));
        assertEquals("CMS_TST_CORE_CONSTRUCT", pm.getCoreConstructSchemaName());
        assertEquals("CMS_TST_CORE_CONSTRUCT.TABLE", pm.getCoreConstructTable("TABLE"));
        assertEquals("CMS_TST_CORE_MANAGEMNT", pm.getCoreManagemntSchemaName());
        assertEquals("CMS_TST_TEST_COND", pm.getExtConditionSchemaName());
        assertEquals("CMS_TST_TEST_COND.TABLE", pm.getExtConditionTable("TABLE"));
        assertEquals("CMS_TST_TEST_CONSTRUCT", pm.getExtConstructSchemaName());
        assertEquals("CMS_TST_TEST_CONSTRUCT.TABLE", pm.getExtConstructTable("TABLE"));
        assertEquals("CMS_TST_CORE_IOV_MGMNT", pm.getIovCoreSchemaName());
        
        assertEquals(InetAddress.getLocalHost().getHostName(), pm.getHostName());
        assertEquals(Level.INFO, pm.getLogLevel());
        assertEquals(System.getProperty("user.name"), pm.getOsUser());
        assertNotNull(pm.getVersion());
        
        assertEquals("testing", pm.getPassword());
        assertEquals(new BigInteger("1000"), pm.getRootPartId());
        assertEquals("schema", pm.getSchemaParent());
        assertEquals("jdbc:oracle:thin:@localhost:1521:XE", pm.getUrl());
        assertEquals("CMS_TST_PRTTYPE_TEST_WRITER", pm.getUsername());
        
        assertTrue(pm.isChannelClass());
        assertTrue(pm.isChannelDesc());
        assertTrue(pm.isChannelsList());
        assertTrue(pm.isCondDataset());
        assertTrue(pm.isCondDatasets());
        assertTrue(pm.isConditionClass());
        assertTrue(pm.isConditionDesc());
        assertTrue(pm.isConditionXml());
        assertTrue(pm.isConditionsList());
        assertTrue(pm.isPrintSQL());
        assertTrue(pm.isSchema());
        assertFalse(pm.isTest());
        
    }
    
}
