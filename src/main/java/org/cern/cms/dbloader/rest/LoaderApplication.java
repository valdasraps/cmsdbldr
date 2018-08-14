package org.cern.cms.dbloader.rest;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.java.Log;
import org.cern.cms.dbloader.manager.EntityModificationManager;
import org.cern.cms.dbloader.manager.LogManager;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.cern.cms.dbloader.rest.provider.Load;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * RESTful API application for Single Loader case
 * @author valdo
 */
@Log
public class LoaderApplication {

    public static PropertiesManager pm;
    public static Injector injector;
    public static ResourceFactory rf;
    
    private final int port;
    
    public LoaderApplication(int port, String propertiesFile) throws Exception {

        this.port = port;
        Properties p = new Properties();
        try (InputStream is = new FileInputStream(propertiesFile)) {
            p.load(is);
        }

        pm = new PropertiesManager(p, new String[]{ }) {
            @Override
            public boolean printHelp() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        LogManager.setLogging(pm);
        EntityModificationManager.modify(pm);

        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {

                bind(PropertiesManager.class).toInstance(pm);
                install(new FactoryModuleBuilder().build(ResourceFactory.class));

            }
        });

        rf = injector.getInstance(ResourceFactory.class);
            
    }
    
    public void start() throws Exception {
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        Server server = new Server(this.port);
        server.setHandler(context);

        ServletHolder jersey = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jersey.setInitOrder(0);
        
        jersey.setInitParameter("jersey.config.server.provider.classnames", Load.class.getCanonicalName());
        
    }

    public static void main(String[] args) throws Exception {
        
        int port = Integer.parseInt(args[0]);
        String propertiesFile = args[1];
        
        LoaderApplication app = new LoaderApplication(port, propertiesFile);
        app.start();
    }

}
