package org.cern.cms.dbloader.rest;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
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
import org.cern.cms.dbloader.rest.service.AuthService;
import org.cern.cms.dbloader.rest.service.LoadService;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ServerProperties;

/**
 * RESTful API application for the single Loader
 * @author valdo
 */
@Log4j
public class Application implements Daemon {
    
    private final static String PORT_KEY = "api-port";
    private final static String XSD_FILE_KEY = "xsd-file";
    private final static String DOC_FILE_KEY = "doc-file";
    public final static Named XSD_NAME = Names.named(XSD_FILE_KEY);
    public final static Named DOC_NAME = Names.named(DOC_FILE_KEY);
    
    public static Injector injector;
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
        
        PropertiesManager pm = new PropertiesManager(props, new String[]{ }) {
            
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
                bind(AuthService.class).toInstance(new AuthService(props));
                bind(LoadService.class).toInstance(new LoadService());
                install(new FactoryModuleBuilder().build(ResourceFactory.class));
                
                if (props.getProperty(XSD_FILE_KEY) != null) {
                    bindConstant().annotatedWith(XSD_NAME).to(props.getProperty(XSD_FILE_KEY));
                }
                if (props.getProperty(DOC_FILE_KEY) != null) {
                    bindConstant().annotatedWith(DOC_NAME).to(props.getProperty(DOC_FILE_KEY));
                }

            }
        });

    }
        
    private void initServer(final int port) {
        
        org.eclipse.jetty.util.log.Log.setLog(LogFactory.createLogger());

        HttpConfiguration config = new HttpConfiguration();
        config.setRequestHeaderSize(65535);
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        server = new Server();
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(config));
        http.setPort(port);
        server.setHandler(context);
        server.setConnectors(new Connector[]{ http });

        server.setRequestLog(LogFactory.createRequestLogger());

        ServletHolder jersey = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");

        jersey.setInitOrder(0);
        // http://qaru.site/questions/11412132/no-injection-source-found-for-a-parameter-of-type-public-javaxwsrscoreresponse-jersey-multipartfeature
        jersey.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES, MultiPartFeature.class.getCanonicalName());
        jersey.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "org.cern.cms.dbloader.rest.provider");
        
//        jerseyServlet.setInitParameter(
//       "jersey.config.server.provider.classnames",
//       EntryPoint.class.getCanonicalName());
        
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