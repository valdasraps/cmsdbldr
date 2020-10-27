package org.cern.cms.dbloader.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import junit.framework.TestCase;

import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.model.construct.PartAttrList;
import org.cern.cms.dbloader.model.construct.ext.AssemblyData;
import org.cern.cms.dbloader.model.construct.ext.AssemblyPart;
import org.cern.cms.dbloader.model.construct.ext.AssemblyPartDefiniton;
import org.cern.cms.dbloader.model.construct.ext.AssemblyStep;
import org.cern.cms.dbloader.model.construct.ext.AssemblyStepDefiniton;
import org.cern.cms.dbloader.model.construct.ext.AssemblyStepStatus;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.managemnt.UploadStatus;
import org.cern.cms.dbloader.model.serial.Root;
import org.cern.cms.dbloader.model.serial.map.AttrBase;
import org.cern.cms.dbloader.model.serial.map.AttrCatalog;
import org.cern.cms.dbloader.model.serial.map.Attribute;
import org.cern.cms.dbloader.model.serial.map.PositionSchema;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Test;


public class AssemblyZipTest extends TestBase {

    private final static File JSON_FILE_BASE = new File("src/test/zip/assembly_1.json");
    private final static File DATA_FILE_BASE = new File("src/test/zip/assembly_1.csv");
    
    private final static String DATASET_VERSION = "1.0";
    
    @Test
    public void step10Test() throws Throwable {

        stepTest(new Consumer<AssemblyStep>() {
            @Override
            public void accept(AssemblyStep step) {
                step.setNumber(1);
                AssemblyPart prod = step.getAssemblyParts().iterator().next();
                prod.getAssemblyData().iterator().next().setVersion(DATASET_VERSION);
            }
        }, new BiConsumer<AssemblyStep, Session>() {
            @Override
            public void accept(AssemblyStep step, Session session) {
                AssemblyStepDefiniton stepDef = step.getStepDefinition();
                Assert.assertEquals((Integer) 1, stepDef.getNumber());
                Assert.assertEquals(AssemblyStepStatus.COMPLETED, step.getStatus());
            }
        });
    }

    @Test
    public void step11Test() throws Throwable {
        try {
            stepTest(new Consumer<AssemblyStep>() {
                @Override
                public void accept(AssemblyStep step) {
                    step.setNumber(1);
                    AssemblyPart prod = step.getAssemblyParts().iterator().next();
                    prod.getAssemblyData().iterator().next().setVersion(DATASET_VERSION);
                }
            }, new BiConsumer<AssemblyStep, Session>() {
                @Override
                public void accept(AssemblyStep step, Session session) {
                    Assert.fail("Should not reach this...");
                }
            });
        } catch (IllegalArgumentException ex) {
            // Good!
            TestCase.assertTrue(ex.getMessage().startsWith("Current Assembly step (2) does not match:"));
        }
    }
    
    @Test
    public void step20Test() throws Throwable {
        
        stepTest(new Consumer<AssemblyStep>() {
            @Override
            public void accept(AssemblyStep step) {
                step.setNumber(2);
                step.setStatus(AssemblyStepStatus.IN_PROGRESS);
                AssemblyPart prod = step.getAssemblyParts().iterator().next();
                prod.getAssemblyData().iterator().next().setVersion(DATASET_VERSION);
            }
        }, new BiConsumer<AssemblyStep, Session>() {
            @Override
            public void accept(AssemblyStep step, Session session) {
                AssemblyStepDefiniton stepDef = step.getStepDefinition();
                Assert.assertEquals((Integer) 2, stepDef.getNumber());
                Assert.assertEquals(AssemblyStepStatus.IN_PROGRESS, step.getStatus());
            }
        });
    }
    
    @Test
    public void step21Test() throws Throwable {
        
        stepTest(new Consumer<AssemblyStep>() {
            @Override
            public void accept(AssemblyStep step) {
                step.setNumber(2);
                AssemblyPart prod = step.getAssemblyParts().iterator().next();
                prod.getAssemblyData().clear();
            }
        }, new BiConsumer<AssemblyStep, Session>() {
            @Override
            public void accept(AssemblyStep step, Session session) {
                AssemblyStepDefiniton stepDef = step.getStepDefinition();
                Assert.assertEquals((Integer) 2, stepDef.getNumber());
                Assert.assertEquals(AssemblyStepStatus.COMPLETED, step.getStatus());
            }
        });
    }
        
    @Test
    public void step30Test() throws Throwable {
        
        stepTest(new Consumer<AssemblyStep>() {
            @Override
            public void accept(AssemblyStep step) {
                step.setNumber(3);
                AssemblyPart prod = step.getAssemblyParts().iterator().next();
                prod.getAssemblyData().iterator().next().setVersion(DATASET_VERSION);
            }
        }, new BiConsumer<AssemblyStep, Session>() {
            @Override
            public void accept(AssemblyStep step, Session session) {
                AssemblyStepDefiniton stepDef = step.getStepDefinition();
                Assert.assertEquals((Integer) 3, stepDef.getNumber());
                Assert.assertEquals(AssemblyStepStatus.COMPLETED, step.getStatus());
            }
        });
    }
        
