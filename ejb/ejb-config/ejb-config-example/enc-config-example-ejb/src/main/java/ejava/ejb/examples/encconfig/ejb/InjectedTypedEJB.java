package ejava.ejb.examples.encconfig.ejb;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

/**
 * This EJB will get injected into another session EJB.
 */
@Stateless
@Remote(InjectedTypedRemote.class)
@Local(InjectedTypedLocal.class)
public class InjectedTypedEJB implements InjectedTypedLocal, InjectedTypedRemote {

    @Override
    public void commonMethod() {
    }

    @Override
    public void remoteMethod() {
    }

    @Override
    public void localMethod() {
    }
}
