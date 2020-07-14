package org.cern.cms.dbloader.tests;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.PropertiesManager;
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
                    pm1 = new PropertiesManager(load(pname), new String[] { "somefile.serial" }) {

                        @Override
                        public boolean printHelp() {
                            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }
                        
                    };
                    fail(String.format("Did not fail while loading: %s", pname));
                } catch (PropertiesException ex) {
                    // OK!
                }
            }
        }
        
    }
    
    @Test
    public void checkTestProperties() throws Exception {
        
    	String log_lvl;
        Properties props = new Properties();
        try (InputStream in = this.getClass().getResourceAsStream("/properties/test.properties")) {
        	props.load(in);
        }
    	
        assertEquals(2, pm.getArgs().size());
        assertTrue(pm.getArgs().contains("file1.serial"));
        assertTrue(pm.getArgs().contains("file2.serial"));
        
        
        assertEquals(props.getProperty("channel-class"), pm.getChannelClass());
        assertEquals(props.getProperty("channel-desc"), pm.getChannelDesc());
        
        
        assertFalse(pm.getCondDatasets().hasId());
        assertNull(pm.getCondDatasets().getId());
        assertEquals(props.getProperty("cond-datasets"), pm.getCondDatasets().getName());
        assertEquals(new BigInteger(props.getProperty("cond-dataset")), pm.getCondDataset());
        
        assertTrue(pm.getConditionClass().hasId());
        assertEquals(props.getProperty("cond-class"), pm.getConditionClass().getName());
        assertEquals(new BigInteger(props.getProperty("cond-class")), pm.getConditionClass().getId());
        
        
        assertFalse(pm.getConditionDesc().hasId());
        assertEquals(props.getProperty("cond-desc"), pm.getConditionDesc().getName());
        assertNull(pm.getConditionDesc().getId());
        
        assertFalse(pm.getConditionXml().hasId());
        assertEquals(props.getProperty("cond-xml"), pm.getConditionXml().getName());
        assertNull(pm.getConditionXml().getId());
        
        
        assertEquals(props.getProperty("attribute-core-schema"), pm.getCoreAttributeSchemaName());
        assertEquals(props.getProperty("condition-core-schema"), pm.getCoreConditionSchemaName());
        assertEquals(props.getProperty("condition-core-schema") + ".TABLE", pm.getCoreConditionTable("TABLE"));
        assertEquals(props.getProperty("construct-core-schema"), pm.getCoreConstructSchemaName());
        assertEquals(props.getProperty("construct-core-schema") + ".TABLE", pm.getCoreConstructTable("TABLE"));
        assertEquals(props.getProperty("managemnt-core-schema"), pm.getCoreManagemntSchemaName());
        assertEquals(props.getProperty("condition-ext-schema"), pm.getExtConditionSchemaName());
        assertEquals(props.getProperty("condition-ext-schema") + ".TABLE", pm.getExtConditionTable("TABLE"));
        assertEquals(props.getProperty("construct-ext-schema"), pm.getExtConstructSchemaName());
        assertEquals(props.getProperty("construct-ext-schema") + ".TABLE", pm.getExtConstructTable("TABLE"));
        assertEquals(props.getProperty("iov-core-schema"), pm.getIovCoreSchemaName());
        assertEquals(props.getProperty("view-ext-schema"), pm.getExtViewSchemaName());
        
        log_lvl = props.getProperty("log-level");
        assertEquals(InetAddress.getLocalHost().getHostName(), pm.getHostName());
        assertEquals(Level.toLevel(log_lvl), pm.getLogLevel());
        assertEquals(System.getProperty("user.name"), pm.getOsUser());
        assertEquals(props.getProperty("operator-username"), pm.getOperatorAuth().getUsername());
        assertEquals(props.getProperty("operator-fullname"), pm.getOperatorAuth().getFullname());
        assertTrue(pm.getOperatorAuth().isConditionPermission());
        assertTrue(pm.getOperatorAuth().isConstructPermission());
        assertTrue(pm.getOperatorAuth().isTrackingPermission());
        assertNotNull(pm.getVersion());
        
        assertEquals(props.getProperty("password"), pm.getPassword());
        assertEquals(new BigInteger(props.getProperty("root-part-id")), pm.getRootPartId());
        assertEquals(props.getProperty("schema"), pm.getSchemaParent());
        assertEquals("jdbc:oracle:thin:@" + props.getProperty("url"), pm.getUrl());
        assertEquals(props.getProperty("username"), pm.getUsername());
        assertTrue(pm.isChannelClass());
        assertTrue(pm.isChannelDesc());
        assertTrue(pm.isChannelsList());
        assertTrue(pm.isCondDataset());
        assertTrue(pm.isCondDatasets());
        assertTrue(pm.isConditionClass());
        assertTrue(pm.isConditionDesc());
        assertTrue(pm.isConditionXml());
        assertTrue(pm.isConditionsList());
        if (System.getenv("TRAVIS_CI") != null) {
            assertFalse(pm.isPrintSQL());
        } else {
            assertTrue(pm.isPrintSQL());
        }
        assertTrue(pm.isSchema());
        assertFalse(pm.isTest());
        
        assertTrue(pm.getOperatorAuth().isConstructPermission());
        assertTrue(pm.getOperatorAuth().isConditionPermission());
        assertTrue(pm.getOperatorAuth().isTrackingPermission());
        
    }
    
}
