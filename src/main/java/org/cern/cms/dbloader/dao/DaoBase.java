package org.cern.cms.dbloader.dao;

import com.google.inject.Inject;
import javax.management.modelmbean.XMLParseException;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.model.xml.map.AttrBase;
import org.cern.cms.dbloader.model.xml.map.AttrCatalog;
import org.cern.cms.dbloader.model.xml.map.Attribute;
import org.cern.cms.dbloader.model.xml.map.CondAlgorithm;
import org.cern.cms.dbloader.model.xml.map.ModeStage;
import org.cern.cms.dbloader.model.xml.map.PositionSchema;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public abstract class DaoBase {

    protected static final String DEFAULT_VERSION = "1.0";
    protected static final String NO_RUN_MODE = "no-run";
    protected static final long DEFAULT_EMAP_RUN_ID = 10000000L;

    @Inject
    protected PropertiesManager props;

    protected final SessionManager sm;
    protected final Session session;

    public DaoBase(SessionManager sm) throws Exception {
        this.sm = sm;
        this.session = sm.getSession();
    }
    
    protected AttrBase resolveAttrBase(Attribute attribute, AttrCatalog attrCatalog) throws XMLParseException {
        
        AttrBase base = (AttrBase) session.createCriteria(PositionSchema.class)
                .add(Restrictions.eq("name", attribute.getValue()))
                .add(Restrictions.eq("attrCatalog", attrCatalog))
                .uniqueResult();
        
        if (base != null) {
            return base;
        }

        base = (AttrBase) session.createCriteria(ModeStage.class)
                .add(Restrictions.eq("name", attribute.getValue()))
                .add(Restrictions.eq("attrCatalog", attrCatalog))
                .uniqueResult();
        
        if (base != null) {
            return base;
        }

        base = (AttrBase) session.createCriteria(CondAlgorithm.class)
                .add(Restrictions.eq("name", attribute.getValue()))
                .add(Restrictions.eq("attrCatalog", attrCatalog))
                .uniqueResult();
        
        if (base != null) {
            return base;
        }

        throw new XMLParseException(String.format("Not resolved: %s", attribute));

    }
    
}
