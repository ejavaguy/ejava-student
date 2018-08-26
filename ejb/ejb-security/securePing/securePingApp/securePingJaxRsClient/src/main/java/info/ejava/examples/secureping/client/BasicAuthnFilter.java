package info.ejava.examples.secureping.client;

import java.io.IOException;
import java.util.Base64;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

public class BasicAuthnFilter implements ClientRequestFilter {
    private final String authn;
    
    public BasicAuthnFilter(String username, String password) {
        String credentials = username + ":" + password;
        authn = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        headers.add("Authorization", authn);
    }
}
