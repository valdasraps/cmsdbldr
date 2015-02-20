package org.cern.cms.dbloader.manager;

import static org.junit.Assert.fail;
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
	@SuppressWarnings("unused")
	@Ignore
	public void test1() {
		LobManager lob = new LobManager();
		String path = null;
		String replacement = null;
		String replacable = null;
//		assertEquals(null, lob.buildPath(path, replacement, replacable));
		
		path = "";
		replacement = "";
		replacable = "";
//		assertEquals(null, lob.buildPath(path, replacement, replacable));
		
		path = "data.xml";
		replacement = "data.xml";
		replacable = "name.html";
//		assertEquals("name.html", lob.buildPath(path, replacement, replacable));
		
		path = "etc/temp/data.xml";
		replacement = "data.xml";
		replacable = "name.html";
//		assertEquals("etc/temp/name.html", lob.buildPath(path, replacement, replacable));
		
//		path not provided not specified
//		path = "etc/temp";
//		assertEquals(name, lob.buildPath(path, name));
	}
	
	/*
	 * PRIVATE fileProcessBlob(), fileProcessClob()  test
	 */
	@Test
	@Ignore
	@SuppressWarnings("unused")
	public void test2() {
		LobManager lob = new LobManager();
		String path = "/home/artiom/Skype/html_b904/data00001.html";
//		try {
//			assertNotNull(lob.fileProcessClob(path));
//			path = "/home/artiom/Skype/html_b904/data00004.html";
//			
//			assertNotNull(lob.fileProcessClob(path));
//			path = "";
//			lob.fileProcessClob(path);
//			fail();
//			
//			path = null;
//			lob.fileProcessClob(path);
//			fail();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			path = "/home/artiom/Skype/files_fnal_testing/data00002.png";
//			lob.fileProcessBlob(path);
//			
//			path = "/home/artiom/Skype/files_fnal_testing/data00002.dat";
//			lob.fileProcessBlob(path);
//			
//			path = "";
//			lob.fileProcessBlob(path);
//			fail();
//			
//			path = null;
//			assertNull(lob.fileProcessBlob(path));
//			
//			path = "/home/artiom/Skype/files_fnal_testing/data00002";
//			lob.fileProcessBlob(path);
//			fail();
//			
//			
//		} catch (IOException e) { 
//			e.printStackTrace();
//		}
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
