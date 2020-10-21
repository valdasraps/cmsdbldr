package org.cern.cms.dbloader.tests;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import javax.management.modelmbean.XMLParseException;

import static junit.framework.TestCase.assertEquals;
import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Test;

public class CondAppendTest extends TestBase {
    
    /**
     * First 3 rows should pass easily
     * @throws Throwable 
     */
    @Test
    public void appendData1Test() throws Throwable {
        
        executeTest("src/test/xml/17_condition.xml", new BiConsumer<Session,Class>() {
            @Override
            public void accept(Session session, Class clazz) {
                Dataset ds = (Dataset) session.createCriteria(Dataset.class)
                            .add(Restrictions.eq("version", "AppendTest"))
                            .createCriteria("kindOfCondition")
                                .add(Restrictions.eq("name", "IV"))
                            .uniqueResult();
                assertEquals("3", ds.getSubversion());
                assertEquals(0, ds.getAttrList().size());

                List<CondBase> data = (List<CondBase>) session.createCriteria(clazz)
                            .add(Restrictions.eq("dataset", ds))
                            .list();
                assertEquals(3, data.size());
            }
        });

    }
     
    /**
     * Second run should fail (append=false)
     * @throws Throwable 
     */
    @Test
    public void appendData2Test() throws Throwable {
        
        try {

            executeTest("src/test/xml/17_condition.xml", new BiConsumer<Session,Class>() {
            @Override
            public void accept(Session session, Class clazz) {  }
            });

            Assert.fail("This should not be reached as we expect to fail same dataset insertion, man");

        } catch (Exception ex) {

            assertEquals(XMLParseException.class, ex.getClass());
            Assert.assertTrue(ex.getMessage().contains("Dataset already exists"));

        }
                
    }

    /**
     * Third run should add 3 more rows (append=true)
     * @throws Throwable 
     */
    @Test
    public void appendData3Test() throws Throwable {

        executeTest("src/test/xml/18_condition.xml", new BiConsumer<Session,Class>() {
            @Override
            public void accept(Session session, Class clazz) {

                Dataset ds = (Dataset) session.createCriteria(Dataset.class)
                            .add(Restrictions.eq("version", "AppendTest"))
                            .createCriteria("kindOfCondition")
                                .add(Restrictions.eq("name", "IV"))
                            .uniqueResult();
                assertEquals("3", ds.getSubversion());
                assertEquals(1, ds.getAttrList().size());

                List<CondBase> data = (List<CondBase>) session.createCriteria(clazz)
                            .add(Restrictions.eq("dataset", ds))
                            .list();
                assertEquals(6, data.size());

            }
        });
        
    }
    
    /**
     * Third run should add 3 more rows (append=true)
     * @throws Throwable 
     */
    @Test
    public void appendData4Test() throws Throwable {

        executeTest("src/test/xml/18_condition.xml", new BiConsumer<Session,Class>() {
            @Override
            public void accept(Session session, Class clazz) {

                Dataset ds = (Dataset) session.createCriteria(Dataset.class)
                            .add(Restrictions.eq("version", "AppendTest"))
                            .createCriteria("kindOfCondition")
                                .add(Restrictions.eq("name", "IV"))
                            .uniqueResult();
                assertEquals("3", ds.getSubversion());

                assertEquals(1, ds.getAttrList().size());

                List<CondBase> data = (List<CondBase>) session.createCriteria(clazz)
                            .add(Restrictions.eq("dataset", ds))
                            .list();
                assertEquals(9, data.size());

            }
        });
        
    }
    
    private void executeTest(String filename, BiConsumer<Session,Class> test) throws Throwable {
        FilesManager fm = injector.getInstance(FilesManager.class);

        DbLoader loader = new DbLoader(pm);
        for (FileBase fb: fm.getFiles(Collections.singletonList(filename))) {

            loader.loadArchive(injector, fb, pm.getOperatorAuth());

        }

        try (HbmManager hbm = injector.getInstance(HbmManager.class)) {
            Session session = hbm.getSession();
            try {

                final Class<? extends CondBase> clazz = (Class<? extends CondBase>) 
                    Class.forName("org.cern.cms.dbloader.model.condition.ext.TestIv");

                test.accept(session, clazz);

            } finally {
                session.close();
            }

        }
        
    }
    
}
