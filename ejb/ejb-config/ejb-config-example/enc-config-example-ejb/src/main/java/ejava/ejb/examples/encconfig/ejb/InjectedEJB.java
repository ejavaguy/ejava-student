package ejava.ejb.examples.encconfig.ejb;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * This EJB will get injected into another session EJB.
 */
@Stateless
@LocalBean
public class InjectedEJB {
}
