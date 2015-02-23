package org.cern.cms.dbloader.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Test org.cern.cms.dbloader.manager.LobManager
 * @author artiom
 *
 */
public class LobManagerT {

	/*
	 * PRIVATE buildPath() test
	 */
	@Test
	public void test1() throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
		
		LobManager lob = new LobManager();
		
		String path = null;
		String replacement = null;
		String replacable = null;
		
		Object[] args = new Object[] {path, replacable, replacement};
		Method method = lob.getClass().getDeclaredMethod("buildPath", String.class, String.class, String.class);
		method.setAccessible(true);
		
		assertNull(method.invoke(lob, args));
		
		path = "data.xml";
		replacable = "data.xml";
		replacement = "name.html";
		args = new Object[] {path, replacable, replacement};
		assertEquals(replacement, method.invoke(lob, args));
		
		path = "etc/temp/data.xml";
		replacement = "name.html";
		replacable = "data.xml";
		args = new Object[] {path, replacable, replacement};
		assertEquals("etc/temp/name.html", method.invoke(lob, args));
		
	}
	
	/*
	 * PRIVATE fileProcessBlob(), fileProcessClob()  test
	 */
	@Test
	@SuppressWarnings("unused")
	public void test2() throws NoSuchMethodException, SecurityException {
		LobManager lob = new LobManager();
		Method methodClob = lob.getClass().getDeclaredMethod("fileProcessClob", String.class);
		Method methodBlob = lob.getClass().getDeclaredMethod("fileProcessBlob", String.class);
		methodClob.setAccessible(true);
		methodBlob.setAccessible(true);
		try {
			
		    String path = "src/test/resource/data00001.html";
			assertNotNull(methodClob.invoke(lob, path));
			
			path = "src/test/resource/data00004.html";
			assertNotNull(methodClob.invoke(lob, path));
			
			path = "";
			methodClob.invoke(lob, path);
			fail();
			
			path = null;
			methodClob.invoke(lob, path);
			fail();
			
			path = "src/test/resource/data00005.png";
			assertNotNull(methodBlob.invoke(lob, path));
			
			path = "src/test/resource/data00002.dat";
			assertNotNull(methodBlob.invoke(lob, path));
			
			path = "";
			methodBlob.invoke(lob, path);
			fail();
			
			path = null;
			assertNull(methodClob.invoke(lob, path));
			
			path = "src/test/resource/data00002";
			methodBlob.invoke(lob, path);
			fail();
			
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) { 
			e.printStackTrace();
		}
	}
	
	/*
	 * PUBLIC lobParser() test
	 */
	@Test
	public void test3() {
		LobManager lob = new LobManager();
		try { 
			lob.lobParser(null, null, null);
			fail();
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
}
