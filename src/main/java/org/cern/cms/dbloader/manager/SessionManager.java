package org.cern.cms.dbloader.manager;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.model.construct.Part;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@Log4j
public class SessionManager implements AutoCloseable {

    private final HbmManager hbm;
    
    @Inject
    private PropertiesManager props;
    
    private Session session = null;
    private Transaction tx = null;
    private Part rootPart;

    @Inject
    public SessionManager(HbmManager hbm) {
        this.hbm = hbm;
    }
    
    public Part getRootPart() throws Exception {
        if (this.rootPart == null) {
            
            this.rootPart = (Part) getSession().createCriteria(Part.class)
                .add(Restrictions.eq("id", props.getRootPartId()))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .uniqueResult();

            if (this.rootPart == null) {
                throw new IllegalArgumentException(String.format("Not resolved ROOT part for ID: %d", props.getRootPartId()));
            }
            
            if (rootPart.getPartTree() == null) {
                throw new Exception(String.format("ROOT Part does not have PartTree: %s", rootPart));
            }
            
        }
        
        return this.rootPart;
    }

    public Session getSession() throws Exception {
        if (session == null) {
            session = hbm.getSession();
            tx = session.beginTransaction();
        }
        return session;
    }
    
    public void commit() {
        tx.commit();
    }
    
    public void rollback() {
        tx.rollback();
    }
    
    @Override
    public void close() throws Exception {
        if (session != null) {
            if (this.tx.isActive()) {
                this.rollback();
            }
            this.session.close();
            this.hbm.close();
        }
    }

}
