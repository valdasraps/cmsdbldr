package org.cern.cms.dbloader.manager;

import com.google.inject.Inject;
import lombok.extern.log4j.Log4j;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Log4j
public class SessionManager implements AutoCloseable {

    private final HbmManager hbm;
    
    @Inject
    private PropertiesManager props;
    
    private Session session = null;
    private Transaction tx = null;

    @Inject
    public SessionManager(HbmManager hbm) {
        this.hbm = hbm;
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
