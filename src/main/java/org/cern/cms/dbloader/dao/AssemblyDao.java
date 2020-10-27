package org.cern.cms.dbloader.dao;

import org.cern.cms.dbloader.manager.file.DataFile;

import org.cern.cms.dbloader.manager.*;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.Run;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.construct.ext.AssemblyCurrentStep;
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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

@Log4j
public class AssemblyDao extends DaoBase {

    @Inject
    public AssemblyDao(@Assisted SessionManager sm, @Assisted OperatorAuth auth) throws Exception {
        super(sm, auth);
    }
    
    @Inject
    private ResourceFactory rf;
    
    /**
     * Main entry point to save a single Assembly Step.
     * @param step step to save.
     * @param alog audit log record.
     * @param file data file received.
     * @throws Exception 
     */
    public void saveAssembly(AssemblyStep step, AuditLog alog, DataFile file) throws Exception {
        
        // Lets lookup all stuff in the database and validate input

        if (step.getNumber() == null) {
            throw new IllegalArgumentException(String.format("Assembly step number is mandatory: %s", step));
        }
        
        Part product = resolvePart(step.getPart(), false);

        //  Pick product Kind of Part
        if (product == null) {
            
            product = step.getPart();
            
            if (product.getKindOfPartName() == null) {
                throw new IllegalArgumentException(
                        String.format("Not existing assembly product does not have Kind of Part defined: %s", product));
            }
            
            product.setKindOfPart(resolveKindOfPart(product.getKindOfPartName()));

            if (step.getNumber() != 1) {
                throw new IllegalArgumentException(String.format("Assembly step number for new part must be equal to 1: %s", step));
            }
            
        } else {
            
            AssemblyCurrentStep cstep = (AssemblyCurrentStep) session.createCriteria(AssemblyCurrentStep.class)
                .add(Restrictions.eq("partId", product.getId()))
                .uniqueResult();
            
            if (cstep == null) {
                throw new IllegalArgumentException(String.format("Current Assembly step not resolved: %s", step));
            }
            
            if (!Objects.equals(cstep.getNumber(), step.getNumber())) {
                throw new IllegalArgumentException(String.format("Current Assembly step (%d) does not match: %s ", cstep.getNumber(), step));
            }
            
        }
        
        AssemblyProcess process = (AssemblyProcess) session.createCriteria(AssemblyProcess.class)
            .add(Restrictions.eq("kindOfPart", product.getKindOfPart()))
            .uniqueResult();
        
        if (process == null) {
            throw new IllegalArgumentException(String.format("Assembly process not found: %s", step));
        }
        
        final Integer stepNumber = step.getNumber();
        AssemblyStepDefiniton stepDef = process.getSteps().stream()
                .filter(s -> Objects.equals(s.getNumber(), stepNumber)).findFirst().get();

        if (stepDef == null) {
            throw new IllegalArgumentException(String.format("Assembly step definition not found: %s", step));
        } else {
            step.setStepDefinition(stepDef);
        }
        
        step = updateStep(step, alog, file);

        for (AssemblyPart apart: step.getAssemblyParts()) {
            apart.setStep(step);
            updateAssemblyPart(apart, alog, file);
        }

        session.saveOrUpdate(step);
        
    }
    
    private AssemblyStep updateStep(AssemblyStep step, AuditLog alog, DataFile file) throws Exception {
        
        if (step.getStatus() == null) {
            throw new IllegalArgumentException(String.format("Assembly step status have to be defined: %s", step));
        }
        
        if (step.getComment() == null) {
            throw new IllegalArgumentException(String.format("Assembly step comment is required: %s", step));
        }
        
        if (step.getPart().getId() != null) {
            BigInteger _id = (BigInteger) session.createCriteria(AssemblyStep.class)
                        .add(Restrictions.eq("stepDefinition", step.getStepDefinition()))
                        .add(Restrictions.eq("part", step.getPart()))
                        .setProjection(Projections.property("id"))
                        .uniqueResult();

            if (_id != null) {
                step.setId(_id);
            }
        }
        
        String insertUser = resolveInsertionUser(step.getInsertUser());
        step.setLastUpdateUser(insertUser);

        if (step.getId() == null) {
            step.setInsertUser(insertUser);
        }
        
        return step;
        
    }
    
