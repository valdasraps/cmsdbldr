package org.cern.cms.dbloader.dao;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.management.modelmbean.XMLParseException;

import lombok.extern.log4j.Log4j;

import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.KindOfCondition;
import org.cern.cms.dbloader.model.condition.Run;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.iov.Iov;
import org.cern.cms.dbloader.model.iov.Tag;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.serial.Elements;
import org.cern.cms.dbloader.model.serial.Header;
import org.cern.cms.dbloader.model.serial.map.MapIov;
import org.cern.cms.dbloader.model.serial.map.MapTag;
import org.cern.cms.dbloader.model.serial.map.Maps;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import org.cern.cms.dbloader.manager.DynamicEntityGenerator;
import org.cern.cms.dbloader.manager.LobManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.condition.CondAttrList;
import org.cern.cms.dbloader.model.condition.CondToAttrRltSh;
import org.cern.cms.dbloader.model.serial.map.AttrBase;
import org.cern.cms.dbloader.model.serial.map.AttrCatalog;
import org.cern.cms.dbloader.model.serial.map.Attribute;
import org.cern.cms.dbloader.model.condition.DatasetRoot;
import org.cern.cms.dbloader.util.OperatorAuth;

@Log4j
public class CondDao extends DaoBase {

    @Inject
    private DynamicEntityGenerator enGenerator;
    
    private final LobManager lobm = new LobManager();
    
    @Inject
    public CondDao(@Assisted SessionManager sm, @Assisted OperatorAuth auth) throws Exception {
        super(sm, auth);
    }

    public void saveCondition(DatasetRoot root, AuditLog alog, DataFile file, Dataset parent) throws Exception {
        Header header = root.getHeader();

        if (root.getDatasets().isEmpty()) {
            throw new XMLParseException("No dataset defined!");
        }

        if (header.getKindOfCondition() == null) {
            throw new XMLParseException("No Kind of Conition defined!");
        }

        if (header.getKindOfCondition().getName() == null) {
            throw new XMLParseException("No Kind of Condition name defined!");
        }
        
        // Resolve Condition handler
        CondEntityHandler condeh = enGenerator.getConditionHandler(header.getKindOfCondition().getName());
        if (condeh == null) {
            throw new XMLParseException(String.format("Kind of Condition not resolved: %s", header.getKindOfCondition()));
        }
        
        // Resolve Channel handler
        ChannelEntityHandler chaneh = null;
        if (header.getHint() != null && header.getHint().getChannelMap() != null) {
            chaneh = enGenerator.getChannelHandler(header.getHint().getChannelMap());
            if (chaneh == null) {
                throw new XMLParseException(String.format("Channel Map not resolved: %s", header.getHint()));
            }
        }
        
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

            // Convert data from proxy to true objects
            List<CondBase> data = (List<CondBase>) ds.getData();
            for (int i = 0; i < data.size(); i++) {
                CondBase cb = (CondBase) data.get(i);
                if (cb != null) {
                    CondBase d = cb.getDelegate(condeh.getEntityClass().getC());
                    lobm.lobParser(d, condeh, file.getFile());
                    data.set(i, d);
                }
            }
            
            // Convert channels from proxy to true objects
            if (ds.getChannel() != null) {
                
                if (chaneh == null) {
                    throw new XMLParseException(String.format("Channel Map not resolved: %s. Hint missing?", ds.getChannel()));
                }
                
                ChannelBase cb = ds.getChannel();
                ds.setChannel(cb.getDelegate(chaneh.getEntityClass().getC()));
            }
            
            // Set parent
            ds.setAggregatedDataset(parent);
            
            if (ds.getVersion() == null) {
                ds.setVersion(DEFAULT_VERSION);
            }

            if (!iovMap.isEmpty()) {
                ds.getIovs().addAll(iovMap.values());
                Iov iov = iovMap.values().iterator().next();
                alog.setIntervalOfValidityBegin(iov.getIovBegin());
                alog.setIntervalOfValidityEnd(iov.getIovEnd());
                if (iov.getTags() != null && !iov.getTags().isEmpty()) {
                    alog.setTagName(iov.getTags().iterator().next().getName());
                }
            }
//
//            if ((ds.getPart() != null && ds.getChannel() != null) ||
//                (ds.getPart() != null && ds.getPartAssembly() != null) ||
//                (ds.getChannel() != null && ds.getPartAssembly() != null)) {
//                throw new XMLParseException(String.format("One and Only One of Part, PartAssembly and Channel must be defined for Dataset %s", ds));
//            }

            if ((ds.getPart() != null && ds.getPartAssembly() != null)) {
                throw new XMLParseException(String.format("One and Only One of Part and PartAssembly must be defined for Dataset %s", ds));
            }

            if (ds.getPart() != null) {

                Part dbPart = resolvePart(ds.getPart(), true);
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

            } else if (ds.getPartAssembly() != null) {

                ds.setPart(resolvePartAssembly(ds.getPartAssembly(), true));

            }

            if (ds.getChannel() != null) {

                ds.setChannelMap(resolveChannelMap(ds.getChannel(), true));
            }
            ds.setRun(root.getHeader().getRun());
            ds.setKindOfCondition(root.getHeader().getKindOfCondition());
            ds.setExtensionTable(root.getHeader().getKindOfCondition().getExtensionTable());

            if (ds.getAttributes() != null) {
                ds.setAttrList(new HashSet<>());
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

            ds.setLastUpdateUser(auth.getOperatorValue());
            if (ds.getInsertUser() == null) { 
                ds.setInsertUser(auth.getOperatorValue());
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

        if (dbRun != null) {
            header.setRun(dbRun);
            log.info(String.format("Resolved: %s", dbRun));
        } else {
            dbRun = xmRun;
            dbRun.setLastUpdateUser(auth.getOperatorValue());
            dbRun.setInsertUser(auth.getOperatorValue());
            log.info(String.format("Not resolved: %s. Will attempt to create.", dbRun));
        }

        return dbRun;

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
                tag.getIovs().add(iov);
                iov.getTags().add(tag);
                session.save(iov);
            }
            
            tag.setLastUpdateUser(auth.getOperatorValue());
            if (tag.getInsertUser() == null) { 
                tag.setInsertUser(auth.getOperatorValue());
            }
            
            session.save(tag);
        }
        return mapIov;
    }

    private CondAttrList resolveAttribute(Attribute attr, Dataset ds) throws Exception {
        KindOfCondition koc = ds.getKindOfCondition();
                
        AttrCatalog catalog = (AttrCatalog) session.createCriteria(AttrCatalog.class)
                .add(Restrictions.eq("name", attr.getName()))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .uniqueResult();

        if (catalog == null) {
            throw new XMLParseException(String.format("Not resolved attribute catalog for %s", attr));
        }

        AttrBase attrbase = resolveAttrBase(attr, catalog);

        CondToAttrRltSh condship = (CondToAttrRltSh) session.createCriteria(CondToAttrRltSh.class)
                .add(Restrictions.eq("koc", koc))
                .add(Restrictions.eq("attrCatalog", catalog))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
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
