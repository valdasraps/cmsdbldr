package org.cern.cms.dbloader.rest;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import java.io.FileInputStream;
import java.util.Properties;
import lombok.extern.log4j.Log4j;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.cern.cms.dbloader.manager.EntityModificationManager;
import org.cern.cms.dbloader.manager.LogManager;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * RESTful API application for the single Loader
 * @author valdo
 */
@Log4j
public class Application implements Daemon {
    
    private final static String PORT_KEY = "api-port";
    
    public static PropertiesManager pm;
    public static Injector injector;
    public static ResourceFactory rf;
    
    private Server server;
    
    public void setup(String[] args) throws Exception {

        if (args.length == 0) {
            throw new IllegalArgumentException("Please provide loader descriptor file");
        }

        String propsFileName = args[0];
        Properties props = new Properties();
        try (FileInputStream propsin = new FileInputStream(propsFileName)) {
            props.load(propsin);
        }
        
        if (!props.containsKey(PORT_KEY)) {
            throw new IllegalArgumentException(String.format("API port key not defined: %s", PORT_KEY));
        }
        
        int port = Integer.parseInt(props.getProperty(PORT_KEY));

        log.info(String.format("Loading API on %d for %s..", port, propsFileName));
        
        initLoader(props);
        initServer(port);
        
    }
    
    private void initLoader(final Properties props) throws Exception {
        
        pm = new PropertiesManager(props, new String[]{ }) {
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
        
    private void initServer(final int port) {
        
        org.eclipse.jetty.util.log.Log.setLog(LogFactory.createLogger());
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        server = new Server(port);
        server.setHandler(context);
        
        server.setRequestLog(LogFactory.createRequestLogger());

        ServletHolder jersey = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jersey.setInitOrder(0);
        
        jersey.setInitParameter("jersey.config.server.provider.packages", "org.cern.cms.dbloader.rest.provider");
        
    }
        
    @Override
    public void start() throws Exception {
        server.start();
        server.join();
    }
    
    @Override
    public void init(DaemonContext dc) throws DaemonInitException, Exception {
        setup(dc.getArguments());
    }

    @Override
    public void stop() throws Exception {
        server.stop();
    }

    @Override
    public void destroy() {
        server.destroy();
    }

    public static void main(String[] args) throws Exception {
        Application app = new Application();
        app.setup(args);
        app.start();
    }

}