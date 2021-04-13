package org.cern.cms.dbloader;

import java.util.List;

import org.cern.cms.dbloader.app.*;
import org.cern.cms.dbloader.dao.AuditLogDao;
import org.cern.cms.dbloader.manager.*;

import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.util.PropertiesException;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.util.OperatorAuth;

@Log4j
@RequiredArgsConstructor
public class DbLoader {

    private final PropertiesManager props;

    private void run() throws Throwable {
        
        LogManager.setLogging(props);
        
        /**
         * Modify individual classes. This routine must be run before the class
         * loader tries to access the to-be-modified classes!
         */
        EntityModificationManager.modify(props);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {

                bind(PropertiesManager.class).toInstance(props);
                install(new FactoryModuleBuilder().build(ResourceFactory.class));

            }
        });

        ResourceFactory rf = injector.getInstance(ResourceFactory.class);
        FilesManager fm = injector.getInstance(FilesManager.class);

        if (props.isSchema()) {
            XmlManager xmlm = injector.getInstance(XmlManager.class);
            xmlm.generateSchema(props.getSchemaParent());
            return;
        }
        
        CondApp condApp = injector.getInstance(CondApp.class);
        if (condApp.handleInfo()) {
            return;
        }

        PartApp partApp = injector.getInstance(PartApp.class);
        if (partApp.handleInfo()) {
            return;
        }

        DatasetApp datasetApp = injector.getInstance(DatasetApp.class);
        if (datasetApp.handleInfo()) {
            return;
        }

        /**
         * Processing files from here!
         */
        if (props.getArgs().isEmpty()) {
            throw new IllegalArgumentException("No input files provided");
        }

        OperatorAuth auth = props.getOperatorAuth();
        
        // Loop archives
        for (FileBase archive : fm.getFiles(props.getArgs())) {

            loadArchive(injector, archive, auth, props.isTest());

        }

    }
    
    public void loadArchive(Injector injector, FileBase archive, OperatorAuth auth) throws Throwable {
        this.loadArchive(injector, archive, auth, false);
    }
    
    public void loadArchive(Injector injector, FileBase archive, OperatorAuth auth, boolean isTest) throws Throwable {
        
        ResourceFactory rf = injector.getInstance(ResourceFactory.class);
        CondApp condApp = injector.getInstance(CondApp.class);
        PartApp partApp = injector.getInstance(PartApp.class);
        ChannelApp channelApp = injector.getInstance(ChannelApp.class);
        ConfigApp configApp = injector.getInstance(ConfigApp.class);
        TrackingApp trackingApp = injector.getInstance(TrackingApp.class);
        AssemblyApp assemblyApp = injector.getInstance(AssemblyApp.class);
        
        // Array of audit logs to handle
        List<AuditLogDao> auditLogs = new ArrayList<>();
        
        // Start archive log if needed
        AuditLogDao archiveLog = null;
        if (archive.isArchive()) {
            
            archiveLog = rf.createAuditDao(archive, auth);
            archiveLog.saveProcessing(isTest);
            auditLogs.add(archiveLog);
            
        }
        
        Throwable error = null;
        
        // Start session & transaction
        try (SessionManager sm = injector.getInstance(SessionManager.class)) {
            
            // Loop data files in archive
            for (DataFile data : archive.getDataFiles()) {

                // set Mask to hide real user
                auth.setOperatorName(data.getRoot().getOperatorName());

                // Start datafile log
                AuditLogDao dataLog = rf.createAuditDao(data, auth);
                dataLog.saveProcessing(isTest);
                auditLogs.add(dataLog);

                try {

                    log.info(String.format("Processing %s", data));
                  
                    AppBase app = null;

                    switch (data.getType()){
                        case PART:
                            app = partApp;
                            break;
                        case CHANNEL:
                            app = channelApp;
                            break;
                        case CONDITION:
                            app = condApp;
                            break;
                        case KEY:
                        case KEY_ALIAS:
                        case VERSION_ALIAS:
                            app = configApp;
                            break;
                        case REQUEST:
                        case SHIPMENT:
                            app = trackingApp;
                            break;
                        case ASSEMBLY_STEP:
                            app = assemblyApp;
                            break;
                    }

                    if (app != null) {
                        
                        app.checkPermission(auth);
                        app.handleData(sm, data, dataLog.getLog(), auth);
                        dataLog.saveSuccess(isTest);
                        
                    }

                } catch (Error ex) {
                
                    error = ex;
                    
                } catch (RuntimeException ex) {
                    
                    error = ex;
                    
                } catch (Exception ex) {

                    error = ex;
                    
                } finally {
                    
                    if (error != null) {
                        
                        dataLog.saveFailure(error, isTest);
                        
                        // Force rollback, not needed but in any case
                        try {
                            sm.rollback();
                        } catch (Exception ex) {
                            log.error(ex);
                        }
                            
                        throw error;
                        
                    }
                    
                }

            }

            if (isTest) {

                log.info("Rollback transaction (loader test)");
                sm.rollback();

            } else {

                log.info("commit transaction");
                sm.commit();

            }

        } catch (Error ex) {

            error = ex;
            
        } catch (RuntimeException ex) {

            error = ex;
            
        } catch (Exception ex) {

            error = ex;
            
        } finally {
        
            if (error != null) {
                
                for (AuditLogDao auditLog: auditLogs) {
                    auditLog.saveFailure(error, isTest);
                }
                
                throw error;
                
            } else {
                
                if (archiveLog != null) {
                    archiveLog.saveSuccess(isTest);
                }
                
            }
            
        }
    }

    public static void main(String[] args) {

        Logger.getRootLogger().setLevel(Level.OFF);
        
        // Install SLF4J logger to direct all java.util.logging messages to log4j
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        try {

            final PropertiesManager props = new CLIPropertiesManager(args);

            if (props.printVersion()) {
                System.exit(0);
            }

            if (props.printHelp()) {
                System.exit(0);
            }

            DbLoader loader = new DbLoader(props);
            loader.run();

            System.exit(0);

        } catch (PropertiesException ex) {

            System.err.println("ERROR: " + ex.getMessage());
            System.exit(1);

        } catch (Throwable ex) {

            ex.printStackTrace(System.err);
            System.exit(2);

        }

    }

}
