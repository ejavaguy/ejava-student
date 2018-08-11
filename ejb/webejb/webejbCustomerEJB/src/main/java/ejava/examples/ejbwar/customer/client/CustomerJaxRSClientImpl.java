package ejava.examples.ejbwar.customer.client;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.ResponseProcessingException;
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
	private static final Logger log = LoggerFactory.getLogger(CustomerJaxRSClientImpl.class);
	private Client client;
	/**
	 * Defines the HTTP URL for the WAR that hosts the JAX-RS resources.
	 */
	private URI appURI;

	public void setClient(Client client) {
		this.client = client;
	}
	public void setAppURI(URI appURI) {
		this.appURI = appURI;
	}	
	
	/**
	 * Helper method that returns a URIBuilder fully initialized to point
	 * to the URI that will reach the specified method within the inventory
	 * resource classes.
	 * @param resourceClass
	 * @param method
	 * @return
	 */
	protected <T> UriBuilder buildURI(String path) {
		//start with the URI for the WAR deployed to the server 
		//that ends with the context-root
		return UriBuilder.fromUri(appURI)
				//add path info from the 
				//javax.ws.rs.core.Application @ApplicationPath
				.path("rest")
				//add in @Path added by resource class and method
				.path(path);
	}

	@Override
	public Customer addCustomer(Customer customer) {
		URI uri = buildURI("customers")
				.build();
			
		//build overall request
		Invocation request = client.target(uri)
		        .request(MediaType.APPLICATION_XML_TYPE)
		        .buildPost(Entity.entity(customer, MediaType.APPLICATION_XML_TYPE));
		
		//issue request and look for OK with entity
		try (Response response=request.invoke()) {
	        log.info("POST {} returned {}", uri, response.getStatusInfo());
	        if (Status.Family.SUCCESSFUL==response.getStatusInfo().getFamily()) {
	            return response.readEntity(Customer.class);
	        } else {
	            String payload = response.hasEntity() ? response.readEntity(String.class) : "";
	            log.warn(payload);
	            throw new ResponseProcessingException(response, payload);
	        }
		}
	}

	@Override
	public Customers findCustomersByName(String firstName, String lastName, int offset, int limit) {
		//build a URI to the specific method that is hosted within the app
		URI uri = buildURI("customers")
				//marshall @QueryParams into URI
				.queryParam("firstName", firstName)
				.queryParam("lastName", lastName)
				.queryParam("offset", offset)
				.queryParam("limit", limit)
				.build();
		
		//build the overall request 
		Invocation request = client.target(uri)
		        .request(MediaType.APPLICATION_XML)
		        .buildGet();
		
		//issue request and look for an OK response with entity
		try (Response response = request.invoke()) {
            log.info("GET {} returned {}", uri, response.getStatusInfo());
            if (Status.Family.SUCCESSFUL==response.getStatusInfo().getFamily()) {
                return response.readEntity(Customers.class);
            } else {
                String payload = response.hasEntity() ? response.readEntity(String.class) : "";
                log.warn(payload);
                throw new ResponseProcessingException(response, payload);
            }		    
		}
	}
	
	@Override
	public Customer getCustomer(int id) {
		URI uri = buildURI("customers/{id}")
				//marshall @PathParm into the URI
				.build(id);
		
		//build the overall request
		Invocation request = client.target(uri)
		        .request(MediaType.APPLICATION_XML_TYPE)
		        .buildGet();

		//execute request and look for an OK response without an entity
		try (Response response = request.invoke()) {
		    log.info("GET {} returned {}", uri, response.getStatusInfo());
            if (Status.Family.SUCCESSFUL==response.getStatusInfo().getFamily()) {
                return response.readEntity(Customer.class);
            } else {
                String payload = response.hasEntity() ? response.readEntity(String.class) : "";
                log.warn(payload);
                throw new ResponseProcessingException(response, payload);
            }           
		}
	}

	@Override
	public boolean deleteCustomer(int id) {
		URI uri = buildURI("customers/{id}")
				//marshall @PathParm into the URI
				.build(id);
		
		//build the overall request
		Invocation request = client.target(uri)
		        .request()
		        .buildDelete();

		//execute request and look for an OK response without an entity
		try (Response response=request.invoke()) {
            log.info("GET {} returned {}", uri, response.getStatusInfo());
            if (Status.Family.SUCCESSFUL==response.getStatusInfo().getFamily()) {
                return true;
            } else {
                String payload = response.hasEntity() ? response.readEntity(String.class) : "";
                log.warn(payload);
                throw new ResponseProcessingException(response, payload);
            }           		    
		}
	}
}
