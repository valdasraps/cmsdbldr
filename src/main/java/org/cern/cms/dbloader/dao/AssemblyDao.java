package org.cern.cms.dbloader.dao;

import org.cern.cms.dbloader.manager.file.DataFile;

import org.cern.cms.dbloader.manager.*;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;

import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.Run;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.construct.ext.AssemblyData;
import org.cern.cms.dbloader.model.construct.ext.AssemblyDataDefiniton;
import org.cern.cms.dbloader.model.construct.ext.AssemblyPart;
import org.cern.cms.dbloader.model.construct.ext.AssemblyPartDefiniton;
import org.cern.cms.dbloader.model.construct.ext.AssemblyProcess;
import org.cern.cms.dbloader.model.construct.ext.AssemblyStep;
import org.cern.cms.dbloader.model.construct.ext.AssemblyStepDefiniton;
import org.cern.cms.dbloader.model.serial.Header;
import org.cern.cms.dbloader.model.serial.Root;
import org.cern.cms.dbloader.util.OperatorAuth;
import org.hibernate.criterion.Restrictions;

@Log4j
public class AssemblyDao extends DaoBase {

    @Inject
    public AssemblyDao(@Assisted SessionManager sm, @Assisted OperatorAuth auth) throws Exception {
        super(sm, auth);
    }
    
    @Inject
    private ResourceFactory rf;

    @Inject
    private DynamicEntityGenerator enGenerator;

    public void saveAssembly(AssemblyStep step, AuditLog alog, DataFile file) throws Exception {
        
        // Lets lookup all stuff in the database and validate input
        
        Part product = resolvePart(step.getPart(), false);

        //  Pick product Kind of Part
        if (product == null) {
            
            product = step.getPart();
            
            if (product.getKindOfPartName() == null) {
                throw new IllegalArgumentException(
                        String.format("Not existing assembly product does not have Kinf of Part defined: %s", product));
            }
            
            product.setKindOfPart(resolveKindOfPart(product.getKindOfPartName()));
            
        }
        
        if (step.getNumber() == null) {
            throw new IllegalArgumentException(String.format("Assembly step number is mandatory: %s", step));
        }
        
        AssemblyProcess process = (AssemblyProcess) session.createCriteria(AssemblyProcess.class)
            .add(Restrictions.eq("kindOfPart", product.getKindOfPart()))
            .uniqueResult();
        
        if (process == null) {
            throw new IllegalArgumentException(String.format("Assembly process not found: %s", step));
        }
        
        AssemblyStepDefiniton stepDef = (AssemblyStepDefiniton) session.createCriteria(AssemblyStepDefiniton.class)
            .add(Restrictions.eq("number", step.getNumber()))
            .add(Restrictions.eq("assemblyProcess", process))
            .uniqueResult();

        if (stepDef == null) {
            throw new IllegalArgumentException(String.format("Assembly step definition not found: %s", step));
        } else {
            step.setStepDefinition(stepDef);
        }
        
        AssemblyStep dbStep = updateStep(step);
        
        for (AssemblyPart apart: step.getAssemblyParts()) {

            Part part = resolvePart(apart.getPart(), false);
            
            if (apart.getNumber() == null) {
                throw new IllegalArgumentException(String.format("Assembly step part number is mandatory: %s", apart));
            }
                        
            AssemblyPartDefiniton partDef = (AssemblyPartDefiniton) session.createCriteria(AssemblyPartDefiniton.class)
                .add(Restrictions.eq("number", apart.getNumber()))
                .add(Restrictions.eq("assemblyStepDefinition", stepDef))
                .uniqueResult();
            
            if (partDef == null) {
                throw new IllegalArgumentException(String.format("Assembly step part definition not found: %s", apart));
            }
            
            if (!partDef.getKindOfPart().equals(part.getKindOfPart())) {
                throw new IllegalArgumentException(String.format("Assembly step part kind of part does not match: %s", apart));
            }
            
            if (partDef.getType().equals(AssemblyPartDefiniton.AssemblyPartType.PRODUCT)) {
                dbStep.setLocation(part.getLocation());
            }
            
            apart.setPartDefinition(partDef);
            apart.setStep(step);

            
            for (AssemblyData adata: apart.getAssemblyData()) {

                if (adata.getNumber() == null) {
                    throw new IllegalArgumentException(String.format("Assembly step data number is mandatory: %s", adata));
                }
                
                if (adata.getDataFilename() == null) {
                    throw new IllegalArgumentException(String.format("Assembly step data file is not defined: %s", adata));
                }

                AssemblyDataDefiniton dataDef = (AssemblyDataDefiniton) session.createCriteria(AssemblyDataDefiniton.class)
                    .add(Restrictions.eq("number", adata.getNumber()))
                    .add(Restrictions.eq("assemblyPartDefinition", partDef))
                    .uniqueResult();

                if (dataDef == null) {
                    throw new IllegalArgumentException(String.format("Assembly step part data definition not found: %s", adata));
                }
                
                adata.setDataDefinition(dataDef);
                adata.setAssemblyPart(apart);

                Root root = generateDataRoot(adata);
                adata.setDataset(root.getDatasets().iterator().next());
                rf.createCondDao(sm, auth).saveCondition(root, alog, file, null);

            }
            
        }

        session.saveOrUpdate(dbStep);
        
    }
    
    private AssemblyStep updateStep(AssemblyStep step) throws Exception {
        
        if (step.getStatus() == null) {
            throw new IllegalArgumentException(String.format("Assembly step status have to be defined: %s", step));
        }
        
        if (step.getComment() == null) {
            throw new IllegalArgumentException(String.format("Assembly step comment is required: %s", step));
        }
        
        AssemblyStep dbStep = (AssemblyStep) session.createCriteria(AssemblyStep.class)
                    .add(Restrictions.eq("stepDefinition", step.getStepDefinition()))
                    .add(Restrictions.eq("part", step.getPart()))
                    .uniqueResult();

        if (dbStep == null) {
            
            dbStep = step;
            
        } else {
            
            dbStep.setStatus(step.getStatus());
            dbStep.setComment(step.getComment());
            
        }
        
        String insertUser = resolveInsertionUser(dbStep.getInsertUser());
        dbStep.setLastUpdateUser(insertUser);

        if (dbStep.getId() == null) {
            dbStep.setInsertUser(insertUser);
        }
        
        return dbStep;
        
    }
    
    private Root generateDataRoot(AssemblyData adata) {
        AssemblyStepDefiniton stepDef = adata.getAssemblyPart().getStep().getStepDefinition();
        Root root = new Root();
        
        Header header = new Header();
        header.setKindOfCondition(adata.getDataDefinition().getKindOfCondition());
        root.setHeader(header);
        
        Run run = new Run();
        run.setRunType(stepDef.getAssemblyProcess().getName());
        run.setNumber(BigInteger.valueOf(stepDef.getNumber()));
        run.setBeginTime(new Date());
        header.setRun(run);
        
        Dataset dataset = new Dataset();
        dataset.setDataFilename(adata.getDataFilename());
        root.setDatasets(Collections.singletonList(dataset));
        
        return root;
    }

}
