package info.ejava.examples.secureping.client;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

public class SecurePingJaxRsClient {
    private Client client;
    private URI baseUrl; //https://localhost:8443/securePingApi/api/;
    private MediaType[] acceptTypes;
    
    public SecurePingJaxRsClient(Client client, URI baseUrl) {
        this.client = client;
        this.baseUrl = baseUrl;
    }

    public Response whoAmI() {
        URI uri = getURI(null, "whoAmI");
        return client.target(uri).request().get();
    }
    public Response isCallerInRole(String role) {
        URI uri = getURI(null, "roles/" + role);
        return client.target(uri).request().get();
    }
    
    public Response ping(String context, String targetResource) {
        URI uri = getURI(context, targetResource);
        return client.target(uri)
              .request()
              .buildGet()
              .invoke();
    }

    public Response pingAdmin(String context) {
        return ping(context, "pingAdmin");
    }
    public Response pingUser(String context) {
        return ping(context, "pingUser");
    }
    public Response pingAll(String context) {
        return ping(context, "pingAll");
    }

    private URI getURI(String context, String targetResource) {
        UriBuilder b = UriBuilder.fromUri(baseUrl).path("ping");
        if (context!=null) {
           b.path(context);
        }
        b.path(targetResource);
        return b.build();
    }

}
