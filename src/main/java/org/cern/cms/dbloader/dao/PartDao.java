package org.cern.cms.dbloader.dao;

import java.util.Stack;

import javax.management.modelmbean.XMLParseException;

import org.cern.cms.dbloader.model.construct.KindOfPart;
import org.cern.cms.dbloader.model.construct.Manufacturer;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.construct.PartAttrList;
import org.cern.cms.dbloader.model.construct.PartRelationship;
import org.cern.cms.dbloader.model.construct.PartToAttrRltSh;
import org.cern.cms.dbloader.model.construct.PartTree;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.managemnt.Institution;
import org.cern.cms.dbloader.model.managemnt.Location;
import org.cern.cms.dbloader.model.xml.Root;
import org.cern.cms.dbloader.model.xml.map.AttrBase;
import org.cern.cms.dbloader.model.xml.map.AttrCatalog;
import org.cern.cms.dbloader.model.xml.map.Attribute;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.manager.SessionManager;
import org.hibernate.NonUniqueResultException;

@Log4j
public class PartDao extends DaoBase {

    @Inject
    public PartDao(@Assisted SessionManager sm) throws Exception {
        super(sm);
    }
    
    public void savePart(Root root, AuditLog alog) throws Exception {
        
        // Read ROOT part
        
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

        for (Part part : root.getParts()) {
            resolvePart(part, pairs);
        }

        int count = 0;
        while (!pairs.isEmpty()) {
            PartsPair pp = pairs.pop();
            resolvePartTree(pp.getPart(), pp.getParent(), rootPart);
            count++;
        }
        
        alog.setDatasetRecordCount(count);

    }

    private Part resolvePart(Part part, Stack<PartsPair> pairs) throws Exception {

        Part xmlPart = part;
        Part dbPart = null;
        KindOfPart kop = null;

        if (xmlPart.getId() != null) {

            dbPart = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("id", xmlPart.getId()))
                    .add(Restrictions.eq("deleted", Boolean.FALSE))
                    .uniqueResult();

        }

        if (dbPart == null && xmlPart.getBarcode() != null) {

            dbPart = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("barcode", xmlPart.getBarcode()))
                    .add(Restrictions.eq("deleted", Boolean.FALSE))
                    .uniqueResult();

        }

        if (dbPart == null) {

            kop = resolveKindOfPart(xmlPart);

            // Get the part based on KindOfPart and Name
            if (xmlPart.getName() != null) {
                dbPart = (Part) session.createCriteria(Part.class)
                        .add(Restrictions.eq("name", xmlPart.getName()))
                        .add(Restrictions.eq("deleted", Boolean.FALSE))
                        .add(Restrictions.eq("kindOfPart", kop))
                        .uniqueResult();

                // Get the part based on KindOfPart and SerialNumber
            } else if (xmlPart.getSerialNumber() != null) {
                dbPart = (Part) session.createCriteria(Part.class)
                        .add(Restrictions.eq("serialNumber", xmlPart.getSerialNumber()))
                        .add(Restrictions.eq("deleted", Boolean.FALSE))
                        .add(Restrictions.eq("kindOfPart", kop))
                        .uniqueResult();
            }

        }

        if (dbPart == null) {
            dbPart = xmlPart;
            dbPart.setKindOfPart(kop);
        }

        if (xmlPart.getLocationName() != null || xmlPart.getInstitutionName() != null) {
            dbPart.setLocation(resolveInstituteLocation(part));
        }

        if (xmlPart.getManufacturerName() != null) {
            dbPart.setManufacturer(resolveManufacturer(xmlPart.getManufacturerName()));
        }

        session.save(dbPart);

        if (xmlPart.getAttributes() != null) {
            for (Attribute attr : xmlPart.getAttributes()) {
                resolveAttribute(attr, dbPart);
            }
        }

        if (xmlPart.getChildren() != null) {
            for (Part child : xmlPart.getChildren()) {
                pairs.push(new PartsPair(resolvePart(child, pairs), dbPart));
            }
        }

        return dbPart;

    }

    private PartTree resolvePartTree(Part part, Part parent, Part rootPart) throws Exception {

        PartTree parentTree = parent.getPartTree();
        if (parentTree == null) {
            parentTree = new PartTree();
            parentTree.setPartId(parent.getId());
            parentTree.setParentPartTree(rootPart.getPartTree());
            parentTree.setRelationship(resolveRelationship(rootPart.getKindOfPart(), parent.getKindOfPart()));
            session.save(parentTree);
            parent.setPartTree(parentTree);
            session.save(parent);
        }

        PartTree partTree = part.getPartTree();
        if (partTree == null) {
            partTree = new PartTree();
            partTree.setPartId(part.getId());
            partTree.setParentPartTree(parentTree);
            partTree.setRelationship(resolveRelationship(parent.getKindOfPart(), part.getKindOfPart()));
        }

        partTree.setParentPartTree(parentTree);
        part.setPartTree(partTree);

        return partTree;

    }

    private PartRelationship resolveRelationship(KindOfPart parentKop, KindOfPart partKop) {

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

            session.save(relationship);

        }

        log.info(String.format("Resolved: %s", relationship));

        return relationship;
    }

    private Location resolveInstituteLocation(Part part) {

        String locationName = part.getLocationName() != null ? part.getLocationName() : part.getInstitutionName();
        String institutionName = part.getInstitutionName() != null ? part.getInstitutionName() : part.getLocationName();

        Institution institution = (Institution) session.createCriteria(Institution.class)
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .add(Restrictions.eq("name", institutionName))
                .uniqueResult();

        Location location = null;

        if (institution != null) {
            location = (Location) session.createCriteria(Location.class)
                    .add(Restrictions.eq("deleted", Boolean.FALSE))
                    .add(Restrictions.eq("name", locationName))
                    .add(Restrictions.eq("institution", institution))
                    .uniqueResult();
        } else {
            institution = new Institution();
            institution.setName(institutionName);
            institution.setInstituteCode(0); // Hard Coded
            session.save(institution);
        }

        if (location == null) {
            location = new Location();
            location.setName(locationName);
            location.setInstitution(institution);
            session.save(location);
            institution.getLocations().add(location);
        }

        log.info(String.format("Resolved: %s", location));
        return location;
    }

    private Manufacturer resolveManufacturer(String name) {
        Manufacturer m = (Manufacturer) session.createCriteria(Manufacturer.class)
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .add(Restrictions.eq("name", name)).
                uniqueResult();

        if (m == null) {
            m = new Manufacturer();
            m.setName(name);
        }
        log.info(String.format("Resolved: %s", m));
        return m;
    }

    private KindOfPart resolveKindOfPart(Part part) throws XMLParseException {

        if (part.getKindOfPartName() == null) {
            throw new XMLParseException(String.format("Kind of part not defined for %s", part.getName()));
        }

        KindOfPart kop = (KindOfPart) session.createCriteria(KindOfPart.class)
                .add(Restrictions.eq("name", part.getKindOfPartName()))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .uniqueResult();

        if (kop == null) {
            throw new XMLParseException(String.format("Not resolved kind of part for: %s", part));
        }

        return kop;
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

        if (partAttrList == null) {

            partAttrList = new PartAttrList();
            partAttrList.setPartToAttrRtlSh(partlship);
            partAttrList.setAttrBase(attrbase);
            partAttrList.setPart(part);
            partAttrList.setDeleted(Boolean.FALSE);
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
