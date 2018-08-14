package org.cern.cms.dbloader.rest.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.cern.cms.dbloader.rest.ApplicationConfig;

/**
 * Main entry point for RESTful API
 * @author valdo
 */
@Path("/")
public class Router {
    
    private final ApplicationConfig config = ApplicationConfig.getInstance();
    
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public String root() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(config.getProps());
    }
    
}
