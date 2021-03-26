package org.cern.cms.dbloader.rest.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.FileAppender;
import org.apache.log4j.TTCCLayout;
import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.JsonManager;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.rest.Application;
import org.cern.cms.dbloader.rest.RestPropertiesManager;
import org.cern.cms.dbloader.util.OperatorAuth;

/**
 * Load JSON service.
 * @author valdo
 */
@Log4j
public class LoadService {
    
    private static final String WORK_BASE_FOLDER = "/var/cmsdbldr/work/";
    private static final Path JOBS_LOG_FILE = Paths.get("/var/cmsdbldr/jobs.log");
    private static final String OUTPUT_LOG_FILE = "output.log";

    private static final FileAttribute<Set<PosixFilePermission>> WORK_FOLDER_ATTRS = 
            PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr-x"));
    private static final FileAttribute<Set<PosixFilePermission>> WORK_FILE_ATTRS = 
            PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-r--r--"));
    
    public enum Extension {
        
        JSON,
        XML,
        ZIP;
        
        public static Extension get(String name) {
            return Extension.valueOf(name.toUpperCase());
        }
        
    }
    
    private final PropertiesManager pm;
    private final Path workFolder;
    
    /**
     * Constructor.
     * @param pm tuned up PropertiesManager.
     * @throws java.io.IOException
     */
    public LoadService(final RestPropertiesManager pm) throws IOException {
        this.pm = pm;
        this.workFolder = Paths.get(WORK_BASE_FOLDER, pm.getDetName(), pm.getDbName());
        if (!Files.exists(this.workFolder)) {
            Files.createDirectories(this.workFolder);
            Files.setPosixFilePermissions(this.workFolder, WORK_FOLDER_ATTRS.value());
        }
    }
    
    /**
     * Write InputStream to String
     * @param inputStream to write from.
     * @param path to write to.
     * @return resulted string.
     * @throws IOException something went wrong.
     */
    private static String writeToFile(InputStream inputStream, String path) throws IOException {

        try (OutputStream outputStream = new FileOutputStream(new File(path))) {
            
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.flush();
            
        }
        return path;
    }
    
    /** 
     * Create temporary file in folder with extension.
     * @param folder parent folder.
     * @param ext file extension.
     * @return file path.
     * @throws IOException something wrong.
     */
    private Path createWorkFile(final Path folder, final Extension ext) throws IOException {
        return Files.createTempFile(folder, "LoadServiceFile-", "." + ext.toString(), WORK_FILE_ATTRS);
    }

    /**
     * Do the actual loading job (master function).
     * @param auth authentication stuff.
     * @param isTest is it a test run (no commit).
     * @param fileProvider function to list all the files for loading.
     */
    private void loadJob(OperatorAuth auth, boolean isTest, FileProvider fileProvider) {
        
        String step_file = "";
        try {
        
            FilesManager fm = Application.injector.getInstance(FilesManager.class);

            // Create work folder
            Path folder = Files.createTempDirectory(this.workFolder, "LoadService-", WORK_FOLDER_ATTRS);

            // Appending stuff to jobs.log file
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date());
            String logLine = timeStamp + "\t" + folder.toAbsolutePath().toString() + "\n";
            Files.write(JOBS_LOG_FILE, logLine.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);

            // Add output.log appender for the job
            String outputLogFile = Paths.get(folder.toString(), OUTPUT_LOG_FILE).toAbsolutePath().toString();
            FileAppender outLogAppender = new FileAppender(new TTCCLayout(), outputLogFile);
            outLogAppender.setName(OUTPUT_LOG_FILE);
            log.addAppender(outLogAppender);

            // Do the actual loading job
            DbLoader loader = new DbLoader(pm);
            for (FileBase fb: fm.getFiles(fileProvider.getFiles(folder))) {
                step_file = fb.getFilename();
                log.info("Start // " + step_file);
                loader.loadArchive(Application.injector, fb, auth, isTest);
                log.info("Success // " + step_file);
            }

            log.removeAppender(OUTPUT_LOG_FILE);
        
        } catch (Throwable ex) {
            
            log.warn(ex.getMessage());
            log.info("Error // " + step_file);
            
            log.removeAppender(OUTPUT_LOG_FILE);
            
            throw buildException(ex);

        }
        
    }
    
    /**
     * Load stuff from InputStream.
     * @param inputStream input stuff.
     * @param ext extension.
     * @param auth authorization.
     * @param isTest is it a test run (no commit then).
     * @return response.
     */
    public final Response load(final InputStream inputStream, final Extension ext, OperatorAuth auth, boolean isTest) {
        
        loadJob(auth, isTest, (Path folder) -> {
            
            final Path file = createWorkFile(folder, ext);
            writeToFile(inputStream, file.toAbsolutePath().toString());
            return Collections.singletonList(file.toAbsolutePath().toString());
            
        });

        return Response.status(Response.Status.OK).entity("loaded").type(MediaType.TEXT_PLAIN).build();
        
    }

    /**
     * Load data from JSON string.
     * @param data JSON in string format.
     * @param ext extension.
     * @param auth authorization.
     * @param isTest is it a test run (no commit then).
     * @return response.
     */
    public final Response loadJsonArray(final String data, final Extension ext, OperatorAuth auth, boolean isTest) {
        
        loadJob(auth, isTest, (Path folder) -> {

            JsonManager jmnger = new JsonManager();
            List<String> files = new ArrayList<>();
            for (String root: jmnger.deserilizeRootArray(data)) {
                final Path file = createWorkFile(folder, ext);
                Files.write(file, root.getBytes());
                files.add(file.toAbsolutePath().toString());
            }
            return files;

        });            
            
        return Response.status(Response.Status.OK).entity("loaded").type(MediaType.MULTIPART_FORM_DATA).build();
    }

    /**
     * Load some other string data.
     * @param data data to load babe.
     * @param ext extension.
     * @param auth authorization.
     * @param isTest is it a test run (no commit then).
     * @return response.
     */
    public final Response load(final String data, final Extension ext, OperatorAuth auth, boolean isTest) {
        
        loadJob(auth, isTest, (Path folder) -> {

            final Path file = createWorkFile(folder, ext);
            Files.write(file, data.getBytes());
            return Collections.singletonList(file.toAbsolutePath().toString());
            
        });
        
        return Response.status(Response.Status.OK).entity("loaded").type(MediaType.MULTIPART_FORM_DATA).build();

    }
    
    /**
     * Biulding Web Runtime exception.
     * @param cause based on this.
     * @return Runtime stuff for Jetty to handle.
     */
    private WebApplicationException buildException(Throwable cause) {
        if (cause instanceof WebApplicationException) {
            return (WebApplicationException) cause;
        }
        Response.ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST).entity(cause.getMessage()).type(MediaType.TEXT_PLAIN);
        return new WebApplicationException(rb.build());
    }
    
    /**
     * Interface instead of default utility because of exception.
     */
    @FunctionalInterface
    private interface FileProvider {
        
        public List<String> getFiles(Path folder) throws Exception;
        
    }
    
}
