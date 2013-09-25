package org.cern.cms.dbloader.dao;

import java.util.List;

import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.EntityHandler;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.condition.Dataset;
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
	
	public Dataset getDataset(Long id) throws Exception {
		Session session = hbm.getSession();
		try {
			return (Dataset)session.createCriteria(Dataset.class)
							.add(Restrictions.eq("id", id))
							.add(Restrictions.eq("deleted", Boolean.FALSE))
							.uniqueResult();
		} finally {
			session.close();
		}
    }
	
	@SuppressWarnings("unchecked")
	public List<? extends CondBase> getDatasetData(CondEntityHandler ceh, Dataset dataset) throws Exception {
		Session session = hbm.getSession();
		try {
			return session.createCriteria(ceh.getEntityClass().getC())
					.add(Restrictions.eq("dataset", dataset))
					.list();
		} finally {
			session.close();
		}
	}
	
}
