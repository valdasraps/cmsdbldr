package org.cern.cms.dbloader.rest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import lombok.extern.log4j.Log4j;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.cern.cms.dbloader.rest.provider.Router;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * RESTful API application
 * @author valdo
 */
@Log4j
public class Application implements Daemon {
    
    private final static String PROP_DIR_KEY = "PROP_DIR";
    private final static String PORT_KEY = "PORT";
    private final static String LOGS_DIR_KEY = "LOGS_DIR";
    
    private final ApplicationConfig config = ApplicationConfig.getInstance();
    private Server server;
    
    public void setup(String[] args) throws Exception {

        if (args.length == 0) {
            throw new IllegalArgumentException("Please provide configuration file");
        }
        
        Properties props = new Properties();
        try (FileInputStream propsin = new FileInputStream(args[0])) {
            props.load(propsin);
        }
        
        if (!props.containsKey(PORT_KEY)) {
            throw new IllegalArgumentException(String.format("API port key not defined: %s", PORT_KEY));
        }
        
        int port = Integer.parseInt(props.getProperty(PORT_KEY));

        if (!props.containsKey(PROP_DIR_KEY)) {
            throw new IllegalArgumentException(String.format("Loaders properties folder not provided: %s", PROP_DIR_KEY));
        }
        
        Path pfolder = Paths.get(props.getProperty(PROP_DIR_KEY));
        if (Files.exists(pfolder) && Files.isReadable(pfolder) && Files.isDirectory(pfolder)) {
            log.info(String.format("Loaders properties path: %s", pfolder));
        } else {
            throw new FileNotFoundException(String.format("Please provide existing readable Loaders properties folder: %s", pfolder));
        }

        if (!props.containsKey(LOGS_DIR_KEY)) {
            throw new IllegalArgumentException(String.format("Logs folder not provided: %s", LOGS_DIR_KEY));
        }
        
        Path logFolder = Paths.get(props.getProperty(LOGS_DIR_KEY));
        Path logFile;
        if (Files.exists(logFolder) && Files.isReadable(logFolder) && Files.isDirectory(logFolder)) {
            logFile = Paths.get(logFolder.toAbsolutePath().toString(), "application.log");
            log.info(String.format("Log: %s", logFile));
        } else {
            throw new FileNotFoundException(String.format("Please provide existing readable logs folder: %s", logFolder));
        }
        
        config.load(pfolder, logFolder.toAbsolutePath());
        for (String det: config.getProps().keySet()) {
            for (String db: config.getProps().get(det).keySet()) {
                try {
                    config.getProps().get(det).get(db).start();
                } catch (Exception ex) {
                    log.error(String.format("Error while starting service for %s.%s", det, db), ex);
                }
            }
        }
        org.eclipse.jetty.util.log.Log.setLog(new org.cern.cms.dbloader.rest.Logger(Application.class.getSimpleName()));
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        server = new Server(port);
        server.setHandler(context);

        NCSARequestLog requestLog = new NCSARequestLog(logFile.toString());
        requestLog.setAppend(true);
        requestLog.setExtended(false);
        requestLog.setLogTimeZone("GMT");
        requestLog.setLogLatency(true);
        requestLog.setRetainDays(90);

        server.setRequestLog(requestLog);
        
        ServletHolder jersey = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jersey.setInitOrder(0);
        
        jersey.setInitParameter("jersey.config.server.provider.classnames", Router.class.getCanonicalName());
        
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
