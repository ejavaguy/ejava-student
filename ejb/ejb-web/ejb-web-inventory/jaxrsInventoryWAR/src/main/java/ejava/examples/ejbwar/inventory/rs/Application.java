package ejava.examples.ejbwar.inventory.rs;

import javax.ws.rs.ApplicationPath;

/**
 * This class triggers JAX-RS behavior in the server and registers
 * all JAX-RS classes below the @ApplicationPath listed here
 */
@ApplicationPath("api")
public class Application extends javax.ws.rs.core.Application {
}
