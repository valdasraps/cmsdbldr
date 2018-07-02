package org.cern.cms.dbloader.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.LobManager;

import org.junit.Test;

/**
 * Test org.cern.cms.dbloader.manager.LobManager
 *
 * @author artiom
 *
 */
public class LobManagerTest extends TestBase {

    /*
     * PRIVATE buildPath() test
     */
    @Test
    public void test1() throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {

        LobManager lob = new LobManager();

        String path = null;
        String replacement = null;
        String replacable = null;

        Object[] args = new Object[]{path, replacable, replacement};
        Method method = lob.getClass().getDeclaredMethod("buildPath", String.class, String.class, String.class);
        method.setAccessible(true);

        assertNull(method.invoke(lob, args));

        path = "data.serial";
        replacable = "data.serial";
        replacement = "name.html";
        args = new Object[]{path, replacable, replacement};
        assertEquals(replacement, method.invoke(lob, args));

        path = "etc/temp/data.serial";
        replacement = "name.html";
        replacable = "data.serial";
        args = new Object[]{path, replacable, replacement};
        assertEquals("etc/temp/name.html", method.invoke(lob, args));

    }

    /*
     * PRIVATE fileProcessBlob(), fileProcessClob()  test
     */
    @Test
    public void test2() throws Exception {
        
        LobManager lob = new LobManager();
        Method methodClob = lob.getClass().getDeclaredMethod("fileProcessClob", String.class);
        Method methodBlob = lob.getClass().getDeclaredMethod("fileProcessBlob", String.class);
        methodClob.setAccessible(true);
        methodBlob.setAccessible(true);

        String path = "src/test/lob/data.html";
        assertNotNull(methodClob.invoke(lob, path));

        path = "src/test/lob/picture.png";
        assertNotNull(methodBlob.invoke(lob, path));

        path = null;
        assertNull(methodClob.invoke(lob, path));

        try {
            
            path = "";
            methodBlob.invoke(lob, path);
            fail();

        } catch (Exception e) {
            // OK
        }
            
        try {

            path = "src/test/lob/data00002";
            methodBlob.invoke(lob, path);
            fail();

        } catch (Exception e) {
            // OK
        }
            
        try {
            
            path = "";
            methodClob.invoke(lob, path);
            fail();

        } catch (Exception e) {
            // OK
        }
        
        try {
            lob.lobParser(null, null, null);
            fail();
        } catch (Exception e) {
            // OK!
        }

    }

}
