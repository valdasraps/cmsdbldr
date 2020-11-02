package org.cern.cms.dbloader.dao;

import org.cern.cms.dbloader.manager.file.DataFile;

import org.cern.cms.dbloader.manager.*;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.Run;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.construct.PartAttrList;
import org.cern.cms.dbloader.model.construct.ext.AssemblyAttributeDefiniton;
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
import org.cern.cms.dbloader.model.serial.map.AttrBase;
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
        AssemblyStepDefiniton stepDef = process.getStepDefinitions().stream()
                .filter(s -> Objects.equals(s.getNumber(), stepNumber)).findFirst().get();

        if (stepDef == null) {
            throw new IllegalArgumentException(String.format("Assembly step definition not found: %s", step));
        } else {
            step.setStepDefinition(stepDef);
        }
        
        // Check step
        
        step = checkStep(step);

        // First pass to check entries (re-writting product!)
        
        product = null;
        Set<Integer> partNumbers = new HashSet<>();
        for (AssemblyPart apart: step.getAssemblyParts()) {
            
            if (apart.getNumber() == null) {
                throw new IllegalArgumentException(String.format("Assembly step part number is mandatory: %s", apart));
            }
            
            if (partNumbers.contains(apart.getNumber())) {
                throw new IllegalArgumentException(String.format("Dublicate assembly step part number: %s", apart));
            }
            
            partNumbers.add(apart.getNumber());
            apart.setStep(step);
            checkAssemblyPart(apart);
            
            if (apart.isProductType()) {
                product = apart.getPart();
            }
            
        }

        // Second pass to save parts hierarchy.
        
        for (AssemblyPart apart: step.getAssemblyParts()) {
            Part part = apart.getPart();
            if (apart.isComponentType() && apart.getPartDefinition().getNewPart()) {
                if (product != null) {
                    product.addChild(part);
                } else {
                    savePart(part, alog, file);
                }
            }
        }
        
        if (product != null) {
            savePart(product, alog, file);
        }

        // Third pass to save datasets.
        
        for (AssemblyPart apart: step.getAssemblyParts()) {
            for (AssemblyData adata: apart.getAssemblyData()) {
                saveDataset(adata, alog, file);
            }
        }
        
        // Save the step itself.
        
        session.saveOrUpdate(step);
        
        // Ping with database
        
        session.flush();
        session.refresh(step);
        
        // Lets validate step
        
        validateStep(step);
        
    }
    
    /**
     * Checking step consistency.
     * @param step object to check.
     * @return checked object.
     * @throws Exception error.
     */
    private AssemblyStep checkStep(AssemblyStep step) throws Exception {
        
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
    
    /**
     * Checking assembly part consistency.
     * @param apart object to check.
     * @return checked object.
     * @throws Exception error.
     */
    private AssemblyPart checkAssemblyPart(AssemblyPart apart) throws Exception {
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

        AssemblyPartDefiniton partDef = (AssemblyPartDefiniton) session.createCriteria(AssemblyPartDefiniton.class)
            .add(Restrictions.eq("number", apart.getNumber()))
            .add(Restrictions.eq("stepDefinition", stepDef))
            .uniqueResult();

        if (partDef == null) {
            throw new IllegalArgumentException(String.format("Assembly step part definition not found: %s", apart));
        }

        if (!partDef.getKindOfPart().equals(part.getKindOfPart())) {
            throw new IllegalArgumentException(String.format("Assembly step part kind of part does not match: %s", apart));
        }
        
        if (partDef.isProductType()) {
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
        }
  
        for (AssemblyData adata: apart.getAssemblyData()) {
            adata.setAssemblyPart(apart);
            checkAssemblyData(adata);
        }
        
        return apart;
    }

    /**
     * Check assembly data for consistency.
     * @param adata object to check.
     * @return checked object.
     * @throws Exception error.
     */
    private AssemblyData checkAssemblyData(AssemblyData adata) throws Exception {
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
        
        Dataset dataset = new Dataset();
        dataset.setPart(apart.getPart());
        dataset.setVersion(adata.getVersion());
        dataset.setComment(adata.getDataDefinition().getDescription());
        dataset.setDataFilename(adata.getDataFilename());
        
        adata.setDataset(dataset);
        
        return adata;
        
    }
    
    /**
     * Validate step information.
     * @param step data to validate.
     * @throws Exception any error.
     */
    private void validateStep(AssemblyStep step) throws Exception {
        AssemblyStepDefiniton stepDef = step.getStepDefinition();
        for (AssemblyPartDefiniton partDef: stepDef.getPartDefinitions()) {
            AssemblyPart apart = step.findAssemblyPart(partDef);
            
            // For complete step all parts have to be set
            
            if (apart == null && step.isCompletedStatus()) {
                throw new IllegalArgumentException(String.format("Assembly part #%d (%s) is missing for COMPLETED step: %s", 
                        partDef.getNumber(), partDef.getType(), step));
            }
            
            if (apart != null) {
                Part part = apart.getPart(); //resolvePart(apart.getPart(), true);
                
                // Checking locations
                
                if (step.getLocation() == null || 
                    part.getLocation() == null || 
                    !step.getLocation().equals(part.getLocation())) {
                    throw new IllegalArgumentException(String.format("Assembly part #%d (%s) location (%s) does not match step location (%s): %s", 
                            partDef.getNumber(), partDef.getType(), part.getLocation(),
                            step.getLocation(), step));
                }

                // Checking attributes presence
                
                for (AssemblyAttributeDefiniton attrDef: partDef.getAttributeDefinitions()) {
                    if (attrDef.getSelectable() || attrDef.getStepStatus() == step.getStatus()) {
                        AttrBase attrBase = attrDef.getAttribute();
                        PartAttrList attrList = part.findAttrList(attrBase);
                        if (attrList == null || attrList.getDeleted()) {
                            throw new IllegalArgumentException(
                                    String.format("Assembly part #%d attribute %s is missing in step #%d (%s): %s", 
                                            stepDef.getNumber(), attrBase, stepDef.getNumber(), step.getStatus(), step));
                        }
                        
                    }
                }
                
                // Checking dataset presence

                for (AssemblyDataDefiniton dataDef: partDef.getDataDefinitions()) {
                    if (step.isCompletedStatus()) {
                        AssemblyData adata = apart.findAssemblyData(dataDef);
                        if (adata == null) {
                            throw new IllegalArgumentException(
                                    String.format("Assembly part #%d dataset #%d is missing in step #%d (%s): %s", 
                                            stepDef.getNumber(), dataDef.getNumber(), stepDef.getNumber(), step.getStatus(), step));
                        }
                    }
                }
                
            }
        }
    }

    /**
     * Save part to DB.
     * @param part part to save.
     * @param alog audit log entry.
     * @param file file provided.
     * @throws Exception error.
     */
    private void savePart(Part part, AuditLog alog, DataFile file) throws Exception {
        Root root = new Root();
        root.setParts(Collections.singletonList(part));

        rf.createPartDao(sm, auth).savePart(root, alog, file);
    }
    
    /**
     * Save dataset to DB.
     * @param adata assembly data (dataset) to save.
     * @param alog audit log entry.
     * @param file file provided.
     * @throws Exception error.
     */
    private void saveDataset(AssemblyData adata, AuditLog alog, DataFile file) throws Exception {
        
        AssemblyStepDefiniton stepDef = adata.getAssemblyPart().getStep().getStepDefinition();
        Root root = new Root();
        
        Header header = new Header();
        header.setKindOfCondition(adata.getDataDefinition().getKindOfCondition());
        root.setHeader(header);
        
        Run run = new Run();
        run.setBeginTime(new Date());
        run.setRunType(stepDef.getProcess().getName());
        run.setNumber(BigInteger.valueOf(stepDef.getNumber()));
        run.setComment(stepDef.getProcess().getDescription());
        header.setRun(run);

        root.setDatasets(Collections.singletonList(adata.getDataset()));

        rf.createCondDao(sm, auth).saveCondition(root, alog, file, null);

    }

}
