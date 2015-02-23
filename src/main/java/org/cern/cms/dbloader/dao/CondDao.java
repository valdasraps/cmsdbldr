package org.cern.cms.dbloader.dao;

import java.lang.reflect.Field;

import javax.management.modelmbean.XMLParseException;

import lombok.extern.log4j.Log4j;

import org.apache.commons.beanutils.PropertyUtils;
import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.condition.ChannelMap;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.KindOfCondition;
import org.cern.cms.dbloader.model.condition.Run;
import org.cern.cms.dbloader.model.construct.KindOfPart;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.xml.Header;
import org.cern.cms.dbloader.model.xml.Root;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

@Log4j
public class CondDao extends DaoBase {

    private static final String DEFAULT_VERSION = "1.0";
    private static final String NO_RUN_MODE = "no-run";
    private static final long DEFAULT_EMAP_RUN_ID = 10000000L;

    @Inject
    public CondDao(@Assisted HbmManager hbm) {
        super(hbm);
    }

    public void saveCondition(Root root, AuditLog alog) throws Exception {
        Session session = hbm.getSession();
        Transaction tx = session.beginTransaction();
        try {

            KindOfCondition dbKoc = resolveKindOfCondition(session, root.getHeader());

            alog.setExtensionTableName(dbKoc.getExtensionTable());
            alog.setKindOfConditionName(dbKoc.getName());

            Run dbRun = resolveRun(session, root.getHeader());

            if (dbRun != null) {

                if (dbRun.getNumber() != null) {
                    try {
                        alog.setRunNumber(Integer.parseInt(dbRun.getNumber()));
                    } catch (Exception ex) {
                    }
                }

                alog.setRunType(dbRun.getRunType());
                alog.setComment(dbRun.getComment());

            }

            boolean newRun = dbRun.getId() == null;

            alog.setDatasetCount(root.getDatasets().size());
            alog.setDatasetRecordCount(0);

            // Assembling Datasets: resolving parts and assigning other values
            for (Dataset ds : root.getDatasets()) {

                if (ds.getVersion() == null) {
                    ds.setVersion(DEFAULT_VERSION);
                }

				 // Resolving Part or Channel
//				if ((ds.getPart() == null && ds.getChannel() == null)) {
//					throw new XMLParseException(String.format("Part or Channel must be defined for dataset %s", ds));
//				}
                if ((ds.getPart() != null && ds.getChannel() != null)) {
                    throw new XMLParseException(String.format("Both Part and Channel can not be defined for dataset %s", ds));
                }

                if (ds.getPart() != null) {

                    Part dbPart = resolvePart(session, ds);

                    if (alog.getSubdetectorName() == null) {
                        alog.setSubdetectorName(dbPart.getKindOfPart().getSubdetector().getName());
                    }

                    // If KindOfCondition limits part type - check it up
                    if (dbKoc.getKindsOfParts() != null && !dbKoc.getKindsOfParts().isEmpty()) {
                        if (!dbKoc.getKindsOfParts().contains(dbPart.getKindOfPart())) {
                            throw new XMLParseException(String.format("%s is not allowed with %s", dbPart.getKindOfPart(), dbKoc));
                        }
                    }

                } else if (ds.getChannel() != null) {

                    resolveChannelMap(session, ds);

                }

                ds.setRun(root.getHeader().getRun());
                ds.setKindOfCondition(root.getHeader().getKindOfCondition());
                ds.setExtensionTable(root.getHeader().getKindOfCondition().getExtensionTable());

                // Check if the dataset does not exist?
                if (!newRun) {
                    checkDataset(session, ds);
                }

                if (alog.getVersion() == null) {
                    alog.setVersion(ds.getVersion());
                }

                session.save(ds);

                alog.setDatasetRecordCount(alog.getDatasetRecordCount() + ds.getData().size());
                for (CondBase cb : ds.getData()) {
                    cb.setDataset(ds);
                    session.save(cb);
                }

            }

            if (props.isTest()) {
                log.info("rollback transaction (loader test)");
                tx.rollback();
            } else {
                log.info("commit transaction");
                tx.commit();
            }

        } catch (Exception ex) {

            tx.rollback();
            throw ex;

        } finally {

            session.close();

        }
    }

    private KindOfCondition resolveKindOfCondition(Session session, Header header) throws Exception {

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

    private Run resolveRun(Session session, Header header) throws Exception {
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

    private Part resolvePart(Session session, Dataset ds) throws Exception {

        Part xmPart = ds.getPart();
        Part dbPart = null;

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
        ds.setPart(dbPart);

        return dbPart;

    }

    private ChannelMap resolveChannelMap(Session session, Dataset ds) throws Exception {

        ChannelBase xmChannel = ds.getChannel();
        ChannelMap dbChannel = null;

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

    private void checkDataset(Session session, Dataset ds) throws Exception {

        Criteria c = session.createCriteria(Dataset.class)
                .add(Restrictions.eq("kindOfCondition", ds.getKindOfCondition()))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .add(Restrictions.eq("version", ds.getVersion()))
                .add(Restrictions.eq("run", ds.getRun()));

        if (ds.getPart() != null) {
            c.add(Restrictions.eq("part", ds.getPart()))
                    .add(Restrictions.isNull("channelMap"));
        } else {
            c.add(Restrictions.eq("channelMap", ds.getChannelMap()))
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

}
