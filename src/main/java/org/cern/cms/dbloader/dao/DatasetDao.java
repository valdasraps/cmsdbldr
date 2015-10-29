package org.cern.cms.dbloader.dao;

import java.math.BigInteger;
import java.util.List;

import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.EntityHandler;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.KindOfCondition;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class DatasetDao extends DaoBase {

    @Inject
    public DatasetDao(@Assisted HbmManager hbm) {
        super(hbm);
    }

    @SuppressWarnings("unchecked")
    public List<Dataset> getCondDatasets(EntityHandler<CondBase> tm) throws Exception {
        Session session = hbm.getSession();
        Transaction tx = session.beginTransaction();
        try {
            return (List<Dataset>) session.createCriteria(tm.getEntityClass().getC())
                    .setProjection(Projections.distinct(Projections.property("dataset")))
                    .list();
        } finally {
            tx.rollback();
            session.close();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Dataset> getCondDatasets(KindOfCondition koc) throws Exception {
        Session session = hbm.getSession();
        Transaction tx = session.beginTransaction();
        try {
            return (List<Dataset>) session.createCriteria(Dataset.class)
                    .add(Restrictions.eq("kindOfCondition", koc))
                    .list();
        } finally {
            tx.rollback();
            session.close();
        }
    }

    public Dataset getDataset(Session session, BigInteger id) throws Exception {
        return (Dataset) session.createCriteria(Dataset.class)
                .add(Restrictions.eq("id", id))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<? extends CondBase> getDatasetData(Session session, CondEntityHandler ceh, Dataset dataset) throws Exception {
        return session.createCriteria(ceh.getEntityClass().getC())
                .add(Restrictions.eq("dataset", dataset))
                .list();
    }

}
