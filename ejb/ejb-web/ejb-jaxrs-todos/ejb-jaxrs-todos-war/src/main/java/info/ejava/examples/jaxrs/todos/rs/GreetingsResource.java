package info.ejava.examples.jaxrs.todos.rs;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import info.ejava.examples.jaxrs.todos.ejb.InternalErrorException;
import info.ejava.examples.jaxrs.todos.ejb.InvalidRequestException;

@Path("greetings")
public class GreetingsResource {
    @EJB
    private GreetingEJB greetingEJB;

    @GET
    @Path("hi")
    @Produces(MediaType.TEXT_PLAIN)
    public Response sayHi() {
        String entity = "hi";
        ResponseBuilder rb = Response.ok(entity);            
        return rb.build();
    }
    
    @GET
    @Path("greet")
    @Produces(MediaType.TEXT_PLAIN)
    public Response greet(@QueryParam("name") String name) {
        ResponseBuilder rb=null;
        try {
            String entity = greetingEJB.greet(name);
            rb = Response.ok(entity);
        } catch (InvalidRequestException ex) {
            rb = Response.status(Status.BAD_REQUEST)
                    .entity(ex.getMessage());
        } catch (InternalErrorException ex) {
            rb = Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(ex.getMessage());
        } catch (Exception ex) {
            rb=Response.serverError()
                .entity(String.format("unexpected error greeting name[%s]", name));
        }
        return rb.build();
    }
}
