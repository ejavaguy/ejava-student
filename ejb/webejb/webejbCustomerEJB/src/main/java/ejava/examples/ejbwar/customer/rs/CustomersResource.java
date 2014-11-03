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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.ejbwar.customer.bo.Customer;
import ejava.examples.ejbwar.customer.bo.Customers;
import ejava.examples.ejbwar.customer.ejb.CustomerMgmtLocal;

/**
 * This class provides a JAX-RS resource for interfacing with customer
 * methods.
 */
@Path("customers")
public class CustomersResource {
	private static final Log log = LogFactory.getLog(CustomersResource.class);
	@Inject
	private CustomerMgmtLocal ejb;
	@Context
	private Request request;
	@Context
	private UriInfo uriInfo;
	
	@POST @Path("")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Response addCustomer(Customer customer) {
		log.debug(String.format("%s %s", request.getMethod(), uriInfo.getAbsolutePath()));
		try {
			Customer c = ejb.addCustomer(customer);
			URI uri = UriBuilder.fromUri(uriInfo.getAbsolutePath())
					.path(CustomersResource.class, "getCustomer")
					.build(c.getId());
			return Response.created(uri)
					.entity(c)
					.build();
		} catch (Exception ex) {
			return serverError(log, "creating person", ex).build();
		}
	}
	
	@GET @Path("")
	@Produces(MediaType.APPLICATION_XML)
	public Response findCustomersByName(
			@QueryParam("firstName") String firstName,
			@QueryParam("lastName") String lastName,
			@QueryParam("offset") @DefaultValue("0") int offset,
			@QueryParam("limit") @DefaultValue("0") int limit) {
		log.debug(String.format("%s %s", request.getMethod(), uriInfo.getAbsolutePath()));
		try {
			Customers customers = ejb.findCustomersByName(firstName, lastName, offset, limit);
			return Response.ok(customers)
					.build();
		} catch (Exception ex) {
			return serverError(log, "finding person", ex).build();
		}
	}
	
	@GET @Path("{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getCustomer(@PathParam("id") int id) {
		log.debug(String.format("%s %s", request.getMethod(), uriInfo.getAbsolutePath()));
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
			return serverError(log, "getting person", ex).build();
		}
	}

	@DELETE @Path("{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response deleteCustomer(@PathParam("id") int id) {
		log.debug(String.format("%s %s", request.getMethod(), uriInfo.getAbsolutePath()));
		try {
			ejb.deleteCustomer(id);
			return Response.ok()
					.build();
		} catch (Exception ex) {
			return serverError(log, "deleting person", ex).build();
		}
	}
	
	public static ResponseBuilder serverError(Log log, String context, Exception ex) {
		String message = String.format("unexpected error %s: %s",context, ex.getLocalizedMessage());
		log.warn(message, ex);
		return Response.serverError()
				.entity(message)
				.type(MediaType.TEXT_PLAIN);
	}
	
}
