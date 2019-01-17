package org.cern.cms.dbloader.manager;

import com.google.inject.Inject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.ServiceRegistryBuilder;
import org.reflections.Reflections;

@Log4j
public class HbmManager implements AutoCloseable {

    private static final String PROPERTY_URL = "hibernate.connection.url";
    private static final String PROPERTY_USERNAME = "hibernate.connection.username";
    private static final String PROPERTY_PASSWORD = "hibernate.connection.password";

    private final PropertiesManager props;
    private final Configuration cfg;
    private SessionFactory sessionFactory;
    private final Set<Class<?>> classPool = new HashSet<>();

    @Inject
    private CondManager condm;

    @Inject
    public HbmManager(final PropertiesManager props) throws Exception {
        this.props = props;
        this.cfg = new Configuration();
        cfg.setProperty(PROPERTY_URL, props.getUrl());
        cfg.setProperty(PROPERTY_USERNAME, props.getUsername());
        cfg.setProperty(PROPERTY_PASSWORD, props.getPassword());
        cfg.setProperty("hibernate.show_sql", Boolean.toString(props.isPrintSQL()));

        Reflections reflections = new Reflections("org.cern.cms.dbloader.model");
        classPool.addAll(reflections.getTypesAnnotatedWith(MappedSuperclass.class));
        classPool.addAll(reflections.getTypesAnnotatedWith(Entity.class));

    }

    public Session getSession() throws Exception {
        if (sessionFactory == null) {

            for (CondEntityHandler eh : condm.getConditionHandlers()) {
                classPool.add(eh.getEntityClass().getC());
            }
            for (ChannelEntityHandler eh : condm.getChannelHandlers()) {
                classPool.add(eh.getEntityClass().getC());
            }
            
            for (Class<?> entityClass : classPool) {
                cfg.addAnnotatedClass(entityClass);
            }

            cfg.configure();
            cfg.buildSessionFactory(
                    new ServiceRegistryBuilder().applySettings(
                            cfg.getProperties()).buildServiceRegistry())
                    .close();

            // Attach Entity class Table schema
            Iterator<PersistentClass> pcIt = cfg.getClassMappings();
            while (pcIt.hasNext()) {
                PersistentClass pc = pcIt.next();

                if (pc.getEntityName().startsWith(PropertiesManager.CONSTRUCT_PACKAGE)) {
                    pc.getTable().setSchema(props.getCoreConstructSchemaName());
                }

                if (pc.getEntityName().startsWith(PropertiesManager.CONSTRUCT_EXT_PACKAGE)) {
                    pc.getTable().setSchema(props.getExtConstructSchemaName());
                }

                if (pc.getEntityName().startsWith(PropertiesManager.CONDITION_CORE_PACKAGE)) {
                    pc.getTable().setSchema(props.getCoreConditionSchemaName());
                }

                if (pc.getEntityName().startsWith(PropertiesManager.CONDITION_EXT_PACKAGE)) {
                    pc.getTable().setSchema(props.getExtConditionSchemaName());
                }

                if (pc.getEntityName().startsWith(PropertiesManager.MANAGEMNT_CORE_PACKAGE)) {
                    pc.getTable().setSchema(props.getCoreManagemntSchemaName());
                }

                if (pc.getEntityName().startsWith(PropertiesManager.IOV_CORE_PACKAGE) ||
                    pc.getEntityName().startsWith(PropertiesManager.CONFIG_CORE_PACKAGE)) {
                    pc.getTable().setSchema(props.getIovCoreSchemaName());
                }

                if (pc.getEntityName().startsWith(PropertiesManager.CONDITION_XML_PACKAGE)) {
                    pc.getTable().setSchema(props.getCoreAttributeSchemaName());
                }

            }

            cfg.setProperty("hibernate.show_sql", Boolean.toString(props.isPrintSQL()));
            this.sessionFactory = cfg.buildSessionFactory(
                    new ServiceRegistryBuilder().applySettings(
                            cfg.getProperties()).buildServiceRegistry());

        }

        return this.sessionFactory.openSession();
    }

    @Override
    public void close() {
        try {
            if (this.sessionFactory != null) {
                this.sessionFactory.close();
            }
        } catch (Exception ex) {
            log.warn("Exception while closing SessionManager.", ex);
        }
    }

}
