package org.cern.cms.dbloader.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.model.construct.ext.AssemblyData;
import org.cern.cms.dbloader.model.construct.ext.AssemblyPart;
import org.cern.cms.dbloader.model.construct.ext.AssemblyPartDefiniton;
import org.cern.cms.dbloader.model.construct.ext.AssemblyProcess;
import org.cern.cms.dbloader.model.construct.ext.AssemblyStep;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.managemnt.UploadStatus;
import org.cern.cms.dbloader.model.serial.Root;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Test;


public class AssemblyZipTest extends TestBase {

    private final static File JSON_FILE_BASE = new File("src/test/zip/assembly_1.json");
    private final static File DATA_FILE_BASE = new File("src/test/zip/assembly_1.csv");

    @Test
    public void step01Test() throws Throwable {
        String datasetVersion = "1.0";
        
        stepTest(1, datasetVersion);
        stepTest(2, datasetVersion);
        stepTest(3, datasetVersion);
        stepTest(4, datasetVersion);
        
        datasetVersion = "2.0";
        
        stepTest(1, datasetVersion);
        stepTest(2, datasetVersion);
        stepTest(3, datasetVersion);
        stepTest(4, datasetVersion);
        
    }
    
    public void stepTest(Integer stepNumber, String datasetVersion) throws Throwable {
        
        Root root = readJSONFile(JSON_FILE_BASE);
        
        AssemblyStep step = root.getAssemblySteps().iterator().next();
        step.setNumber(stepNumber);
        AssemblyPart prod = step.getAssemblyParts().iterator().next();
        prod.getAssemblyData().iterator().next().setVersion(datasetVersion);
        
        File zipFile = upload(root);
        
        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            Criteria c = session.createCriteria(AssemblyStep.class);
            c.createCriteria("stepDefinition").add(Restrictions.eq("number", step.getNumber()));
            c.createCriteria("part").add(Restrictions.eq("id", prod.getPart().getId()));
            AssemblyStep astep = (AssemblyStep) c.uniqueResult();

            for (AssemblyPart apart: astep.getAssemblyParts()) {
                if (apart.getPartDefinition().getType() == AssemblyPartDefiniton.AssemblyPartType.PRODUCT) {
                    
                    AssemblyData adata = apart.getAssemblyData().iterator().next();
                    Assert.assertEquals(datasetVersion, adata.getDataset().getVersion());
                    
                }
            }
                    
            AuditLog alog = (AuditLog) session.createCriteria(AuditLog.class)
                .add(Restrictions.eq("archiveFileName", zipFile.getName()))
                    .add(Restrictions.eq("version", datasetVersion))
                .uniqueResult();

            Assert.assertEquals(UploadStatus.Success, alog.getStatus());

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

    private AssemblyProcess getAssemblyProcess() throws Exception {
        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            return (AssemblyProcess) session.createCriteria(AssemblyProcess.class)
                        .add(Restrictions.eq("id", 1))
                        .uniqueResult();

        }
    }    
}