    @Test
    public void step40Test() throws Throwable {
        
        final Attribute attr = new Attribute();
        attr.setName("Construction phase");
        attr.setValue("Prepared for assembly");
        
        stepTest(new Consumer<AssemblyStep>() {
            @Override
            public void accept(AssemblyStep step) {
                step.setNumber(4);
                AssemblyPart prod = step.getAssemblyParts().iterator().next();
                prod.getAssemblyData().iterator().next().setVersion(DATASET_VERSION);
                prod.getPart().addAttributes(attr);
            }
        }, new BiConsumer<AssemblyStep, Session>() {
            @Override
            public void accept(AssemblyStep step, Session session) {
                
                AssemblyStepDefiniton stepDef = step.getStepDefinition();
                Assert.assertEquals((Integer) 4, stepDef.getNumber());
                
                AttrCatalog catalog = (AttrCatalog) session.createCriteria(AttrCatalog.class)
                    .add(Restrictions.eq("deleted", Boolean.FALSE))
                    .add(Restrictions.eq("name", attr.getName()))
                    .uniqueResult();
             
                AttrBase base = (AttrBase) session.createCriteria(PositionSchema.class)
                        .add(Restrictions.eq("name", attr.getValue()))
                        .add(Restrictions.eq("attrCatalog", catalog))
                        .add(Restrictions.eq("deleted", Boolean.FALSE))
                        .uniqueResult();
                
                PartAttrList partAttrList = (PartAttrList) session.createCriteria(PartAttrList.class)
                        .add(Restrictions.eq("deleted", Boolean.FALSE))
                        .add(Restrictions.eq("attrBase", base))
                        .add(Restrictions.eq("part", step.getPart()))
                        .uniqueResult();
                
                Assert.assertNotNull(partAttrList);
        
            }
        });
        
    }
    
    public void stepTest(Consumer<AssemblyStep> update, BiConsumer<AssemblyStep, Session> check) throws Throwable {
        
        Root root = readJSONFile(JSON_FILE_BASE);
        
        AssemblyStep step = root.getAssemblySteps().iterator().next();
        update.accept(step);
        AssemblyPart prod = step.getAssemblyParts().iterator().next();
        
        File zipFile = upload(root);
        
        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            Criteria c = session.createCriteria(AssemblyStep.class);
            c.createCriteria("stepDefinition").add(Restrictions.eq("number", step.getNumber()));
            c.createCriteria("part").add(Restrictions.eq("id", prod.getPart().getId()));
            AssemblyStep astep = (AssemblyStep) c.uniqueResult();

            for (AssemblyPart apart: astep.getAssemblyParts()) {
                if (apart.getPartDefinition().getType() == AssemblyPartDefiniton.AssemblyPartType.PRODUCT) {
                    
                    Iterator<AssemblyData> it = apart.getAssemblyData().iterator();
                    if (it.hasNext()) {
                        AssemblyData adata = it.next();
                        if (prod.getAssemblyData().iterator().hasNext()) {
                            Assert.assertEquals(prod.getAssemblyData().iterator().next().getVersion(), adata.getDataset().getVersion());
                        }
                    } else {
                        Assert.assertFalse(prod.getAssemblyData().iterator().hasNext());
                    }
                    
                }
            }
            
            check.accept(astep, session);
            if (prod.getAssemblyData().iterator().hasNext()) {
                AuditLog alog = (AuditLog) session.createCriteria(AuditLog.class)
                    .add(Restrictions.eq("archiveFileName", zipFile.getName()))
                        .add(Restrictions.eq("version", prod.getAssemblyData().iterator().next().getVersion()))
                    .uniqueResult();
                Assert.assertEquals(UploadStatus.Success, alog.getStatus());
            }

        }
        
    }
    
    private File upload(Root root) throws Throwable {
            
        FilesManager fm = injector.getInstance(FilesManager.class);
        File zipFile = fm.createZip(writeJSONFile(root), DATA_FILE_BASE);
        DbLoader loader = new DbLoader(pm);
        for (FileBase fb: fm.getFiles(Collections.singletonList(zipFile.getAbsolutePath()))) {

            loader.loadArchive(injector, fb, pm.getOperatorAuth());

        }
        
        return zipFile;
                
    }
    
    private static Root readJSONFile(File f) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(f, Root.class);
    }
    
    private static File writeJSONFile(Root root) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File f = File.createTempFile("assembly_", ".json");
        objectMapper.writeValue(f, root);
        return f;
    }

}


