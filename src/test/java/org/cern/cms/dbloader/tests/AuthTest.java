package org.cern.cms.dbloader.tests;

import java.util.Arrays;
import java.util.Iterator;
import junit.framework.TestCase;

import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.util.NotAuthorizedException;
import org.cern.cms.dbloader.util.OperatorAuth;
import org.junit.Test;

public class AuthTest extends TestBase {

    @Test
    public void authExampleTest() throws Throwable {

        FilesManager fm = injector.getInstance(FilesManager.class);

        Iterator<FileBase> it = fm.getFiles(Arrays.asList(new String [] {
            "src/test/xml/02_condition.xml",
            "src/test/xml/01_construct.xml",
            "src/test/xml/shipment_1.xml"
        })).iterator();
        DbLoader loader = new DbLoader(pm);
        
        FileBase conditionFile = it.next();
        FileBase constructFile = it.next();
        FileBase shipmentFile  = it.next();

        try {
            loader.loadArchive(injector, constructFile, new OperatorAuth("test", "Test Username", false, true, false));
            TestCase.fail("Should not reach this");
        } catch (Exception ex) {
            
            TestCase.assertSame(NotAuthorizedException.class, ex.getClass());
            TestCase.assertSame("OPERATOR_CONSTRUCT_PERMISSION", ex.getMessage());
            
        }

        try {
            loader.loadArchive(injector, constructFile, new OperatorAuth("test", "Test Username", true, false, false));
        } catch (Exception ex) {
            
            TestCase.assertNotSame(NotAuthorizedException.class, ex.getClass());
            
        }
        
        try {
            loader.loadArchive(injector, conditionFile, new OperatorAuth("test", "Test Username", false, false, false));
            TestCase.fail("Should not reach this");
        } catch (Exception ex) {
            
            TestCase.assertSame(NotAuthorizedException.class, ex.getClass());
            TestCase.assertSame("OPERATOR_CONDITION_PERMISSION", ex.getMessage());
            
        }

        try {
            loader.loadArchive(injector, conditionFile, new OperatorAuth("test", "Test Username", false, true, false));
        } catch (Exception ex) {
            
            TestCase.assertNotSame(NotAuthorizedException.class, ex.getClass());
            
        }

        try {
            loader.loadArchive(injector, shipmentFile, new OperatorAuth("test", "Test Username", true, true, false));
            TestCase.fail("Should not reach this");
        } catch (Exception ex) {
            
            TestCase.assertSame(NotAuthorizedException.class, ex.getClass());
            TestCase.assertSame("OPERATOR_TRACKING_PERMISSION", ex.getMessage());
            
        }

        try {
            loader.loadArchive(injector, shipmentFile, new OperatorAuth("test", "Test Username", false, false, true));
        } catch (Exception ex) {
            
            TestCase.assertNotSame(NotAuthorizedException.class, ex.getClass());
            
        }
        
    }
    
}
