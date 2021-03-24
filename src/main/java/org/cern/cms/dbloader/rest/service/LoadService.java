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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.log4j.Log4j;
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
    
    public static final String WORK_BASE_FOLDER = "/var/cmsdbldr/work/";
    public static final Path JOBS_LOG_FILE = Paths.get("/var/cmsdbldr/jobs.log");
    
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
    
    public LoadService(final RestPropertiesManager pm) {
        this.pm = pm;
        this.workFolder = Paths.get(WORK_BASE_FOLDER, pm.getDetName(), pm.getDbName());
    }
    
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
    
    private static void appendToWorkLog(final Path folder) throws IOException {
        
        // Get current time, i.e. 2021-03-08 16:20:46.049266
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date());
        String logLine = timeStamp + "\t" + folder.toAbsolutePath().toString() + "\n";
        
        Files.write(JOBS_LOG_FILE, logLine.getBytes(), StandardOpenOption.APPEND);
        
    }
    
    private Path createWorkFile(final Extension ext) throws IOException {
        Path folder = Files.createTempDirectory(this.workFolder, "LoadService.".concat(ext.toString()).concat("-"));
        appendToWorkLog(folder);
        final Path file = Files.createTempFile(folder, "LoadServiceFile", ".".concat(ext.toString()));
        return file;
    }

    public final Response load(final InputStream inputStream, final Extension ext, OperatorAuth auth, boolean isTest) {
        PropertiesManager pm = Application.injector.getInstance(PropertiesManager.class);
        FilesManager fm = Application.injector.getInstance(FilesManager.class);
        try {

            final Path file = createWorkFile(ext);
            writeToFile(inputStream, file.toAbsolutePath().toString());

            log.info(String.format("Load request: %s", file.toAbsolutePath()));
            
            DbLoader loader = new DbLoader(pm);
            for (FileBase fb: fm.getFiles(Collections.singletonList(file.toAbsolutePath().toString()))) {
                loader.loadArchive(Application.injector, fb, auth, isTest);
            }

            return Response.status(Response.Status.OK).entity("loaded").type(MediaType.TEXT_PLAIN).build();
        
        } catch (Throwable ex) {
            
            log.warn(ex.getMessage());
            throw buildException(ex);
            
        }
    }

    public final Response loadJsonArray(final String data, final Extension ext, OperatorAuth auth, boolean isTest)  throws Throwable {
        PropertiesManager pm = Application.injector.getInstance(PropertiesManager.class);
        FilesManager fm = Application.injector.getInstance(FilesManager.class);

        JsonManager jmnger = new JsonManager();
        List<String> roots = jmnger.deserilizeRootArray(data);
        List<String> filePaths = new ArrayList<>();
        for (String root: roots) {
            final Path file = createWorkFile(ext);
            Files.write(file, root.getBytes());
            filePaths.add(file.toAbsolutePath().toString());
        }
        DbLoader loader = new DbLoader(pm);
        for (FileBase fb: fm.getFiles(filePaths)) {
            loader.loadArchive(Application.injector, fb, auth, isTest);
        }
        return Response.status(Response.Status.OK).entity("loaded").type(MediaType.MULTIPART_FORM_DATA).build();
    }

    public final Response load(final String data, final Extension ext, OperatorAuth auth, boolean isTest) {
        PropertiesManager pm = Application.injector.getInstance(PropertiesManager.class);
        FilesManager fm = Application.injector.getInstance(FilesManager.class);

        try {

            final Path file = createWorkFile(ext);
            Files.write(file, data.getBytes());

            log.info(String.format("Load request: %s", file.toAbsolutePath()));

            DbLoader loader = new DbLoader(pm);
            for (FileBase fb: fm.getFiles(Collections.singletonList(file.toAbsolutePath().toString()))) {
                loader.loadArchive(Application.injector, fb, auth, isTest);
            }

            return Response.status(Response.Status.OK).entity("loaded").type(MediaType.MULTIPART_FORM_DATA).build();

        } catch (Throwable ex) {

            log.warn(ex.getMessage());
            throw buildException(ex);

        }
    }
    
    private WebApplicationException buildException(Throwable cause) {
        if (cause instanceof WebApplicationException) {
            return (WebApplicationException) cause;
        }
        Response.ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST).entity(cause.getMessage()).type(MediaType.TEXT_PLAIN);
        return new WebApplicationException(rb.build());
    }
    
}
