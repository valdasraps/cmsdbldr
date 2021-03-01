package org.cern.cms.dbloader.rest.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
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
import org.cern.cms.dbloader.util.OperatorAuth;

/**
 * Load JSON service.
 * @author valdo
 */
@Log4j
public class LoadService {
    
    public enum Extension {
        
        JSON,
        XML,
        ZIP;
        
        public static Extension get(String name) {
            return Extension.valueOf(name.toUpperCase());
        }
        
    }
    
    private static String writeToFileServer(InputStream inputStream, String path) throws IOException {

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

    public final Response load(final InputStream inputStream, final Extension ext, OperatorAuth auth, boolean isTest) {
        PropertiesManager pm = Application.injector.getInstance(PropertiesManager.class);
        FilesManager fm = Application.injector.getInstance(FilesManager.class);
        try {

            final java.nio.file.Path file = Files.createTempFile("Load.", ".".concat(ext.toString()));
            writeToFileServer(inputStream, file.toAbsolutePath().toString());

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
            final java.nio.file.Path file = Files.createTempFile("Load.", ".".concat(ext.toString()));
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

            final java.nio.file.Path file = Files.createTempFile("Load.", ".".concat(ext.toString()));
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
