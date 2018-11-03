package info.ejava.examples.jaxrs.todos.rs;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("greetings")
public class GreetingsResource {

    @GET
    @Path("hi")
    @Produces(MediaType.TEXT_PLAIN)
    public Response sayHi() {
        String entity = "hi";
        ResponseBuilder rb = Response.ok(entity);            
        return rb.build();
    }
    
    @POST
    @Path("greet")
    public Response greet(@QueryParam("name") String name) {
        ResponseBuilder rb=null;
        try {
            String greeting = String.format("hello %s", name);
            rb = Response.ok(greeting);            
        } catch (Exception ex) {
            Response.serverError()
                .entity(String.format("unexpected error greeting name[%s]", name));
        }
        return rb.build();
    }
}
