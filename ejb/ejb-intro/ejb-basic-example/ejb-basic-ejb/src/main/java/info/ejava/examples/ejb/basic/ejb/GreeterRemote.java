package info.ejava.examples.ejb.basic.ejb;

import javax.ejb.Remote;

/**
 * This interface extends the Greeter business interface to provide a means for
 * remote clients and clients from different classloaders to communicate with
 * the session EJB.
 */
@Remote
public interface GreeterRemote extends Greeter {

}
