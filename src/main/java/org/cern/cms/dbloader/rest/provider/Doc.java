package org.cern.cms.dbloader.rest.provider;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.name.Named;
import java.io.File;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.rest.Application;

@Log4j
@Path("/")
public class Doc {
    
    private static Response getFile(Named name, MediaType type) {
        return getFile(name, type, null);
    }
    
    private static Response getFile(Named name, MediaType type, String fileName) {
        Binding<String> binding = Application.injector.getExistingBinding(Key.get(String.class, name));
        if (binding != null) {
            
            File file;
            if (fileName != null) {
                file = new File(binding.getProvider().get(), fileName);
            } else {
                file = new File(binding.getProvider().get());
            }
            
            if (file.exists() && file.isFile() && file.canRead()) {
                return Response.ok(file, type).build();
            }
            
        }
        
        return Response.status(Response.Status.NOT_FOUND).build();
        
    }
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/doc/xsd")
    public final Response getSchema() {
        return getFile(Application.XSD_NAME, MediaType.APPLICATION_XML_TYPE);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/doc/doc")
    public final Response getDocument() {
        return getFile(Application.DOC_NAME, MediaType.TEXT_HTML_TYPE);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/doc/xml")
    public final Response getXmlSample() {
        return getFile(Application.XML_NAME, MediaType.TEXT_HTML_TYPE, "index.html");
        
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/doc/xml/{filename:.*}")
    public final Response getXmlSample(@PathParam("filename") String filename) {
        return getFile(Application.XML_NAME, MediaType.APPLICATION_XML_TYPE, filename);
    }
    
}
