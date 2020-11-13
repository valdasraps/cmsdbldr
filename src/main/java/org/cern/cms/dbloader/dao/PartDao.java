package org.cern.cms.dbloader.dao;

import java.util.Stack;

import javax.management.modelmbean.XMLParseException;
import org.cern.cms.dbloader.manager.file.DataFile;

import org.cern.cms.dbloader.manager.*;
import org.cern.cms.dbloader.metadata.ConstructEntityHandler;
import org.cern.cms.dbloader.model.construct.PartDetailsBase;
import org.cern.cms.dbloader.model.construct.KindOfPart;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.construct.PartAttrList;
import org.cern.cms.dbloader.model.construct.PartRelationship;
import org.cern.cms.dbloader.model.construct.PartToAttrRltSh;
import org.cern.cms.dbloader.model.construct.PartTree;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.serial.Root;
import org.cern.cms.dbloader.model.serial.map.AttrBase;
import org.cern.cms.dbloader.model.serial.map.AttrCatalog;
import org.cern.cms.dbloader.model.serial.map.Attribute;
import org.hibernate.criterion.Restrictions;


import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.util.OperatorAuth;
import org.hibernate.NonUniqueResultException;

@Log4j
public class PartDao extends DaoBase {

    @Inject
    public PartDao(@Assisted SessionManager sm, @Assisted OperatorAuth auth) throws Exception {
        super(sm, auth);
    }

    @Inject
    private DynamicEntityGenerator enGenerator;

    public void savePart(Root root, AuditLog alog, DataFile file) throws Exception {

        Part rootPart = (Part) session.createCriteria(Part.class)
            .add(Restrictions.eq("id", props.getRootPartId()))
            .add(Restrictions.eq("deleted", Boolean.FALSE))
            .uniqueResult();

        if (rootPart == null) {
            throw new IllegalArgumentException(String.format("Not resolved ROOT part for ID: %d", props.getRootPartId()));
        }

        if (rootPart.getPartTree() == null) {
            throw new Exception(String.format("ROOT Part does not have PartTree: %s", rootPart));
        }
        
        // Fill in AUDIT LOG

        alog.setKindOfConditionName("[CONSTRUCT]");
        alog.setExtensionTableName("[PARTS]");

        // Loop Parts and load
        
        Stack<PartsPair> pairs = new Stack<>();

        for (int i = 0; i < root.getParts().size(); i++) {
            Part part = root.getParts().get(i);
            part = resolvePart(part, pairs, file);
            root.getParts().set(i, part);
        }

        int count = 0;
        while (!pairs.isEmpty()) {
            PartsPair pp = pairs.pop();
            PartTree partTree = resolvePartTree(pp.getPart(), pp.getParent(), rootPart);

            // Set operator value
            String insertUser = resolveInsertionUser(partTree.getInsertUser());
            partTree.setLastUpdateUser(insertUser);
            partTree.setInsertUser(insertUser);

            session.save(partTree);
            count++;
        }
        
        alog.setDatasetRecordCount(count);

    }

    private Part resolvePart(Part part, Stack<PartsPair> pairs, DataFile file) throws Exception {

        Part xmlPart = part;
        KindOfPart kop;

        Part dbPart = resolvePart(xmlPart, false);
        if (dbPart == null) {
            
            kop = resolveKindOfPart(xmlPart.getKindOfPartName());
            dbPart = xmlPart;
            dbPart.setKindOfPart(kop);
            
        } else {

            if (xmlPart.getSerialNumber() != null) {
                dbPart.setSerialNumber(xmlPart.getSerialNumber());
            }

            if (xmlPart.getBarcode() != null) {
                dbPart.setBarcode(xmlPart.getBarcode());
            }
            if (xmlPart.getName() != null) {
                dbPart.setName(xmlPart.getName());
            }

            if (xmlPart.getComment() != null) {
                dbPart.setComment(xmlPart.getComment());
            }
        }

        if (xmlPart.getLocationName() != null || xmlPart.getInstitutionName() != null) {
            String locationName = part.getLocationName() != null ? part.getLocationName() : part.getInstitutionName();
            String institutionName = part.getInstitutionName() != null ? part.getInstitutionName() : part.getLocationName();
            dbPart.setLocation(resolveInstituteLocation(institutionName, locationName, dbPart.getInsertUser()));
        }

        if (xmlPart.getManufacturerName() != null) {
            dbPart.setManufacturer(resolveManufacturer(xmlPart.getManufacturerName()));
        }

        String insertUser = resolveInsertionUser(dbPart.getInsertUser());
        dbPart.setLastUpdateUser(insertUser);

        if (dbPart.getId() == null) {
            dbPart.setInsertUser(insertUser);
        }
        
        session.save(dbPart);

        if (xmlPart.getAttributes() != null) {
            for (Attribute attr : xmlPart.getAttributes()) {
                resolveAttribute(attr, dbPart);
            }
        }

        if (xmlPart.getChildren() != null) {
            for (Part child : xmlPart.getChildren()) {
                pairs.push(new PartsPair(resolvePart(child, pairs, file), dbPart));
            }
        }

        if (xmlPart.getPartDetails() != null) {
            PartDetailsBase details = resolvePartDetails(dbPart, xmlPart, file);
            session.save(details);
        }

        // Set operator value
        insertUser = resolveInsertionUser(dbPart.getInsertUser());
        dbPart.setLastUpdateUser(insertUser);

        if (dbPart.getId() == null) {
            dbPart.setInsertUser(insertUser);
        }
        
        session.save(dbPart);

        return dbPart;

    }

