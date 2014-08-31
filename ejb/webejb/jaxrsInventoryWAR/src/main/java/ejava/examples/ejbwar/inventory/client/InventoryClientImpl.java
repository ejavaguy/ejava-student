package ejava.examples.ejbwar.inventory.client;

import java.net.URI;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import ejava.examples.ejbwar.inventory.bo.Categories;
import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.InventoryRepresentation;
import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.inventory.bo.Products;
import ejava.examples.ejbwar.inventory.rs.CategoriesResource;
import ejava.examples.ejbwar.inventory.rs.ProductsResource;

/**
 * This class implements an HTTP Client interface to the inventory 
 * web application. All commands are through HTTP POST, GET, PUT, and DELETE
 * methods to specific resource URIs for products and categories.
 */
public class InventoryClientImpl implements InventoryClient {
	private static final Log log = LogFactory.getLog(InventoryClientImpl.class);
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
	public Categories findCategoryByName(String name, int offset, int limit) throws Exception {
		//build a URI to the specific method that is hosted within the app
		URI uri = buildURI(CategoriesResource.class,"findCategoriesByName")
				//marshall @QueryParams into URI
				.queryParam("name", name)
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
			return InventoryRepresentation.unmarshall(Categories.class,
					response.getEntity().getContent());
		}
		return null;
	}
	
	@Override
	public Category getCategory(int id) throws Exception {
		URI uri = buildURI(CategoriesResource.class,"getCategory")
				//marshall @PathParm into the URI
				.build(id);
		
		//build the overall request
		HttpGet get = new HttpGet(uri);
		get.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
		
		//execute request and look for an OK response with entity
		HttpResponse response = client.execute(get);
		log.info(String.format("%s %s", get.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return InventoryRepresentation.unmarshall(Category.class,
					response.getEntity().getContent());
		}
		return null;
	}
	
	@Override
	public boolean deleteCategory(int id) throws Exception {
		URI uri = buildURI(CategoriesResource.class,"deleteCategory")
				//marshall @PathParm into the URI
				.build(id);
		
		//build the overall request
		HttpDelete delete = new HttpDelete(uri);

		//execute request and look for an OK response without an entity
		HttpResponse response = client.execute(delete);
		log.info(String.format("%s %s", delete.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return true;
		}
		EntityUtils.consume(response.getEntity()); //must read returned data to release conn
		return false;
	}
	
	/**
	 * This method uses HTML FORM mechanism to POST a new product in the
	 * inventory. 
	 */
	@Override
	public Product createProduct(Product product, String categoryName) 
		throws Exception {
		URI uri = buildURI(ProductsResource.class,"createProduct")
				//no @PathParams here
				.build();

		//build the form data with the request parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("name", product.getName()));
		params.add(new BasicNameValuePair("category", categoryName));
		if (product.getQuantity()!=null) {
			params.add(new BasicNameValuePair("quantity", product.getQuantity().toString()));
		}
		if (product.getPrice() != null) {
			params.add(new BasicNameValuePair("price", product.getPrice().toString()));
		}

		//create the request
		HttpPost post = new HttpPost(uri);
		post.addHeader(HttpHeaders.CONTENT_ENCODING, MediaType.APPLICATION_FORM_URLENCODED);
		post.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
		post.setEntity(new UrlEncodedFormEntity(params));
			
		//issue the request and check the response
		HttpResponse response = client.execute(post);
		log.info(String.format("%s %s", post.getURI(), response));
		if (Response.Status.CREATED.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return InventoryRepresentation.unmarshall(Product.class,response.getEntity().getContent());
		}
		return null;
	}
	
	@Override
	public Products findProductsByName(String name, int offset, int limit) throws Exception {
		URI uri = buildURI(ProductsResource.class,"findProductsByName")
				//marshall @QueryParams into URI
				.queryParam("name", name)
				.queryParam("offset", offset)
				.queryParam("limit", limit)
				.build();
			
		//build the overall request
		HttpGet get = new HttpGet(uri);
		get.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
		
		//issue request and look for OK response with entity
		HttpResponse response = client.execute(get);
		log.info(String.format("%s %s", get.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return InventoryRepresentation.unmarshall(Products.class,
					response.getEntity().getContent());
		}
		return null;
	}
	
	@Override
	public Product getProduct(int id) throws Exception {
		URI uri = buildURI(ProductsResource.class,"getProduct")
				//marshall @PathParm into the URI
				.build(id);
			
		//build overall request
		HttpGet get = new HttpGet(uri);
		get.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
		
		//issue request and look for OK response with entity
		HttpResponse response = client.execute(get);
		log.info(String.format("%s %s", get.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return InventoryRepresentation.unmarshall(Product.class,
					response.getEntity().getContent());
		}
		return null;
	}
	
	@Override
	public Product updateProduct(Product product) throws Exception {
		URI uri = buildURI(ProductsResource.class,"updateProduct")
				//marshall @PathParm into the URI
				.build(product.getId());
			
		//build overall request
		HttpPut put = new HttpPut(uri);
		put.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);
		put.setEntity(new StringEntity(product.toString(), "UTF-8"));
		
		//issue request and look for OK with entity
		HttpResponse response = client.execute(put);
		log.info(String.format("%s %s", put.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return InventoryRepresentation.unmarshall(Product.class,
					response.getEntity().getContent());
		}
		return null;
	}

	@Override
	public boolean deleteProduct(int id) throws Exception {
		URI uri = buildURI(ProductsResource.class,"deleteProduct")
				//marshall @PathParm into the URI
				.build(id);
			
		//build overall request
		HttpDelete delete = new HttpDelete(uri);
		
		//issue request and look for OK respose without and entity
		HttpResponse response = client.execute(delete);
		log.info(String.format("%s %s", delete.getURI(), response));
		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
			return true;
		}
		EntityUtils.consume(response.getEntity()); //must read returned data to release conn
		return false;
	}
}
