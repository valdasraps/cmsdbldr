package org.cern.cms.dbloader;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.model.construct.ext.AssemblyData;
import org.cern.cms.dbloader.model.construct.ext.AssemblyPart;
import org.cern.cms.dbloader.model.construct.ext.AssemblyPartType;
import org.cern.cms.dbloader.model.construct.ext.AssemblyStep;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.managemnt.UploadStatus;
import org.cern.cms.dbloader.model.serial.Root;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;


public abstract class AssemblyBase extends TestBase {
   
    protected void stepTest(File jsonFile, File dataFile, 
            Consumer<AssemblyStep> update, 
            BiConsumer<AssemblyStep, Session> check) throws Throwable {
        
        Root root = readJSONFile(jsonFile);
        
        AssemblyStep step = root.getAssemblySteps().iterator().next();
        update.accept(step);
        AssemblyPart prod = step.getAssemblyParts().iterator().next();
        
        File zipFile = upload(dataFile, root);
        
        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            Session session = sm.getSession();

            Criteria c = session.createCriteria(AssemblyStep.class);
            c.createCriteria("stepDefinition").add(Restrictions.eq("number", step.getNumber()));
            c.createCriteria("part").add(Restrictions.eq("id", prod.getPart().getId()));
            AssemblyStep astep = (AssemblyStep) c.uniqueResult();

            for (AssemblyPart apart: astep.getAssemblyParts()) {
                if (apart.getPartDefinition().getType() == AssemblyPartType.PRODUCT) {
                    
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
    
    private File upload(File dataFile, Root root) throws Throwable {
            
        FilesManager fm = injector.getInstance(FilesManager.class);
        File zipFile = fm.createZip(writeJSONFile(root), dataFile);
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


