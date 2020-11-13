package org.cern.cms.dbloader.tests;

import java.io.File;
import java.math.BigInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import junit.framework.TestCase;
import org.cern.cms.dbloader.AssemblyBase;

import org.cern.cms.dbloader.model.construct.ext.AssemblyStep;
import org.cern.cms.dbloader.model.construct.ext.AssemblyStepDefiniton;
import org.cern.cms.dbloader.model.construct.ext.AssemblyStepStatus;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;


public class AssemblyModuleTest extends AssemblyBase {

    private final static File JSON_FILE_BASE_1 = new File("src/test/zip/assembly_module1.json");
    private final static File JSON_FILE_BASE_2 = new File("src/test/zip/assembly_module2.json");
    private final static File JSON_FILE_BASE_3 = new File("src/test/zip/assembly_module3.json");
    private final static File JSON_FILE_BASE_4 = new File("src/test/zip/assembly_module4.json");
    private final static File JSON_FILE_BASE_5 = new File("src/test/zip/assembly_module5.json");
    private final static File DATA_FILE_BASE = new File("src/test/zip/assembly_data.csv");
    
    @Test
    public void step10Test() throws Throwable {
        stepTest(JSON_FILE_BASE_1, DATA_FILE_BASE, 
        new Consumer<AssemblyStep>() {
            @Override
            public void accept(AssemblyStep step) { }
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
    public void step20Test() throws Throwable {
        stepTest(JSON_FILE_BASE_2, DATA_FILE_BASE, 
        new Consumer<AssemblyStep>() {
            @Override
            public void accept(AssemblyStep step) { }
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
        try {
            
            stepTest(JSON_FILE_BASE_3, DATA_FILE_BASE, 
            new Consumer<AssemblyStep>() {
                @Override
                public void accept(AssemblyStep step) {
                    step.getAssemblyParts().get(2).getPart().setId(BigInteger.valueOf(1200));
                }
            }, new BiConsumer<AssemblyStep, Session>() {
                @Override
                public void accept(AssemblyStep step, Session session) {
                    AssemblyStepDefiniton stepDef = step.getStepDefinition();
                    Assert.assertEquals((Integer) 3, stepDef.getNumber());
                    Assert.assertEquals(AssemblyStepStatus.COMPLETED, step.getStatus());
                }
            });
        
        } catch (IllegalArgumentException ex) {

            TestCase.assertTrue(ex.getMessage().startsWith("Assembly part #3 "));
            TestCase.assertTrue(ex.getMessage().contains("does not match previous Part("));
	    
        }
    }
    
    @Test
    public void step31Test() throws Throwable {
        stepTest(JSON_FILE_BASE_3, DATA_FILE_BASE, 
        new Consumer<AssemblyStep>() {
            @Override
            public void accept(AssemblyStep step) { }
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
        stepTest(JSON_FILE_BASE_4, DATA_FILE_BASE, 
        new Consumer<AssemblyStep>() {
            @Override
            public void accept(AssemblyStep step) { }
        }, new BiConsumer<AssemblyStep, Session>() {
            @Override
            public void accept(AssemblyStep step, Session session) {
                AssemblyStepDefiniton stepDef = step.getStepDefinition();
                Assert.assertEquals((Integer) 4, stepDef.getNumber());
                Assert.assertEquals(AssemblyStepStatus.COMPLETED, step.getStatus());
            }
        });
    }
    
    @Test
    public void step50Test() throws Throwable {
        stepTest(JSON_FILE_BASE_5, DATA_FILE_BASE, 
        new Consumer<AssemblyStep>() {
            @Override
            public void accept(AssemblyStep step) { }
        }, new BiConsumer<AssemblyStep, Session>() {
            @Override
            public void accept(AssemblyStep step, Session session) {
                AssemblyStepDefiniton stepDef = step.getStepDefinition();
                Assert.assertEquals((Integer) 5, stepDef.getNumber());
                Assert.assertEquals(AssemblyStepStatus.COMPLETED, step.getStatus());
            }
        });
    }
    
}


