package org.cern.cms.dbloader.dao;

import com.google.inject.Inject;
import java.lang.reflect.Field;
import javax.management.modelmbean.XMLParseException;
import lombok.extern.log4j.Log4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.condition.ChannelMap;
import org.cern.cms.dbloader.model.construct.KindOfPart;
import org.cern.cms.dbloader.model.construct.Manufacturer;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.construct.PartAttrList;
import org.cern.cms.dbloader.model.managemnt.Institution;
import org.cern.cms.dbloader.model.managemnt.Location;
import org.cern.cms.dbloader.model.serial.map.AttrBase;
import org.cern.cms.dbloader.model.serial.map.AttrCatalog;
import org.cern.cms.dbloader.model.serial.map.Attribute;
import org.cern.cms.dbloader.model.serial.map.CondAlgorithm;
import org.cern.cms.dbloader.model.serial.map.ModeStage;
import org.cern.cms.dbloader.model.serial.map.PositionSchema;
import org.cern.cms.dbloader.model.serial.part.PartAssembly;
import org.cern.cms.dbloader.util.OperatorAuth;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

@Log4j
public abstract class DaoBase {

    protected static final String DEFAULT_VERSION = "1.0";

    @Inject
    protected PropertiesManager props;

    protected final SessionManager sm;
    protected final Session session;
    protected final OperatorAuth auth;

    public DaoBase(SessionManager sm, OperatorAuth auth) throws Exception {
        this.sm = sm;
        this.auth = auth;
        this.session = sm.getSession();
    }
    
    protected AttrBase resolveAttrBase(Attribute attribute, AttrCatalog attrCatalog) throws XMLParseException {
        
        AttrBase base = (AttrBase) session.createCriteria(PositionSchema.class)
                .add(Restrictions.eq("name", attribute.getValue()))
                .add(Restrictions.eq("attrCatalog", attrCatalog))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .uniqueResult();
        
        if (base != null) {
            return base;
        }

        base = (AttrBase) session.createCriteria(ModeStage.class)
                .add(Restrictions.eq("name", attribute.getValue()))
                .add(Restrictions.eq("attrCatalog", attrCatalog))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .uniqueResult();
        
        if (base != null) {
            return base;
        }

        base = (AttrBase) session.createCriteria(CondAlgorithm.class)
                .add(Restrictions.eq("name", attribute.getValue()))
                .add(Restrictions.eq("attrCatalog", attrCatalog))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .uniqueResult();
        
        if (base != null) {
            return base;
        }

        throw new XMLParseException(String.format("Not resolved: %s", attribute));

    }
    
    protected final Location resolveInstituteLocation(String institutionName, String locationName, String insertionUsr) {

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
            institution.setLastUpdateUser(resolveInsertionUser(insertionUsr));
            institution.setInsertUser(resolveInsertionUser(insertionUsr));
            session.save(institution);
        }

        if (location == null) {
            location = new Location();
            location.setName(locationName);
            location.setInstitution(institution);
            location.setLastUpdateUser(resolveInsertionUser(insertionUsr));
            location.setInsertUser(resolveInsertionUser(insertionUsr));
            session.save(location);
            institution.getLocations().add(location);
        }

