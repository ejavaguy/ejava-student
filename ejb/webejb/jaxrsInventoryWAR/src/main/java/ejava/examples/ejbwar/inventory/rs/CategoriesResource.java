package ejava.examples.ejbwar.inventory.rs;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.ejbwar.inventory.bo.Categories;
import ejava.examples.ejbwar.inventory.bo.Category;
import ejava.examples.ejbwar.inventory.ejb.InventoryMgmtEJB;

/**
 * This class implements a web facade for the product catageories in the 
 * inventory management. It uses JAX-RS to implement the server-side HTTP
 * communications.
 */
@Path("categories") //part of the method's final URI
public class CategoriesResource {
	private static final Log log = LogFactory.getLog(CategoriesResource.class);
	
	@Inject
	private InventoryMgmtEJB ejb;
	@Context
	private Request request;
	@Context 
	private UriInfo uriInfo;
	
	/**
	 * This method will respond to a GET of the base resource URI to 
	 * return categories based on query parameters.
	 * @param name
	 * @param offset
	 * @param limit
	 * @return
	 */
	@GET @Path("")
	@Produces(MediaType.APPLICATION_XML)
	public Response findCategoriesByName(
			@QueryParam("name") @DefaultValue("") String name,
			@QueryParam("offset") @DefaultValue("0") int offset,
			@QueryParam("limit") @DefaultValue("0") int limit) {
		log.debug(String.format("%s %s", request.getMethod(), uriInfo.getAbsolutePath()));

		try {
			Categories categories = ejb.findCategoryByName(name, offset, limit);
				//wrap java.lang.List in object that preserves type for marshalling
			return Response.ok(categories)
					.build();
		} catch (Exception ex) {
			return ResourceHelper.serverError(log, "get categories", ex).build();
		}
	}
	
	/**
	 * This method will respond to a GET method for (root)/{id} URIs to get 
	 * a specific category.
	 *  
	 * @param id
	 * @return
	 */
	@GET @Path("{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getCategory(@PathParam("id")int id) {
		log.debug(String.format("%s %s", request.getMethod(), uriInfo.getAbsolutePath()));

		try {
			Category category = ejb.getCategory(id);
			if (category != null) {
				return Response.ok(category)
						.tag("" + category.getVersion())
						.build();
			}
			else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity(String.format("unable to locate category %d", id))
						.type(MediaType.TEXT_PLAIN)
						.build();
			}
		} catch (Exception ex) {
			return ResourceHelper.serverError(log, "get category", ex).build();
		}
	}

	/**
	 * This method responds to DELETE method calls to (root)/{id} URIs to
	 * delete a specific category.
	 * @param id
	 * @return
	 */
	@DELETE @Path("{id}")
	public Response deleteCategory(@PathParam("id")int id) {
		log.debug(String.format("%s %s", request.getMethod(), uriInfo.getAbsolutePath()));

		try {
			ejb.deleteCategory(id);
			return Response.noContent().build();
		} catch (Exception ex) {
			return ResourceHelper.serverError(log, "delete category", ex).build();
		}
	}

}
