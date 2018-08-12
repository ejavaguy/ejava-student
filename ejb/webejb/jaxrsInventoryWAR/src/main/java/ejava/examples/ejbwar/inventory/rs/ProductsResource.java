package ejava.examples.ejbwar.inventory.rs;

import java.net.URI;

import javax.inject.Inject;
import javax.json.bind.annotation.JsonbAnnotation;
import javax.json.bind.annotation.JsonbProperty;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbwar.inventory.bo.Product;
import ejava.examples.ejbwar.inventory.bo.Products;
import ejava.examples.ejbwar.inventory.ejb.InventoryMgmtEJB;

/**
 * This class implements the JAX-RS interface for the injected inventory 
 * management EJB logic.
 */
@Path("/products")
public class ProductsResource {
	private static final Logger logger = LoggerFactory.getLogger(ProductsResource.class);
	@Inject
	private InventoryMgmtEJB ejb;
	@Context
	private Request request;
	@Context 
	private UriInfo uriInfo;

	/**
	 * Creates a product
	 * @param product
	 * @param category
	 * @return
	 */
	@POST @Path("")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@JsonbAnnotation  //helps trigger JSON-B response marshaling when class also JAXB
	public Response createProduct(
			@FormParam("name") String name,
			@FormParam("quantity") Integer quantity,
			@FormParam("price") Double price,
			@FormParam("category") String category) {
		logger.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());

		try {
			Product product = new Product(name, quantity, price);
			Product p = ejb.addProduct(product, category);
            logger.info("returning created pojo product={}", p);
			//build URI that can be used to get this product
			URI uri = UriBuilder.fromUri(uriInfo.getAbsolutePath())
						.path(ProductsResource.class, "getProduct")
						.build(p.getId());
			return Response.created(uri)
					.entity(p)
  				    .tag("" + p.getVersion())
				    .build();
		} catch (Exception ex) {
			return ResourceHelper.serverError(logger, "creating product", ex).build();
		}
	}
	
	/**
	 * Returns a specific product
	 * @param id
	 * @return
	 */
	@GET @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@JsonbAnnotation  //helps trigger JSON-B response marshaling when class also JAXB
	public Response getProduct(@PathParam("id")int id) {
		logger.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());

		try {
			Product product = ejb.getProduct(id);
			if (product != null) {
				return Response.ok(product)
						.tag("" + product.getVersion())
						.build();
			}
			else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity(String.format("unable to locate product %d", id))
						.type(MediaType.TEXT_PLAIN)
						.build();
			}
		} catch (Exception ex) {
			return ResourceHelper.serverError(logger, "getting product", ex).build();
		}
	}

	/**
	 * Updates a product with the values of the object passed in
	 * @param id
	 * @param product
	 * @return
	 */
	@PUT @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@JsonbAnnotation  //helps trigger JSON-B response marshaling when class also JAXB
	public Response updateProduct(@PathParam("id") int id, 
	        @JsonbProperty Product product  //annotation helps trigger JSON-B request demarshaling when class also JAXB
	     ) {
		logger.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());

		try {
			Product p = ejb.updateProduct(product);
			logger.info("updated pojo product={}", p);
			return Response.ok(p)
					.tag("" + p.getVersion())
					.build();
		} catch (Exception ex) {
			return ResourceHelper.serverError(logger, "update product", ex).build();
		}
	}

	/* for debugging
	@PUT @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updateProductJSON(@PathParam("id") int id, String payload) {
        logger.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());

        try {
            Product product = JSONUtils.unmarshal(payload, Product.class);
            Product p = ejb.updateProduct(product);
            String result = JSONUtils.marshal(p);
            return Response.ok(result)
                    .tag("" + p.getVersion())
                    .build();
        } catch (Exception ex) {
            return ResourceHelper.serverError(logger, "update product", ex).build();
        }
    }
    */

	/**
	 * Deletes the product identified.
	 * @param id
	 * @return
	 */
	@DELETE @Path("{id}")
	public Response deleteProduct(@PathParam("id")int id) {
		logger.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());

		try {
			Product product = ejb.getProduct(id);
			if (product != null) {
				try {
					ejb.deleteProduct(product);
					return Response.noContent().build();
				} catch (Exception ex) {
					return ResourceHelper.serverError(logger, "deleting product", ex).build();
				}
			}
			else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity(String.format("unable to locate product %d", id))
						.type(MediaType.TEXT_PLAIN)
						.build();
			}
		} catch (Exception ex) {
			return ResourceHelper.serverError(logger, "getting product", ex).build();
		}
	}

	/**
	 * Returns a simple list of products that match provided name
	 * @param name
	 * @return
	 */
	@GET @Path("")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@JsonbAnnotation  //helps trigger JSON-B response marshaling when class also JAXB
	public Response findProductsByName(
			@QueryParam("name")String name,
			@QueryParam("offset") @DefaultValue("0") int offset,
			@QueryParam("limit") @DefaultValue("0") int limit) {
		logger.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());

		try {
			Products products = ejb.findProductByName(name, offset, limit);
			return Response.ok(products)
					.build();
		} catch (Exception ex) {
			return ResourceHelper.serverError(logger, "getting products", ex).build();
		}
	}	
}
