package org.cern.cms.dbloader.dao;

import java.math.BigInteger;
import java.util.List;

import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.EntityHandler;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.KindOfCondition;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.cern.cms.dbloader.manager.SessionManager;

public class DatasetDao extends DaoBase {

    @Inject
    public DatasetDao(@Assisted SessionManager sm) throws Exception {
        super(sm);
    }

    @SuppressWarnings("unchecked")
    public List<Dataset> getCondDatasets(EntityHandler<CondBase> tm) throws Exception {
        return (List<Dataset>) session.createCriteria(tm.getEntityClass().getC())
                .setProjection(Projections.distinct(Projections.property("dataset")))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<Dataset> getCondDatasets(KindOfCondition koc) throws Exception {
        return (List<Dataset>) session.createCriteria(Dataset.class)
                .add(Restrictions.eq("kindOfCondition", koc))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .list();
    }

    public Dataset getDataset(BigInteger id) throws Exception {
        return (Dataset) session.createCriteria(Dataset.class)
                .add(Restrictions.eq("id", id))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<? extends CondBase> getDatasetData(CondEntityHandler ceh, Dataset dataset) throws Exception {
        return session.createCriteria(ceh.getEntityClass().getC())
                .add(Restrictions.eq("dataset", dataset))
                .list();
    }

}