    private PartDetailsBase resolvePartDetails(Part dbPart, Part xmlPart, DataFile file) throws Exception {


        ConstructEntityHandler coneh = enGenerator.getConstructHandler(dbPart.getKindOfPart().getName());

        PartDetailsBase partDetailsBase = xmlPart.getPartDetails().getDelegate(coneh.getEntityClass().getC());
        LobManager lobManager = new LobManager();
        lobManager.lobParserParts(partDetailsBase, coneh, file);
        PartDetailsBase partDetailsEn = (PartDetailsBase) session.createCriteria(PartDetailsBase.class)
                .add(Restrictions.eq("part", dbPart))
                .uniqueResult();

        if (partDetailsEn == null) {
            partDetailsBase.setPart(dbPart);

            return partDetailsBase;

        } else {
            partDetailsEn.copyProps(partDetailsBase);

            return partDetailsEn;
        }
    }

    private PartTree resolvePartTree(Part part, Part parent, Part rootPart) throws Exception {

        PartTree parentTree = parent.getPartTree();
        if (parentTree == null) {
            parentTree = new PartTree();
            parentTree.setPartId(parent.getId());
            parentTree.setParentPartTree(rootPart.getPartTree());
            parentTree.setRelationship(resolveRelationship(rootPart.getKindOfPart(), parent.getKindOfPart(), part.getInsertUser()));
            
            // Set operator value
            String insertUser = resolveInsertionUser(parentTree.getInsertUser());
            parentTree.setLastUpdateUser(insertUser);
            parentTree.setInsertUser(insertUser);

            session.save(parentTree);
            parent.setPartTree(parentTree);
            
            // Set operator value

            insertUser = resolveInsertionUser(parent.getInsertUser());
            parent.setLastUpdateUser(insertUser);

            if (parent.getId() == null) {
                parent.setInsertUser(insertUser);
            }
            session.save(parent);
        }

        PartTree partTree = part.getPartTree();
        if (partTree == null) {
            partTree = new PartTree();
            partTree.setPartId(part.getId());
            partTree.setParentPartTree(parentTree);
            partTree.setRelationship(resolveRelationship(parent.getKindOfPart(), part.getKindOfPart(), part.getInsertUser()));
        }

        partTree.setParentPartTree(parentTree);
        part.setPartTree(partTree);
        partTree.setDeleted(Boolean.FALSE);

        return partTree;

    }

    private PartRelationship resolveRelationship(KindOfPart parentKop, KindOfPart partKop, String insertUser) {

        PartRelationship relationship = (PartRelationship) session.createCriteria(PartRelationship.class)
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .add(Restrictions.eq("parentKop", parentKop))
                .add(Restrictions.eq("partKop", partKop))
                .uniqueResult();

        if (relationship == null) {

            relationship = new PartRelationship();
            relationship.setPartKop(partKop);
            relationship.setParentKop(parentKop);
            relationship.setPriority(0); // Hard Coded
            relationship.setName(String.format("AutoAssigned: %s --> %s", partKop.getName(), parentKop.getName()));
            relationship.setComment(relationship.getName());
            relationship.setDeleted(Boolean.FALSE);

            // Set operator value
            relationship.setLastUpdateUser(insertUser);
            relationship.setInsertUser(insertUser);
            
            session.save(relationship);

        }

        log.info(String.format("Resolved: %s", relationship));

        return relationship;
    }

    private PartAttrList resolveAttribute(Attribute attr, Part part) throws Exception {

        KindOfPart kop = part.getKindOfPart();

        AttrCatalog catalog = null;
        try {
            
             catalog = (AttrCatalog) session.createCriteria(AttrCatalog.class)
                    .add(Restrictions.eq("deleted", Boolean.FALSE))
                    .add(Restrictions.eq("name", attr.getName()))
                    .uniqueResult();
             
        } catch (NonUniqueResultException ex) {
            throw new XMLParseException(String.format("More than one attribute catalog found for %s (%s)", attr, ex.getMessage()));
        }

        if (catalog == null) {
            throw new XMLParseException(String.format("Not resolved attribute catalog for %s", attr));
        }

        AttrBase attrbase = resolveAttrBase(attr, catalog);

        PartToAttrRltSh partlship = null;
        
        try {
            
            partlship = (PartToAttrRltSh) session.createCriteria(PartToAttrRltSh.class)
                    .add(Restrictions.eq("deleted", Boolean.FALSE))
                    .add(Restrictions.eq("kop", kop))
                    .add(Restrictions.eq("attrCatalog", catalog))
                    .uniqueResult();
            
        } catch (NonUniqueResultException ex) {
            throw new XMLParseException(String.format("More than one attribute catalog to kind of part relationship found for %s and %s (%s)", catalog, kop, ex.getMessage()));
        }

        if (partlship == null) {
            throw new XMLParseException(String.format("Not resolved attribute to kind of part relationship for %s and %s", kop, catalog));
        }


        PartAttrList partAttrList = (PartAttrList) session.createCriteria(PartAttrList.class)
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .add(Restrictions.eq("partToAttrRtlSh", partlship))
                .add(Restrictions.eq("attrBase", attrbase))
                .add(Restrictions.eq("part", part))
                .uniqueResult();

        if (partAttrList != null && attr.getDeleted() == true) {
            partAttrList.setDeleted(Boolean.TRUE);
            session.save(partAttrList);

        } else if (partAttrList == null) {

            partAttrList = new PartAttrList();
            partAttrList.setPartToAttrRtlSh(partlship);
            partAttrList.setAttrBase(attrbase);
            partAttrList.setPart(part);
            partAttrList.setDeleted(Boolean.FALSE);
            partAttrList.setInsertUser(resolveInsertionUser(part.getInsertUser()));

            session.save(partAttrList);

        }

        return partAttrList;

    }

    @RequiredArgsConstructor
    @Getter
    private class PartsPair {

        private final Part part;
        private final Part parent;

    }

}
