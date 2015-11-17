package org.cern.cms.dbloader.dao;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.management.modelmbean.XMLParseException;

import lombok.extern.log4j.Log4j;

import org.apache.commons.beanutils.PropertyUtils;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.condition.ChannelMap;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.KindOfCondition;
import org.cern.cms.dbloader.model.condition.Run;
import org.cern.cms.dbloader.model.construct.KindOfPart;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.iov.Iov;
import org.cern.cms.dbloader.model.iov.Tag;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.xml.Elements;
import org.cern.cms.dbloader.model.xml.Header;
import org.cern.cms.dbloader.model.xml.Root;
import org.cern.cms.dbloader.model.xml.map.MapIov;
import org.cern.cms.dbloader.model.xml.map.MapTag;
import org.cern.cms.dbloader.model.xml.map.Maps;
import org.cern.cms.dbloader.model.xml.part.PartAssembly;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.math.BigDecimal;
import java.util.HashSet;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.model.condition.CondAttrList;
import org.cern.cms.dbloader.model.condition.CondToAttrRltSh;
import org.cern.cms.dbloader.model.construct.PartAttrList;
import org.cern.cms.dbloader.model.xml.map.AttrBase;
import org.cern.cms.dbloader.model.xml.map.AttrCatalog;
import org.cern.cms.dbloader.model.xml.map.Attribute;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Subqueries;

@Log4j
public class CondDao extends DaoBase {

    @Inject
    public CondDao(@Assisted SessionManager sm) {
        super(sm);
    }

    public void saveCondition(Root root, AuditLog alog) throws Exception {       

        Map<BigInteger, Iov> iovMap = new HashMap<>();

        if (root.getElements() != null && root.getMaps() != null) {
            iovMap = mapIov2Tag(root.getElements(), root.getMaps());
        }

        KindOfCondition dbKoc = resolveKindOfCondition(root.getHeader());

        alog.setExtensionTableName(dbKoc.getExtensionTable());
        alog.setKindOfConditionName(dbKoc.getName());

        Run dbRun = resolveRun(root.getHeader());

        if (dbRun != null) {

            if (dbRun.getNumber() != null) {
                try {
                    alog.setRunNumber(Integer.parseInt(dbRun.getNumber()));
                } catch (Exception ex) {
                    // Ignore
                }
            }

            alog.setRunType(dbRun.getRunType());
            alog.setComment(dbRun.getComment());

        }

        boolean newRun = (dbRun == null ? null : dbRun.getId() == null);

        alog.setDatasetCount(root.getDatasets().size());
        alog.setDatasetRecordCount(0);

        // Assembling Datasets: resolving parts and assigning other values
        for (Dataset ds : root.getDatasets()) {

            if (ds.getVersion() == null) {
                ds.setVersion(DEFAULT_VERSION);
            }

            if (!iovMap.isEmpty()) {
//              No dataset idref check provided
                mapIov2Datasets(ds, iovMap);
            }

            if ((ds.getPart() != null && ds.getChannel() != null) ||
                (ds.getPart() != null && ds.getPartAssembly() != null) ||
                (ds.getChannel() != null && ds.getPartAssembly() != null)) {
                throw new XMLParseException(String.format("One and Only One of Part, PartAssembly and Channel must be defined for Dataset %s", ds));
            }

            if (ds.getPart() != null) {

                Part dbPart = resolvePart(ds.getPart());
                ds.setPart(dbPart);

                if (alog.getSubdetectorName() == null) {
                    alog.setSubdetectorName(dbPart.getKindOfPart().getSubdetector().getName());
                }

                // If KindOfCondition limits part type - check it up
                if (dbKoc.getKindsOfParts() == null
                        || dbKoc.getKindsOfParts().isEmpty()
                        || !dbKoc.getKindsOfParts().contains(dbPart.getKindOfPart())) {
                    throw new XMLParseException(String.format("%s is not allowed with %s", dbPart.getKindOfPart(), dbKoc));
                }

            } else if (ds.getChannel() != null) {

                resolveChannelMap(ds);

            } else if (ds.getPartAssembly() != null) {

                resolvePartAssembly(ds);

            }

            ds.setRun(root.getHeader().getRun());
            ds.setKindOfCondition(root.getHeader().getKindOfCondition());
            ds.setExtensionTable(root.getHeader().getKindOfCondition().getExtensionTable());

            if (ds.getAttributes() != null) {
                ds.setAttrList(new HashSet<CondAttrList>());
                for (Attribute attr : ds.getAttributes()) {
                    ds.getAttrList().add(resolveAttribute(attr, ds));
                }
            }

            // Check if the dataset does not exist?
            if (!newRun) {
                checkDataset(ds);
            }

            alog.setVersion(ds.getVersion());
            try {
                if (ds.getSubversion() != null) {
                    alog.setSubversion(new BigDecimal(ds.getSubversion()).intValue());
                }
            } catch (NumberFormatException ex) {
                // Ignore
            }

            session.save(ds);

            alog.setDatasetRecordCount(alog.getDatasetRecordCount() + ds.getData().size());
            for (CondBase cb : ds.getData()) {
                cb.setDataset(ds);
                session.save(cb);
            }

        }

    }

