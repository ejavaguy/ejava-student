package info.ejava.examples.secureping.rs;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.ejava.examples.secureping.dto.PingResult;
import info.ejava.examples.secureping.ejb.SecurePing;
import info.ejava.examples.secureping.ejb.SecurePingLocal;

/**
 * This JAX-RS resource class acts as a HTTP facade for calls to the EJB tier. Declarative encryption 
 * and authentication requirements are specified by the web.xml. This class primarily implements the structural
 * URIs and proxies the user calls/responses to/from the EJBs. Note we could have made this a stateless session
 * EJB and added @RolesAllowed to be enfored here. A pure JAX-RS resource class does not honor those declarative
 * annotations.    
 */
@Path("ping")
@PermitAll
public class SecurePingResource {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(SecurePingResource.class);
    
    //this injection requires CDI, which requires a WEB-INF/beans.xml file be in place to activate
    //@EJB(lookup="ejb:securePingEAR/securePingEJB/SecurePingEJB!info.ejava.examples.secureping.ejb.SecurePingRemote")
    @EJB(beanName="SecurePingEJB", beanInterface=SecurePingLocal.class)
    private SecurePing secureService;
    
    @Context
    private SecurityContext ctx;
    @Context
    private UriInfo uriInfo;
    
    @Path("whoAmI")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response whoAmI() {
        ResponseBuilder rb = null;
        try {
            if (secureService!=null) {
                rb = Response.ok(secureService.whoAmI());
            } else {
                rb = Response.serverError().entity("no ejb injected!!!"); 
            }
        } catch (Exception ex) {
            rb=makeExceptionResponse(ex);
        }
        
        return rb.build();
    }
    
    @Path("roles/{role}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response isCallerInRole(@PathParam("role") String role) {
        ResponseBuilder rb = null;
        try {
            if (secureService!=null) {
                rb = Response.ok(secureService.isCallerInRole(role));
            } else {
                rb = Response.serverError().entity("no ejb injected!!!"); 
            }
        } catch (Exception ex) {
            rb=makeExceptionResponse(ex);
        }
        
        return rb.build();        
    }
    
    /**
     * This method will return a sub-resource that is the same as the "secure" path except this path
     * is configured to not require any identity or encryption.
     * @return
     */
    @Path("unsecured")
    public Pinger anonymous() {
        return new Pinger();
    }

    /**
     * This method returns a sub-resource and will require that sub-resource to be envoked with 
     * HTTPS and an authenticated user in either the "admin" or "user" role base on the web.xml.
     * @return
     */
    @Path("secured")
    public Pinger authenticated() {
        return new Pinger();
    }
    
    public class Pinger {
        @Path("pingAdmin")
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response pingAdmin() {
            ResponseBuilder rb = null;
            try {
                PingResult result = makeResourcePayload(secureService!=null ?
                        secureService.pingAdmin() : "no ejb injected!!!");
                rb = secureService!=null ? 
                        Response.ok(result) :
                        Response.serverError().entity(result);
            } catch (EJBAccessException ex) {
                PingResult entity = makeResourcePayload(ex.toString());
                rb = Response.status(Status.FORBIDDEN).entity(entity);                
            } catch (Exception ex) {
                rb=makeExceptionResponse(ex);
            }
            
            return rb.build();
        }

        @Path("pingUser")
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response pingUser() {
            ResponseBuilder rb = null;
            try {
                PingResult result = makeResourcePayload(secureService!=null ?
                        secureService.pingUser() : "no ejb injected!!!");
                rb = secureService!=null ? 
                        Response.ok(result) :
                        Response.serverError().entity(result);
            } catch (EJBAccessException ex) {
                PingResult entity = makeResourcePayload(ex.toString());
                rb = Response.status(Status.FORBIDDEN).entity(entity);                
            } catch (Exception ex) {
                rb=makeExceptionResponse(ex);
            }
            
            return rb.build();
        }

        @Path("pingAll")
        @GET
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
        public Response pingAll() {
            ResponseBuilder rb = null;
            try {
                PingResult result = makeResourcePayload(secureService!=null ?
                        secureService.pingAll() : "no ejb injected!!!");
                rb = secureService!=null ? 
                        Response.ok(result) :
                        Response.serverError().entity(result);
            } catch (Exception ex) {
                //everyone should be able to call this -- why are we failing?
                rb=makeExceptionResponse(ex);
            }
            
            return rb.build();
        }
    }
    
    /**
     * This method is used to report an unexpected error condition in the endpoints.
     * @param ex
     * @return error message
     */
    private ResponseBuilder makeExceptionResponse(Exception ex) {
        String user = ctx.getUserPrincipal()==null ? null : ctx.getUserPrincipal().getName();
        return Response.serverError()
                .entity(String.format("unexpected error for user[%s] calling secureService: %s",  user, ex.toString()));
    }
    
    /**
     * This helper method builds a DTO to be marshaled back to the caller.
     * @param ejbResponse
     * @return dto filled in
     */
    private PingResult makeResourcePayload(String ejbResponse) {
        String context = uriInfo.getAbsolutePath().toString();
        String userName = ctx.getUserPrincipal()==null ? null : ctx.getUserPrincipal().getName();
        boolean isAdmin = ctx.isUserInRole("admin");
        boolean isUser = ctx.isUserInRole("user");
        
        PingResult result = new PingResult(context, userName, isAdmin, isUser);
        result.setServiceResult(ejbResponse);
        return result;
    }

}
