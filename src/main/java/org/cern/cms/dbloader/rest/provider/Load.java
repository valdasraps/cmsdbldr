package org.cern.cms.dbloader.rest.provider;

import com.google.inject.Injector;
import java.nio.file.Files;
import java.util.Collections;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.rest.Application;

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
    public final Response loadJson(String data) {
        return load(data, "json");
    }

    @POST
    @Path("/load/xml")
    public final Response loadXml(String data) {
        return load(data, "xml");
    }
    
    private final Response load(final String data, final String ext) {
        try {

            final java.nio.file.Path file = Files.createTempFile("Load.", ".".concat(ext));
            Files.write(file, data.getBytes());
            
            log.info(String.format("Load request: %s.", file.toAbsolutePath()));
            
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
    
}