    public KindOfCondition getCondition(BigInteger id) throws Exception {
        return (KindOfCondition) session.createCriteria(KindOfCondition.class)
                .add(Restrictions.eq("id", id))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .uniqueResult();
    }

    private KindOfCondition resolveKindOfCondition(Header header) throws Exception {

        KindOfCondition xmKoc = header.getKindOfCondition();
        KindOfCondition dbKoc = (KindOfCondition) session.createCriteria(KindOfCondition.class)
                .add(Restrictions.eq("name", xmKoc.getName()))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .add(Restrictions.eq("extensionTable", xmKoc.getExtensionTable()))
                .uniqueResult();

        if (dbKoc == null) {
            throw new XMLParseException(String.format("Not resolved: %s", xmKoc));
        }

        log.info(String.format("Resolved: %s", dbKoc));
        header.setKindOfCondition(dbKoc);

        return dbKoc;
    }

    private Run resolveRun(Header header) throws Exception {
        Run xmRun = header.getRun();
        Run dbRun = null;

        if (NO_RUN_MODE.equals(xmRun.getMode())) {

            log.info(String.format("No Run mode: %s", xmRun));
            dbRun = (Run) session.get(Run.class, DEFAULT_EMAP_RUN_ID);

        } else {

            if (xmRun.getName() == null && (xmRun.getNumber() == null || xmRun.getRunType() == null)) {
                throw new XMLParseException(String.format("%s identification not correct: (name or (number and type)) must be provided", xmRun));
            }

            if (xmRun.getName() != null) {
                dbRun = (Run) session.createCriteria(Run.class)
                        .add(Restrictions.eq("name", xmRun.getName()))
                        .add(Restrictions.eq("deleted", Boolean.FALSE))
                        .uniqueResult();
            } else {
                dbRun = (Run) session.createCriteria(Run.class)
                        .add(Restrictions.eq("number", xmRun.getNumber()))
                        .add(Restrictions.eq("runType", xmRun.getRunType()))
                        .add(Restrictions.eq("deleted", Boolean.FALSE))
                        .uniqueResult();
            }

        }

        if (dbRun != null) {
            header.setRun(dbRun);
            log.info(String.format("Resolved: %s", dbRun));
        } else {
            dbRun = xmRun;
            log.info(String.format("Not resolved: %s. Will attempt to create.", dbRun));
        }

        return dbRun;

    }

    private Part resolvePart(Part part) throws Exception {

        Part xmPart = part;
        Part dbPart = null;

        // Get the part based on id 
        if (xmPart.getId() != null) {
            dbPart = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("id", xmPart.getId()))
                    .add(Restrictions.eq("deleted", Boolean.FALSE))
                    .uniqueResult();
        }

        // Get the part based on barcode
        if (xmPart.getBarcode() != null) {
            dbPart = (Part) session.createCriteria(Part.class)
                    .add(Restrictions.eq("barcode", xmPart.getBarcode()))
                    .add(Restrictions.eq("deleted", Boolean.FALSE))
                    .uniqueResult();

        } else if (xmPart.getKindOfPartName() != null) {
            KindOfPart kop = (KindOfPart) session.createCriteria(KindOfPart.class)
                    .add(Restrictions.eq("name", xmPart.getKindOfPartName()))
                    .add(Restrictions.eq("deleted", Boolean.FALSE))
                    .uniqueResult();

            // Get the part based on KindOfPart and Name
            if (xmPart.getName() != null) {
                dbPart = (Part) session.createCriteria(Part.class)
                        .add(Restrictions.eq("name", xmPart.getName()))
                        .add(Restrictions.eq("deleted", Boolean.FALSE))
                        .add(Restrictions.eq("kindOfPart", kop))
                        .uniqueResult();

                // Get the part based on KindOfPart and SerialNumber
            } else if (xmPart.getSerialNumber() != null) {
                dbPart = (Part) session.createCriteria(Part.class)
                        .add(Restrictions.eq("serialNumber", xmPart.getSerialNumber()))
                        .add(Restrictions.eq("deleted", Boolean.FALSE))
                        .add(Restrictions.eq("kindOfPart", kop))
                        .uniqueResult();
            }
        }

        // If part not resolved - give-up
        if (dbPart == null) {
            throw new XMLParseException(String.format("Not resolved: %s", xmPart));
        }

