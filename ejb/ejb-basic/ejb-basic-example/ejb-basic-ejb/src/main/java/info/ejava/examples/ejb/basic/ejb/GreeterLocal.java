package info.ejava.examples.ejb.basic.ejb;

import javax.ejb.Local;

/**
 * This class extends the business interface to define a local EJB interface
 * that can be used to inject a local reference to the session EJB into 
 * other components.
 */
@Local
public interface GreeterLocal extends Greeter {

}
