package org.cern.cms.dbloader.rest.provider;

import com.google.inject.Injector;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Response;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FilenameUtils;
import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.JsonManager;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.rest.Application;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * Data loading entry point for RESTful API
 * @author valdo
 */
@Log4j
@Path("/")
public class Load extends ProviderBase {
    
    private final Injector injector = Application.injector;
    private final PropertiesManager pm = Application.pm;
    private final FilesManager fm = injector.getInstance(FilesManager.class);
    
    @GET
    public final String info() {
        return Application.pm.getVersion();
    }
    
    @POST
    @Path("/load/json")
    public final Response loadJson(String data) throws Throwable {
        if (data.startsWith("[") && data.endsWith("]")) {
            return loadJsonArray(data, "json");
        } else {
            return load(data, "json");
        }
    }

    @POST
    @Path("/load/xml")
    public final Response loadXml(String data) {
        return load(data, "xml");
    }

    @POST
    @Path("/load/file")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public final Response loadZip(@FormDataParam("uploadFile") InputStream fileInputStream,
                                  @FormDataParam("uploadFile") FormDataContentDisposition fileFormDataContentDisposition) {
        String ext = FilenameUtils.getExtension(fileFormDataContentDisposition.getFileName());
        return load(fileInputStream, ext);
    }

    private String writeToFileServer(InputStream inputStream, String path) throws IOException {

        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(new File(path));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.flush();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally{
            //release resource, if any
            outputStream.close();
        }
        return path;
    }


    private final Response load(final InputStream inputStream, final String ext) {
        try {

            final java.nio.file.Path file = Files.createTempFile("Load.", ".".concat(ext));
            writeToFileServer(inputStream, file.toAbsolutePath().toString());

            log.info(String.format("Load request: %s", file.toAbsolutePath()));
            
            DbLoader loader = new DbLoader(pm);
            for (FileBase fb: fm.getFiles(Collections.singletonList(file.toAbsolutePath().toString()))) {
                loader.loadArchive(injector, fb);
            }

            return Response.status(Response.Status.OK).entity("loaded").type(MediaType.TEXT_PLAIN).build();
        
        } catch (Throwable ex) {
            
            log.warn(ex.getMessage());
            throw buildException(ex);
            
        }
    }

    public final Response loadJsonArray(final String data, final String ext)  throws Throwable {
        JsonManager jmnger = new JsonManager();
        List<String> roots = jmnger.deserilizeRootArray(data);
        List<String> filePaths = new ArrayList<String>();
        for (String root: roots) {
            final java.nio.file.Path file = Files.createTempFile("Load.", ".".concat(ext));
            Files.write(file, root.getBytes());
            filePaths.add(file.toAbsolutePath().toString());
        }
        FilesManager fm = injector.getInstance(FilesManager.class);
        DbLoader loader = new DbLoader(pm);
        for (FileBase fb: fm.getFiles(filePaths)) {
            loader.loadArchive(injector, fb);
        }
        return Response.status(Response.Status.OK).entity("loaded").type(MediaType.MULTIPART_FORM_DATA).build();
    }

    private final Response load(final String data, final String ext) {
        try {

            final java.nio.file.Path file = Files.createTempFile("Load.", ".".concat(ext));
            Files.write(file, data.getBytes());

            log.info(String.format("Load request: %s", file.toAbsolutePath()));

            DbLoader loader = new DbLoader(pm);
            for (FileBase fb: fm.getFiles(Collections.singletonList(file.toAbsolutePath().toString()))) {
                loader.loadArchive(injector, fb);
            }

            return Response.status(Response.Status.OK).entity("loaded").type(MediaType.MULTIPART_FORM_DATA).build();

        } catch (Throwable ex) {

            log.warn(ex.getMessage());
            throw buildException(ex);

        }
    }
}