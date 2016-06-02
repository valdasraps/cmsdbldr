package org.cern.cms.dbloader.tests;

import java.math.BigDecimal;
import static junit.framework.TestCase.fail;
import static junit.framework.TestCase.assertEquals;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.CondManager;
import org.cern.cms.dbloader.manager.PropertyType;
import org.cern.cms.dbloader.manager.XmlManager;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.PropertyHandler;
import org.junit.Test;

public class CondInfoTest extends TestBase {
    
    @Test
    public void checkXml() throws Exception {
        XmlManager xmlm = injector.getInstance(XmlManager.class);
        xmlm.generateSchema("target");
    }
    
    @Test
    public void checkCondEntityHandler() throws Exception {
        
        CondManager condm = injector.getInstance(CondManager.class);
        for (CondEntityHandler ceh : condm.getConditionHandlers()) {
            switch (ceh.getName()) {
                case "IV":
                    assertEquals("TEST_IV", ceh.getTableName());
                    assertEquals("org.cern.cms.dbloader.model.condition.ext.TestIv", ceh.getClassName());
                    for (PropertyHandler ph: ceh.getProperties()) {
                        switch (ph.getName()) {
                            case "voltage":
                                assertEquals("java.math.BigDecimal", ph.getClassName());
                                assertEquals(java.math.BigDecimal.class, ph.getPropertyClass());
                                assertEquals("VOLTAGE", ph.getColumnName());
                                assertEquals(PropertyType.OTHER, ph.getType());
                                break;
                            case "curr":
                                assertEquals("java.math.BigDecimal", ph.getClassName());
                                assertEquals(java.math.BigDecimal.class, ph.getPropertyClass());
                                assertEquals("CURR", ph.getColumnName());
                                assertEquals(PropertyType.OTHER, ph.getType());
                                break;
                           case "commentDescription":
                                assertEquals("java.lang.String", ph.getClassName());
                                assertEquals(String.class, ph.getPropertyClass());
                                assertEquals("COMMENT_DESCRIPTION", ph.getColumnName());
                                assertEquals(PropertyType.OTHER, ph.getType());
                                break;
                            default:
                                fail(String.format("Unexpected %s condition property found: %s", ceh.getName(), ph.getName()));
                        }
                    }
                    break;
                case "FILES":
                    assertEquals("TEST_FILES", ceh.getTableName());
                    assertEquals("org.cern.cms.dbloader.model.condition.ext.TestFiles", ceh.getClassName());
                    for (PropertyHandler ph: ceh.getProperties()) {
                        switch (ph.getName()) {
                            case "testTextFile":
                                assertEquals("java.lang.String", ph.getClassName());
                                assertEquals(String.class, ph.getPropertyClass());
                                assertEquals("TEST_TEXT_FILE", ph.getColumnName());
                                assertEquals(PropertyType.OTHER, ph.getType());
                                break;
                            case "testMediaFile":
                                assertEquals("java.lang.String", ph.getClassName());
                                assertEquals(String.class, ph.getPropertyClass());
                                assertEquals("TEST_MEDIA_FILE", ph.getColumnName());
                                assertEquals(PropertyType.OTHER, ph.getType());
                                break;
                            case "testTextClob":
                                assertEquals("java.lang.String", ph.getClassName());
                                assertEquals(String.class, ph.getPropertyClass());
                                assertEquals("TEST_TEXT_CLOB", ph.getColumnName());
                                assertEquals(PropertyType.CLOB, ph.getType());
                                break;
                            case "testMediaBlob":
                                assertEquals("[B", ph.getClassName());
                                assertEquals(byte[].class, ph.getPropertyClass());
                                assertEquals("TEST_MEDIA_BLOB", ph.getColumnName());
                                assertEquals(PropertyType.BLOB, ph.getType());
                                break;
                           case "testComment":
                                assertEquals("java.lang.String", ph.getClassName());
                                assertEquals(String.class, ph.getPropertyClass());
                                assertEquals("TEST_COMMENT", ph.getColumnName());
                                assertEquals(PropertyType.OTHER, ph.getType());
                                break;
                            default:
                                fail(String.format("Unexpected %s condition property found: %s", ceh.getName(), ph.getName()));
                        }
                    }
                    break;
                default:
                    fail(String.format("Unexpected condition type found: %s", ceh.getName()));
                    break;
            }
        }
        
    }
    
    @Test
    public void checkChannelHandlers() throws Exception {
        
        Logger.getLogger("java.sql.ResultSet").setLevel(Level.TRACE);
        
        CondManager condm = injector.getInstance(CondManager.class);
        ChannelEntityHandler ceh = condm.getChannelHandler("TEST_CHANNELS");
        if (ceh == null) {
            condm.registerChannelEntityHandler("TEST_CHANNELS");
            ceh = condm.getChannelHandler("TEST_CHANNELS");
        }
        
        assertEquals("TEST_CHANNELS", ceh.getTableName());
        assertEquals("org.cern.cms.dbloader.model.condition.ext.TestChannels", ceh.getClassName());
        
        for (PropertyHandler ph: ceh.getProperties()) {
            switch (ph.getName()) {
                case "subdet":
                    assertEquals("java.lang.String", ph.getClassName());
                    assertEquals(String.class, ph.getPropertyClass());
                    assertEquals("SUBDET", ph.getColumnName());
                    assertEquals(PropertyType.OTHER, ph.getType());
                    break;
                case "ieta":
                    assertEquals("java.math.BigDecimal", ph.getClassName());
                    assertEquals(BigDecimal.class, ph.getPropertyClass());
                    assertEquals("IETA", ph.getColumnName());
                    assertEquals(PropertyType.OTHER, ph.getType());
                    break;
                case "iphi":
                    assertEquals("java.math.BigDecimal", ph.getClassName());
                    assertEquals(BigDecimal.class, ph.getPropertyClass());
                    assertEquals("IPHI", ph.getColumnName());
                    assertEquals(PropertyType.OTHER, ph.getType());
                    break;
                case "depth":
                    assertEquals("java.math.BigDecimal", ph.getClassName());
                    assertEquals(BigDecimal.class, ph.getPropertyClass());
                    assertEquals("DEPTH", ph.getColumnName());
                    assertEquals(PropertyType.OTHER, ph.getType());
                    break;
                default:
                    fail(String.format("Unexpected %s channel property found: %s", ceh.getTableName(), ph.getName()));
            }
        }
        
    }
    
}
