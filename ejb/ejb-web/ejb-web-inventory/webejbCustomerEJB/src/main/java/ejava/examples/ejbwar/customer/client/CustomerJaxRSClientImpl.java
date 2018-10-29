package ejava.examples.ejbwar.customer.client;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbwar.customer.bo.Customer;
import ejava.examples.ejbwar.customer.bo.Customers;

/**
 * This class implements a JAX-RS Client interface to the customer 
 * web application. All commands are through HTTP POST, GET, PUT, and DELETE
 * methods to specific resource URIs for customers.
 */
public class CustomerJaxRSClientImpl implements CustomerClient {
	private static final Logger logger = LoggerFactory.getLogger(CustomerJaxRSClientImpl.class);
    public static final String CUSTOMERS_PATH = "customers";
    public static final String CUSTOMER_PATH = "customers/{id}";
	private Client client;
	/**
	 * Defines the HTTP URL for the WAR that hosts the JAX-RS resources.
	 */
	private URI baseUrl;
    
    /**
     * Defines the protocol between the client and server.
     */
    private MediaType mediaType=MediaType.APPLICATION_XML_TYPE;

	public void setClient(Client client) {
		this.client = client;
	}
    public void setBaseUrl(URI baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public void setMediaType(String mediaType) {
        this.mediaType = MediaType.valueOf(mediaType);
    }
	

    /**
     * Helper class to build the base URI for a client call.
     * @param resourcePath
     * @return uri builder ready to accept path parameter values
     */
    private UriBuilder getBaseUri(String...resourcePath) {
        UriBuilder b = UriBuilder.fromUri(baseUrl).path("api");
        if (resourcePath!=null) {
            for (String p: resourcePath) {
                b = b.path(p);
            }
        }
        return b;
    }

	@Override
	public Customer addCustomer(Customer customer) {
		URI uri = getBaseUri(CUSTOMERS_PATH)
				.build();
			
		//build overall request
		Invocation request = client.target(uri)
		        .request(mediaType)
		        .buildPost(Entity.entity(customer, mediaType, customer.getClass().getAnnotations()));
		
		//issue request and look for OK with entity
		try (Response response=request.invoke()) {
            logger.debug("POST {}, {} returned {}", uri, mediaType, response.getStatusInfo());
	        if (Status.Family.SUCCESSFUL==response.getStatusInfo().getFamily()) {
	            return response.readEntity(Customer.class);
	        } else {
	            String payload = response.hasEntity() ? response.readEntity(String.class) 
	                    : response.getStatusInfo().toString();
	            logger.warn(payload);
	            throw new ResponseProcessingException(response, payload);
	        }
		}
	}

	@Override
	public Customers findCustomersByName(String firstName, String lastName, int offset, int limit) {
		//build a URI to the specific method that is hosted within the app
		URI uri = getBaseUri(CUSTOMERS_PATH)
				.build();
		
		//build the overall request 
		Invocation request = client.target(uri)
	              //marshall @QueryParams into URI
                .queryParam("firstName", firstName)
                .queryParam("lastName", lastName)
                .queryParam("offset", offset)
                .queryParam("limit", limit)
		        .request(mediaType)
		        .buildGet();
		
		//issue request and look for an OK response with entity
		try (Response response = request.invoke()) {
            logger.debug("GET {}, {} returned {}", uri, mediaType, response.getStatusInfo());
            if (Status.Family.SUCCESSFUL==response.getStatusInfo().getFamily()) {
                return response.readEntity(Customers.class);
            } else {
                String payload = response.hasEntity() ? response.readEntity(String.class) 
                        : response.getStatusInfo().toString();
                logger.warn(payload);
                throw new ResponseProcessingException(response, payload);
            }		    
		}
	}
	
	@Override
	public Customer getCustomer(int id) {
		URI uri = getBaseUri(CUSTOMER_PATH)
				//marshall @PathParm into the URI
				.build(id);
		
		//build the overall request
		Invocation request = client.target(uri)
		        .request(mediaType)
		        .buildGet();

		//execute request and look for an OK response without an entity
		try (Response response = request.invoke()) {
            logger.debug("GET {}, {} returned {}", uri, mediaType, response.getStatusInfo());
            if (Status.Family.SUCCESSFUL==response.getStatusInfo().getFamily()) {
                return response.readEntity(Customer.class);
            } else {
                String payload = response.hasEntity() ? response.readEntity(String.class) 
                        : response.getStatusInfo().toString();
                logger.warn(payload);
                throw new ResponseProcessingException(response, payload);
            }           
		}
	}

	@Override
	public boolean deleteCustomer(int id) {
		URI uri = getBaseUri(CUSTOMER_PATH)
				//marshall @PathParm into the URI
				.build(id);
		
		//build the overall request
		Invocation request = client.target(uri)
		        .request()
		        .buildDelete();

		//execute request and look for an OK response without an entity
		try (Response response=request.invoke()) {
            logger.debug("GET {}, {} returned {}", uri, mediaType, response.getStatusInfo());
            if (Status.Family.SUCCESSFUL==response.getStatusInfo().getFamily()) {
                return true;
            } else {
                String payload = response.hasEntity() ? response.readEntity(String.class) 
                        : response.getStatusInfo().toString();
                logger.warn(payload);
                throw new ResponseProcessingException(response, payload);
            }           		    
		}
	}
}
