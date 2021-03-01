package org.cern.cms.dbloader.rest.provider;


import com.google.inject.Binding;
import com.google.inject.Key;
import java.io.*;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FilenameUtils;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.rest.Application;
import org.cern.cms.dbloader.rest.service.AuthService;
import org.cern.cms.dbloader.rest.service.LoadService;
import org.cern.cms.dbloader.util.OperatorAuth;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * Data loading entry point for RESTful API
 * @author valdo
 */
@Log4j
@Path("/")
public class Load {
    
    private final static String TEST_PARAM_NAME = "test";
    private final PropertiesManager pm = Application.injector.getInstance(PropertiesManager.class);
    private final LoadService loader = Application.injector.getInstance(LoadService.class);
    private final AuthService aservice = Application.injector.getInstance(AuthService.class);
    
    @GET
    public final Response info(@Context HttpHeaders headers, @Context UriInfo uriInfo) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        OperatorAuth auth = aservice.getOperatorAuth(headers);
        node.put("version", pm.getVersion());
        node.put("username", auth.getUsername());
        node.put("fullname", auth.getFullname());
        node.put("construct_permission", auth.isConstructPermission());
        node.put("condition_permission", auth.isConditionPermission());
        node.put("tracking_permission", auth.isTrackingPermission());
        
        Binding<String> binding = Application.injector.getExistingBinding(Key.get(String.class, Application.XSD_NAME));
        if (binding != null) {
            node.put("xsd_uri", "/doc/xsd");
        }
        
        binding = Application.injector.getExistingBinding(Key.get(String.class, Application.DOC_NAME));
        if (binding != null) {
            node.put("doc_uri", "/doc/doc");
        }
        
        return Response.ok(node.toString(), MediaType.APPLICATION_JSON).build();
    }
    
    @POST
    @Path("/load/json")
    public final Response loadJson(String data, @Context HttpHeaders headers, @Context UriInfo info) throws Throwable {
        boolean isTest = info.getQueryParameters().containsKey(TEST_PARAM_NAME);
        OperatorAuth auth = aservice.getOperatorAuth(headers);
        if (data.startsWith("[") && data.endsWith("]")) {
            return loader.loadJsonArray(data, LoadService.Extension.JSON, auth, isTest);
        } else {
            return loader.load(data, LoadService.Extension.JSON, auth, isTest);
        }
    }

    @POST
    @Path("/load/xml")
    public final Response loadXml(String data, @Context HttpHeaders headers, @Context UriInfo info) {
        boolean isTest = info.getQueryParameters().containsKey(TEST_PARAM_NAME);
        OperatorAuth auth = aservice.getOperatorAuth(headers);
        return loader.load(data, LoadService.Extension.XML, auth, isTest);
    }

    @POST
    @Path("/load/file")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public final Response loadZip(@FormDataParam("uploadFile") InputStream fileInputStream,
                                  @FormDataParam("uploadFile") FormDataContentDisposition fileFormDataContentDisposition,
                                  @Context HttpHeaders headers, @Context UriInfo info) {
        boolean isTest = info.getQueryParameters().containsKey(TEST_PARAM_NAME);
        OperatorAuth auth = aservice.getOperatorAuth(headers);
        String ext = FilenameUtils.getExtension(fileFormDataContentDisposition.getFileName());
        return loader.load(fileInputStream, LoadService.Extension.get(ext), auth, isTest);
    }

}