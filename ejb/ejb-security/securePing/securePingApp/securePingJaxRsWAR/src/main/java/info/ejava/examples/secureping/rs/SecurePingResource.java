package info.ejava.examples.secureping.rs;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.ejava.examples.secureping.ejb.SecurePing;
import info.ejava.examples.secureping.ejb.SecurePingLocal;

@Path("ping")
@PermitAll
public class SecurePingResource {
    private static final Logger logger = LoggerFactory.getLogger(SecurePingResource.class);
    
    //@EJB(lookup="ejb:securePingEAR/securePingEJB/SecurePingEJB!info.ejava.examples.secureping.ejb.SecurePingRemote")
    @EJB(beanName="SecurePingEJB", beanInterface=SecurePingLocal.class)
    private SecurePing secureService;
    
    @Context
    private SecurityContext ctx;
    @Context
    private UriInfo uriInfo;
    
    @Path("admin")
    //@RolesAllowed("admin")
    public Pinger admin() {
        return new Pinger();
    }

    @Path("user")
    //@RolesAllowed("user")
    public Pinger user() {
        return new Pinger();
    }

    @Path("")
    public Pinger anonymous() {
        return new Pinger();
    }
    
    public class Pinger {
        @Path("pingAdmin")
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Response pingAdmin() {
            ResponseBuilder rb = null;
            try {
                String ejbResponse = secureService!=null ? secureService.pingAdmin() : null;
                PingResult entity = makeResourcePayload(ejbResponse);
                rb = Response.ok(entity);
            } catch (EJBAccessException ex) {
                PingResult entity = makeResourcePayload(ex.toString());
                rb = Response.serverError()
                             .entity(entity);                
            } catch (Exception ex) {
                rb=makeExceptionResponse(ex);
            }
            
            return rb.build();
        }

        @Path("pingUser")
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Response pingUser() {
            ResponseBuilder rb = null;
            try {
                String ejbResponse = secureService!=null ? secureService.pingUser() : null;
                PingResult entity = makeResourcePayload(ejbResponse);
                rb = Response.ok(entity);
            } catch (EJBAccessException ex) {
                PingResult entity = makeResourcePayload(ex.toString());
                rb = Response.serverError()
                             .entity(entity);                
            } catch (Exception ex) {
                rb=makeExceptionResponse(ex);
            }
            
            return rb.build();
        }

        @Path("pingAll")
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Response pingAll() {
            ResponseBuilder rb = null;
            try {
                String ejbResponse = secureService!=null ? secureService.pingAll() : null;
                PingResult entity = makeResourcePayload(ejbResponse);
                rb = Response.ok(entity);
            } catch (Exception ex) {
                rb=makeExceptionResponse(ex);
            }
            
            return rb.build();
        }
    }
    
    private ResponseBuilder makeExceptionResponse(Exception ex) {
        return Response.serverError()
                .entity(String.format("unexpected error calling secureService: %s",  ex.toString()));
    }
    
    private PingResult makeResourcePayload(String ejbResponse) {
        String context = uriInfo.getAbsolutePath().toString();
        String userName = ctx.getUserPrincipal()==null ? null : ctx.getUserPrincipal().getName();
        boolean isAdmin = ctx.isUserInRole("admin");
        boolean isUser = ctx.isUserInRole("user");
        
        PingResult result = new PingResult(context, userName, isAdmin, isUser);
        result.setServiceResult(ejbResponse);
//
//        StringBuilder text = new StringBuilder(uriInfo.getAbsolutePath().toString());
//        text.append(" called by ").append(userName);
//        text.append(", who isAdmin=").append(isAdmin);
//        text.append(", who isUser=").append(isUser);
//        text.append(" - recieved from EJB: ").append(ejbResponse);
        logger.debug("{}", result);
        return result;
    }

}
