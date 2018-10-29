package ejava.examples.ejbwar.customer.rs;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.ejbwar.customer.bo.Customer;
import ejava.examples.ejbwar.customer.bo.Customers;
import ejava.examples.ejbwar.customer.ejb.CustomerMgmtLocal;

/**
 * This class provides a JAX-RS resource for interfacing with customer
 * methods.
 */
@Path("customers")
public class CustomersResource {
	private static final Logger logger = LoggerFactory.getLogger(CustomersResource.class);
	@Inject
	private CustomerMgmtLocal ejb;
	@Context
	private Request request;
	@Context
	private UriInfo uriInfo;
	
	@POST @Path("")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response addCustomer(Customer customer) {
		logger.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
		try {
			Customer c = ejb.addCustomer(customer);
			URI uri = UriBuilder.fromUri(uriInfo.getAbsolutePath())
					.path(CustomersResource.class, "getCustomer")
					.build(c.getId());
			return Response.created(uri)
					.entity(c)
					.build();
		} catch (Exception ex) {
			return serverError(logger, "creating person", ex).build();
		}
	}
	
	@GET @Path("")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response findCustomersByName(
			@QueryParam("firstName") String firstName,
			@QueryParam("lastName") String lastName,
			@QueryParam("offset") @DefaultValue("0") int offset,
			@QueryParam("limit") @DefaultValue("0") int limit) {
		logger.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
		try {
			Customers customers = ejb.findCustomersByName(firstName, lastName, offset, limit);
			return Response.ok(customers)
					.build();
		} catch (Exception ex) {
			return serverError(logger, "finding person", ex).build();
		}
	}
	
	@GET @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getCustomer(@PathParam("id") int id) {
		logger.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
		try {
			Customer customer = ejb.getCustomer(id);
			if (customer!=null) {
				return Response.ok(customer)
						.build();
			} else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity(String.format("person %d not found", id))
						.type(MediaType.TEXT_PLAIN)
						.build();
			}
		} catch (Exception ex) {
			return serverError(logger, "getting person", ex).build();
		}
	}

	@DELETE @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response deleteCustomer(@PathParam("id") int id) {
		logger.debug("{} {}", request.getMethod(), uriInfo.getAbsolutePath());
		try {
			ejb.deleteCustomer(id);
			return Response.ok()
					.build();
		} catch (Exception ex) {
			return serverError(logger, "deleting person", ex).build();
		}
	}
	
	public static ResponseBuilder serverError(Logger log, String context, Exception ex) {
		String message = String.format("unexpected error %s: %s",context, ex.getLocalizedMessage());
		log.warn(message, ex);
		return Response.serverError()
				.entity(message)
				.type(MediaType.TEXT_PLAIN);
	}
	
}
