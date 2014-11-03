package ejava.examples.ejbwar.customer.client;

import java.net.URI;


import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import ejava.examples.ejbwar.customer.bo.Customer;
import ejava.examples.ejbwar.customer.bo.CustomerRepresentation;
import ejava.examples.ejbwar.customer.bo.Customers;
import ejava.examples.ejbwar.customer.rs.CustomersResource;

/**
 * This class implements an HTTP Client interface to the customer 
 * web application. All commands are through HTTP POST, GET, PUT, and DELETE
 * methods to specific resource URIs for products and categories.
 */
public class CustomerClientImpl implements CustomerClient {
	private static final Log log = LogFactory.getLog(CustomerClientImpl.class);
	private HttpClient client;
	/**
	 * Defines the HTTP URL for the WAR that hosts the JAX-RS resources.
	 */
	private URI appURI;

	public void setHttpClient(HttpClient client) {
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
	protected <T> UriBuilder buildURI(Class<T> resourceClass, String method) {
		//start with the URI for the WAR deployed to the server 
		//that ends with the context-root
		return UriBuilder.fromUri(appURI)
				//add path info from the 
				//javax.ws.rs.core.Application @ApplicationPath
				.path("rest")
				//add in @Path added by resource class
				.path(resourceClass)
				//add in @Path added by resource class' method
				.path(resourceClass,method);
				//the result will be a URI template that 
				//must be passed arguments by the caller during build()
	}

	@Override
	public Customer addCustomer(Customer customer) throws Exception {
		URI uri = buildURI(CustomersResource.class,"addCustomer")
				.build();
			
		//build overall request
		HttpPost post = new HttpPost(uri);
		post.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);
		post.setEntity(new StringEntity(customer.toString(), "UTF-8"));
		
		//issue request and look for OK with entity
		HttpResponse response = client.execute(post);
		log.info(String.format("%s %s", post.getURI(), response));
		if (Response.Status.CREATED.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return CustomerRepresentation.unmarshall(Customer.class,
					response.getEntity().getContent());
		}
		log.warn(EntityUtils.toString(response.getEntity()));
		return null;
	}

	@Override
	public Customers findCustomersByName(String firstName, String lastName, int offset, int limit) throws Exception {
		//build a URI to the specific method that is hosted within the app
		URI uri = buildURI(CustomersResource.class,"findCustomersByName")
				//marshall @QueryParams into URI
				.queryParam("firstName", firstName)
				.queryParam("lastName", lastName)
				.queryParam("offset", offset)
				.queryParam("limit", limit)
				.build();
		
		//build the overall request 
		HttpGet get = new HttpGet(uri);
		get.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
		
		//issue request and look for an OK response with entity
		HttpResponse response = client.execute(get);
		log.info(String.format("%s %s", get.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return CustomerRepresentation.unmarshall(Customers.class,
					response.getEntity().getContent());
		}
		log.warn(EntityUtils.toString(response.getEntity()));
		return null;
	}
	
	@Override
	public Customer getCustomer(int id) throws Exception {
		URI uri = buildURI(CustomersResource.class,"getCustomer")
				//marshall @PathParm into the URI
				.build(id);
		
		//build the overall request
		HttpGet get = new HttpGet(uri);

		//execute request and look for an OK response without an entity
		HttpResponse response = client.execute(get);
		log.info(String.format("%s %s", get.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return CustomerRepresentation.unmarshall(Customer.class,
					response.getEntity().getContent());
		}
		log.warn(EntityUtils.toString(response.getEntity()));
		return null;
	}

	@Override
	public boolean deleteCustomer(int id) throws Exception {
		URI uri = buildURI(CustomersResource.class,"deleteCustomer")
				//marshall @PathParm into the URI
				.build(id);
		
		//build the overall request
		HttpDelete delete = new HttpDelete(uri);

		//execute request and look for an OK response without an entity
		HttpResponse response = client.execute(delete);
		log.info(String.format("%s %s", delete.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			EntityUtils.consume(response.getEntity());
			return true;
		}
		log.warn(EntityUtils.toString(response.getEntity()));
		return false;
	}
}
