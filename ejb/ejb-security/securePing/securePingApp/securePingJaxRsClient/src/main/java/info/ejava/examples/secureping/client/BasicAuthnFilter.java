package info.ejava.examples.secureping.client;

import java.io.IOException;
import java.util.Base64;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicAuthnFilter implements ClientRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(BasicAuthnFilter.class);
    private final String username;
    private final String password;
    private final String authn;
    
    public BasicAuthnFilter(String username, String password) {
        this.username = username;
        this.password = password;
        String credentials = username + ":" + password;
        authn = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        headers.add("Authorization", authn);
        logger.debug("added BASIC credentials[{}] for {}, {}", authn, username, password);
    }
}