    private AssemblyPart updateAssemblyPart(AssemblyPart apart, AuditLog alog, DataFile file) throws Exception {
        AssemblyStep step = apart.getStep();
        AssemblyStepDefiniton stepDef = step.getStepDefinition();
        Part part = resolvePart(apart.getPart(), false);
        
        if (part == null) {
            
            part = apart.getPart();
            if (part.getKindOfPartName() == null) {
                throw new IllegalArgumentException(
                        String.format("Not existing assembly part does not have Kind of Part defined: %s", part));
            } else {
                part.setKindOfPart(resolveKindOfPart(part.getKindOfPartName()));
            }
        }

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
            step.setLocation(part.getLocation());
            if (!part.equals(step.getPart())) {
                throw new IllegalArgumentException(String.format("Assembly step PRODUCT and part PRODUCT do not match: %s", apart));
            }
        }
        
        updatePart(apart.getPart(), alog, file);

        apart.setPartDefinition(partDef);
        
        if (step.getId() != null) {
            BigInteger _id = (BigInteger) session.createCriteria(AssemblyPart.class)
                        .add(Restrictions.eq("partDefinition", partDef))
                        .add(Restrictions.eq("step", step))
                        .setProjection(Projections.property("id"))
                        .uniqueResult();
            if (_id != null) {
                apart.setId(_id);
            }
        }
  
        for (AssemblyData adata: apart.getAssemblyData()) {
            adata.setAssemblyPart(apart);
            updateAssemblyData(adata, alog, file);
        }
        
        return apart;
    }
    
    private void updatePart(Part part, AuditLog alog, DataFile file) throws Exception {
        Root root = new Root();
        root.setParts(Collections.singletonList(part));

        rf.createPartDao(sm, auth).savePart(root, alog, file);
    }
    
    private AssemblyData updateAssemblyData(AssemblyData adata, AuditLog alog, DataFile file) throws Exception {
        AssemblyPart apart = adata.getAssemblyPart();
        AssemblyPartDefiniton partDef = apart.getPartDefinition();

        if (adata.getNumber() == null) {
            throw new IllegalArgumentException(String.format("Assembly step data number is mandatory: %s", adata));
        }

        if (adata.getVersion() == null || adata.getVersion().trim().isEmpty()) {
            throw new IllegalArgumentException(String.format("Assembly step data version is mandatory: %s", adata));
        }

        if (adata.getDataFilename() == null) {
            throw new IllegalArgumentException(String.format("Assembly step data file is not defined: %s", adata));
        }

        AssemblyDataDefiniton dataDef = (AssemblyDataDefiniton) session.createCriteria(AssemblyDataDefiniton.class)
            .add(Restrictions.eq("number", adata.getNumber()))
            .add(Restrictions.eq("partDefinition", partDef))
            .uniqueResult();

        if (dataDef == null) {
            throw new IllegalArgumentException(String.format("Assembly step part data definition not found: %s", adata));
        }

        adata.setDataDefinition(dataDef);
        
        if (apart.getId() != null) {
            BigInteger _id = (BigInteger) session.createCriteria(AssemblyData.class)
                        .add(Restrictions.eq("dataDefinition", dataDef))
                        .add(Restrictions.eq("assemblyPart", apart))
                        .setProjection(Projections.property("id"))
                        .uniqueResult();
            if (_id != null) {
                adata.setId(_id);
            }
        }
        
        adata.setDataset(createDataset(adata, alog, file));
        
        return adata;
        
    }
    
    private Dataset createDataset(AssemblyData adata, AuditLog alog, DataFile file) throws Exception {
        
        AssemblyStepDefiniton stepDef = adata.getAssemblyPart().getStep().getStepDefinition();
        Root root = new Root();
        
        Header header = new Header();
        header.setKindOfCondition(adata.getDataDefinition().getKindOfCondition());
        root.setHeader(header);
        
        Run run = new Run();
        run.setBeginTime(new Date());
        run.setRunType(stepDef.getAssemblyProcess().getName());
        run.setNumber(BigInteger.valueOf(stepDef.getNumber()));
        run.setComment(stepDef.getAssemblyProcess().getDescription());
        header.setRun(run);
        
        Dataset dataset = new Dataset();
        dataset.setVersion(adata.getVersion());
        dataset.setComment(adata.getDataDefinition().getDescription());
        dataset.setDataFilename(adata.getDataFilename());
        root.setDatasets(Collections.singletonList(dataset));

        rf.createCondDao(sm, auth).saveCondition(root, alog, file, null);
        
        return dataset;
    }

}
