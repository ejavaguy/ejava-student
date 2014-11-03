package ejava.examples.ejbwar.inventory.rs;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.logging.Log;

/**
 * This class provides common code build and return standard server responses
 */
public class ResourceHelper {
	public static ResponseBuilder serverError(Log log, String context, Exception ex) {
		String message = String.format("unexpected error %s: %s",context, ex.getLocalizedMessage());
		log.warn(message, ex);
		return Response.serverError()
				.entity(message)
				.type(MediaType.TEXT_PLAIN);
	}
}
