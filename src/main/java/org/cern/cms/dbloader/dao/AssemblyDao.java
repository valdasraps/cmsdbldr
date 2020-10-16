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
import javax.persistence.criteria.CriteriaBuilder;

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

        
//        for (int i = 0; i < step.getAssemblyParts().size(); i++) {
//            AssemblyPart apart = step.getAssemblyParts().get(i);
//            
//            apart.setStep(step);
//            apart = updateAssemblyPart(apart, alog, file);
//            step.getAssemblyParts().set(i, apart);
//        }

        session.saveOrUpdate(step);
        
    }
    
    private AssemblyStep updateStep(AssemblyStep step, AuditLog alog, DataFile file) throws Exception {
        
        if (step.getStatus() == null) {
            throw new IllegalArgumentException(String.format("Assembly step status have to be defined: %s", step));
        }
        
        if (step.getComment() == null) {
            throw new IllegalArgumentException(String.format("Assembly step comment is required: %s", step));
        }
        
        {
            
            BigInteger _id = (BigInteger) session.createCriteria(AssemblyStep.class)
                        .add(Restrictions.eq("stepDefinition", step.getStepDefinition()))
                        .add(Restrictions.eq("part", step.getPart()))
                        .setProjection(Projections.property("id"))
                        .uniqueResult();

            if (_id != null) {
                step.setId(_id);
            }

            
//            AssemblyStep _step = (AssemblyStep) session.createCriteria(AssemblyStep.class)
//                        .add(Restrictions.eq("stepDefinition", step.getStepDefinition()))
//                        .add(Restrictions.eq("part", step.getPart()))
//                        .uniqueResult();
//
//            if (_step != null) {
//                _step.setComment(step.getComment());
//                _step.setStatus(step.getStatus());
//                _step.setAssemblyParts(step.getAssemblyParts());
//                step = _step;
//            }
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
//            AssemblyPart _apart = (AssemblyPart) session.createCriteria(AssemblyPart.class)
//                        .add(Restrictions.eq("partDefinition", partDef))
//                        .add(Restrictions.eq("step", step))
//                        .uniqueResult();
//            if (_apart != null) {
//                _apart.setPart(apart.getPart());
//                _apart.setAssemblyData(apart.getAssemblyData());
//                apart = _apart;
//            }
        }
  
        for (AssemblyData adata: apart.getAssemblyData()) {
            adata.setAssemblyPart(apart);
            updateAssemblyData(adata, alog, file);
        }
        
//        for (int i = 0; i < apart.getAssemblyData().size(); i++) {
//            AssemblyData adata = apart.getAssemblyData().get(i);
//            
//            adata.setAssemblyPart(apart);
//            adata = updateAssemblyData(adata, alog, file);
//            apart.getAssemblyData().set(i, adata);
//        }
        
        return apart;
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
//            AssemblyData db = (AssemblyData) session.createCriteria(AssemblyData.class)
//                        .add(Restrictions.eq("dataDefinition", dataDef))
//                        .add(Restrictions.eq("assemblyPart", apart))
//                        .uniqueResult();
//            if (db != null) {
//                db.setDataFilename(adata.getDataFilename());
//                db.setNumber(adata.getNumber());
//                db.setVersion(adata.getVersion());
//                adata = db;
//            }
        }
        
        Root root = generateDataRoot(adata);
        adata.setDataset(root.getDatasets().iterator().next());
        rf.createCondDao(sm, auth).saveCondition(root, alog, file, null);
        
        return adata;
        
    }
    
    private Root generateDataRoot(AssemblyData adata) {
        
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
        
        return root;
    }

}
