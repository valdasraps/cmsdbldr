package org.cern.cms.dbloader.rest.provider;

import java.util.Collections;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.rest.LoaderApplication;

/**
 * Data loading entry point for RESTful API
 * @author valdo
 */
@Log4j
@Path("/")
public class Load extends ProviderBase {
    
    @GET
    public final String info() {
        return "running";
    }
    
    @GET
    @Path("/load")
    public final Response load(@QueryParam("file") String fileName) {
        try {

            FilesManager fm = LoaderApplication.injector.getInstance(FilesManager.class);

            DbLoader loader = new DbLoader(LoaderApplication.pm);
            for (FileBase fb: fm.getFiles(Collections.singletonList(fileName))) {
                loader.loadArchive(LoaderApplication.injector, fb);
            }
            
            return Response.status(Response.Status.OK).entity("loaded").type(MediaType.TEXT_PLAIN).build();
        
        } catch (Throwable ex) {
            log.warn(ex.getMessage());
            throw buildException(ex);
        }
    }
    
}
