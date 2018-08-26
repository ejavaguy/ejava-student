package info.ejava.examples.secureping.client;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

public class AcceptFilter implements ClientRequestFilter {
    private final Object[] types;
    
    public AcceptFilter(Object...types) {
        this.types = types;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        headers.addAll("Accept", types);
    }
}