        log.info(String.format("Resolved: %s", dbPart));
        
        return dbPart;

    }

    private ChannelMap resolveChannelMap(Dataset ds) throws Exception {

        ChannelBase xmChannel = ds.getChannel();
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
        if (dbChannel == null) {
            throw new XMLParseException(String.format("Not resolved: %s", cb));
        }

        log.info(String.format("Resolved: %s", dbChannel));
        ds.setChannelMap(dbChannel);

        return dbChannel;

    }

    private void checkDataset(Dataset ds) throws Exception {

        Criteria c = session.createCriteria(Dataset.class)
                .add(Restrictions.eq("kindOfCondition", ds.getKindOfCondition()))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .add(Restrictions.eq("version", ds.getVersion()))
                .add(Restrictions.eq("run", ds.getRun()));

        if (ds.getPart() != null) {
            c.add(Restrictions.eq("part", ds.getPart()))
                .add(Restrictions.isNull("channelMap"));
        } else if (ds.getChannelMap() != null) {
            c.add(Restrictions.eq("channelMap", ds.getChannelMap()))
                .add(Restrictions.isNull("part"));
        } else {
            c.add(Restrictions.isNull("channelMap"))
                .add(Restrictions.isNull("part"));
        }

        if (ds.getSubversion() != null) {
            c.add(Restrictions.eq("subversion", ds.getSubversion()));
        } else {
            c.add(Restrictions.isNull("subversion"));
        }

        Dataset dbDs = (Dataset) c.uniqueResult();

        if (dbDs != null) {
            throw new XMLParseException(String.format("Dataset already exists: %s", dbDs));
        }

    }

    private void mapIov2Datasets(Dataset dataset, Map<BigInteger, Iov> iovMap) {
        for (BigInteger key : iovMap.keySet()) {
            dataset.getIovs().add(iovMap.get(key));
        }
    }

    private Map<BigInteger, Iov> mapIov2Tag(Elements elements, Maps maps) {

        Map<BigInteger, Iov> mapIov = new HashMap<>();
        Map<BigInteger, Tag> mapTag = new HashMap<>();

        for (Tag tag : elements.getTags()) {
            mapTag.put(tag.getId(), tag);
        }

        for (Iov iov : elements.getIovs()) {
            mapIov.put(iov.getId(), iov);
        }

        for (MapTag mapT : maps.getTags()) {
            BigInteger tagId = BigInteger.valueOf(mapT.getRefid());
            Tag tag = mapTag.get(tagId);
            for (MapIov mapI : mapT.getIovs()) {
                BigInteger iovId = BigInteger.valueOf(mapI.getRefid());
                Iov iov = mapIov.get(iovId);
                session.save(iov);
                tag.getIovs().add(iov);
            }
            session.save(tag);
        }
        return mapIov;
    }

    private void resolvePartAssembly(Dataset ds) throws Exception {
        PartAssembly pa = ds.getPartAssembly();
        Part parent = resolvePart(pa.getParentPart());

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
                                .setProjection(Projections.property("part"))
                                .setProjection(Projections.property("id"))))
                .createCriteria("partTree")
                    .createCriteria("parentPartTree")
                        .add(Restrictions.eq("partId", parent.getId()))
                .uniqueResult();
        
        if (child == null) {
            throw new XMLParseException(String.format("Part can not be resolved for %s", pa));
        } else {
            log.info(String.format("Resolved: %s", child));
        }
        
        ds.setPart(child);

    }

    private CondAttrList resolveAttribute(Attribute attr, Dataset ds) throws Exception {
        KindOfCondition koc = ds.getKindOfCondition();
                
        AttrCatalog catalog = (AttrCatalog) session.createCriteria(AttrCatalog.class)
                .add(Restrictions.eq("name", attr.getName()))
                .uniqueResult();

        if (catalog == null) {
            throw new XMLParseException(String.format("Not resolved attribute catalog for %s", attr));
        }

        AttrBase attrbase = resolveAttrBase(attr, catalog);

        CondToAttrRltSh condship = (CondToAttrRltSh) session.createCriteria(CondToAttrRltSh.class)
                .add(Restrictions.eq("koc", koc))
                .add(Restrictions.eq("attrCatalog", catalog))
                .uniqueResult();

        if (condship == null) {
            throw new XMLParseException(String.format("Not resolved attribute %s to kind of condition relationship %s", catalog, koc));
        }

        CondAttrList condAttrList = new CondAttrList();
        condAttrList.setCondToAttrRtlSh(condship);
        condAttrList.setAttrBase(attrbase);
        condAttrList.setDataset(ds);
        condAttrList.setDeleted(Boolean.FALSE);

        return condAttrList;

    }
    
}
