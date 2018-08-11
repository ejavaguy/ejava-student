package ejava.examples.ejbwar.inventory.client;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbwar.inventory.bo.Categories;
import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.inventory.bo.Products;
import ejava.examples.ejbwar.inventory.rs.CategoriesResource;
import ejava.examples.ejbwar.inventory.rs.ProductsResource;
import ejava.examples.ejbwar.jaxrs.JAXBUtils;

/**
 * This class implements an HTTP Client interface to the inventory 
 * web application. All commands are through HTTP POST, GET, PUT, and DELETE
 * methods to specific resource URIs for products and categories.
 */
public class InventoryHttpClientImpl implements InventoryClient {
	private static final Logger logger = LoggerFactory.getLogger(InventoryHttpClientImpl.class);
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
	public Categories findCategoryByName(String name, int offset, int limit) {
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
		try {
        		HttpResponse response = client.execute(get);
        		logger.info("{} {}", get.getURI(), response);
        		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
                return JAXBUtils.unmarshall(response.getEntity().getContent(), Categories.class);
        		}
        		return null;
		}
        	catch (Exception ex) {
        	    throw new WebApplicationException(ex);
        	}
	}
	
	@Override
	public Category getCategory(int id) {
		URI uri = buildURI(CategoriesResource.class,"getCategory")
				//marshall @PathParm into the URI
				.build(id);
		
		//build the overall request
		HttpGet get = new HttpGet(uri);
		get.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
		
		//execute request and look for an OK response with entity
		try {
        		HttpResponse response = client.execute(get);
        		logger.info("{} {}", get.getURI(), response);
        		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
        			return JAXBUtils.unmarshall(response.getEntity().getContent(), Category.class);
        		}
        		return null;
        }
        catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
	}
	
	@Override
	public boolean deleteCategory(int id) {
		URI uri = buildURI(CategoriesResource.class,"deleteCategory")
				//marshall @PathParm into the URI
				.build(id);
		
		//build the overall request
		HttpDelete delete = new HttpDelete(uri);

		//execute request and look for an OK response without an entity
		try {
        		HttpResponse response = client.execute(delete);
        		logger.info("{} {}", delete.getURI(), response);
        		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
        			return true;
        		}
        		EntityUtils.consume(response.getEntity()); //must read returned data to release conn
        		return false;
        }
        catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
	}
	
	/**
	 * This method uses HTML FORM mechanism to POST a new product in the
	 * inventory. 
	 */
	@Override
	public Product createProduct(Product product, String categoryName) {
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
		try {
        		HttpPost post = new HttpPost(uri);
        		post.addHeader(HttpHeaders.CONTENT_ENCODING, MediaType.APPLICATION_FORM_URLENCODED);
        		post.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
        		post.setEntity(new UrlEncodedFormEntity(params));
        			
        		//issue the request and check the response
        		HttpResponse response = client.execute(post);
        		logger.info("{} {}", post.getURI(), response);
        		if (Response.Status.CREATED.getStatusCode() == response.getStatusLine().getStatusCode()) {
        			return JAXBUtils.unmarshall(response.getEntity().getContent(), Product.class);
        		}
        		return null;
        }
        catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
	}
	
	@Override
	public Products findProductsByName(String name, int offset, int limit) {
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
		try {
        		HttpResponse response = client.execute(get);
        		logger.info("{} {}", get.getURI(), response);
        		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
        			return JAXBUtils.unmarshall(response.getEntity().getContent(), Products.class);
        		}
        		return null;
        }
        catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
	}
	
	@Override
	public Product getProduct(int id) {
		URI uri = buildURI(ProductsResource.class,"getProduct")
				//marshall @PathParm into the URI
				.build(id);
			
		//build overall request
		HttpGet get = new HttpGet(uri);
		get.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
		
		//issue request and look for OK response with entity
		try {
        		HttpResponse response = client.execute(get);
        		logger.info("{} {}", get.getURI(), response);
        		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
        			return JAXBUtils.unmarshall(response.getEntity().getContent(), Product.class);
        		}
        		return null;
        }
        catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
	}
	
	@Override
	public Product updateProduct(Product product) {
		URI uri = buildURI(ProductsResource.class,"updateProduct")
				//marshall @PathParm into the URI
				.build(product.getId());
			
		//build overall request
		HttpPut put = new HttpPut(uri);
		put.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);

		try {
        		String payload = JAXBUtils.marshall(product);
        		put.setEntity(new StringEntity(payload, "UTF-8"));
        		
        		//issue request and look for OK with entity
        		HttpResponse response = client.execute(put);
        		logger.info("{} {}", put.getURI(), response);
        		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
        			return JAXBUtils.unmarshall(response.getEntity().getContent(), Product.class);
        		}
        		return null;
        }
        catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
	}

	@Override
	public boolean deleteProduct(int id) {
		URI uri = buildURI(ProductsResource.class,"deleteProduct")
				//marshall @PathParm into the URI
				.build(id);
			
		//build overall request
		HttpDelete delete = new HttpDelete(uri);
		
		//issue request and look for OK respose without and entity
		try {
        		HttpResponse response = client.execute(delete);
        		logger.info("{} {}", delete.getURI(), response);
        		if (Response.Status.OK.getStatusCode() == response.getStatusLine().getStatusCode()) {
        			return true;
        		}
        		EntityUtils.consume(response.getEntity()); //must read returned data to release conn
        		return false;
        }
        catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
	}
}
