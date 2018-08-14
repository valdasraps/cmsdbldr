package org.cern.cms.dbloader.rest.provider;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Provider base.
 * @author valdo
 */
public abstract class ProviderBase {
    
    protected WebApplicationException buildException(Throwable cause) {
        if (cause instanceof WebApplicationException) {
            return (WebApplicationException) cause;
        }
        Response.ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST).entity(cause.getMessage()).type(MediaType.TEXT_PLAIN);
        return new WebApplicationException(rb.build());
    }
    
}
