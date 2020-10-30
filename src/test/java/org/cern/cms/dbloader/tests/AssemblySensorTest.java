package org.cern.cms.dbloader.tests;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import junit.framework.TestCase;
import org.cern.cms.dbloader.AssemblyBase;

import org.cern.cms.dbloader.model.construct.PartAttrList;
import org.cern.cms.dbloader.model.construct.ext.AssemblyPart;
import org.cern.cms.dbloader.model.construct.ext.AssemblyStep;
import org.cern.cms.dbloader.model.construct.ext.AssemblyStepDefiniton;
import org.cern.cms.dbloader.model.construct.ext.AssemblyStepStatus;
import org.cern.cms.dbloader.model.serial.map.AttrBase;
import org.cern.cms.dbloader.model.serial.map.AttrCatalog;
import org.cern.cms.dbloader.model.serial.map.Attribute;
import org.cern.cms.dbloader.model.serial.map.PositionSchema;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Test;


public class AssemblySensorTest extends AssemblyBase {

    private final static File JSON_FILE_BASE = new File("src/test/zip/assembly_sensor.json");
    private final static File DATA_FILE_BASE = new File("src/test/zip/assembly_sensor.csv");
    
    private final static String DATASET_VERSION = "1.0";
    
    @Test
    public void step10Test() throws Throwable {

        stepTest(JSON_FILE_BASE, DATA_FILE_BASE, 
        new Consumer<AssemblyStep>() {
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
            stepTest(JSON_FILE_BASE, DATA_FILE_BASE, 
            new Consumer<AssemblyStep>() {
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
        
        stepTest(JSON_FILE_BASE, DATA_FILE_BASE, 
        new Consumer<AssemblyStep>() {
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
        
        stepTest(JSON_FILE_BASE, DATA_FILE_BASE, 
        new Consumer<AssemblyStep>() {
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
        
        stepTest(JSON_FILE_BASE, DATA_FILE_BASE, 
        new Consumer<AssemblyStep>() {
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
        
        stepTest(JSON_FILE_BASE, DATA_FILE_BASE, 
        new Consumer<AssemblyStep>() {
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

}