        log.info(String.format("Resolved: %s", location));
        return location;
    }

    protected Manufacturer resolveManufacturer(String name) {
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

    protected KindOfPart resolveKindOfPart(String kindOfPartName) throws XMLParseException {

        KindOfPart kop = (KindOfPart) session.createCriteria(KindOfPart.class)
                .add(Restrictions.eq("name", kindOfPartName))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .uniqueResult();

        if (kop == null) {
            throw new XMLParseException(String.format("Kind of part not found: %s", kindOfPartName));
        }        
        
        return kop;
    }
    
    protected Part resolvePart(Part part, boolean failIfNotFound) throws Exception {

        Part xmlPart = part;
        Part dbPart = null;
        KindOfPart kop;

        if (xmlPart.getId() != null) {

            dbPart = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("id", xmlPart.getId()))
                    .add(Restrictions.eq("deleted", Boolean.FALSE))
                    .uniqueResult();
            
            if (dbPart == null) {
                throw new XMLParseException(String.format("Part for PART_ID not found: %s", xmlPart));
            }    

        }

        if (dbPart == null && xmlPart.getBarcode() != null) {

            dbPart = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("barcode", xmlPart.getBarcode()))
                    .add(Restrictions.eq("deleted", Boolean.FALSE))
                    .uniqueResult();

        }

        if (dbPart == null) {

            if (xmlPart.getKindOfPartName() == null) {
                throw new XMLParseException(String.format("Kind of part not defined for %s", xmlPart));
            }        
            
            kop = resolveKindOfPart(xmlPart.getKindOfPartName());
            
            if (kop == null) {
                throw new XMLParseException(String.format("Not resolved kind of part for: %s", xmlPart));
            }

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

        // If part not resolved - give-up
        
        if (failIfNotFound && dbPart == null) {
            throw new XMLParseException(String.format("Not resolved: %s", xmlPart));
        }

        log.info(String.format("Resolved: %s", dbPart));
        
        return dbPart;

    }

    protected ChannelMap resolveChannelMap(ChannelBase xmChannel, boolean failIfNotFound) throws Exception {

        ChannelMap dbChannel;

        Criteria c = session.createCriteria(xmChannel.getClass());

        for (Field f : xmChannel.getClass().getDeclaredFields()) {
            String name = f.getName();
            Object value = PropertyUtils.getSimpleProperty(xmChannel, name);
            if (value != null) {
                c.add(Restrictions.eq(name, value));
            }
        }

        ChannelBase cb = (ChannelBase) c.uniqueResult();

        // If part not resolved - give-up
        if (cb == null) {
            throw new XMLParseException(String.format("Not resolved: %s", xmChannel));
        }

        dbChannel = (ChannelMap) session.get(ChannelMap.class, cb.getId());

        // If part not resolved - give-up
        if (failIfNotFound && dbChannel == null) {
            throw new XMLParseException(String.format("Not resolved: %s", cb));
        }

        log.info(String.format("Resolved: %s", dbChannel));

        return dbChannel;

    }
    
    protected Part resolvePartAssembly(PartAssembly pa, boolean failIfNotFound) throws Exception {
        
        Part parent = resolvePart(pa.getParentPart(), true);

        Attribute xmlAttr = pa.getAttribute();
        AttrCatalog catalog = (AttrCatalog) session.createCriteria(AttrCatalog.class)
                .add(Restrictions.eq("name", xmlAttr.getName()))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .uniqueResult();

        if (catalog == null) {
            throw new XMLParseException(String.format("Not resolved attribute catalog for %s", xmlAttr));
        }
        
        AttrBase attrbase = resolveAttrBase(xmlAttr, catalog);
        
        Part child = (Part) session.createCriteria(Part.class)
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .add(Subqueries.propertyIn("id", 
                        DetachedCriteria.forClass(PartAttrList.class)
                                .add(Restrictions.eq("attrBase", attrbase))
                                .add(Restrictions.eq("deleted", Boolean.FALSE))
                                .setProjection(Projections.property("part"))))
                .createCriteria("partTree")
                    .createCriteria("parentPartTree")
                        .add(Restrictions.eq("partId", parent.getId()))
                        .add(Restrictions.eq("deleted", Boolean.FALSE))
                .uniqueResult();
        
        if (failIfNotFound && child == null) {
            throw new XMLParseException(String.format("Part can not be resolved for %s", pa));
        } else {
            log.info(String.format("Resolved: %s", child));
        }
        
        return child;

    }

    protected String resolveInsertionUser (String insertionUser) {

        if (insertionUser != null) {
                return insertionUser;
            } else {
                return auth.getOperatorValue();
        }
    }

}
