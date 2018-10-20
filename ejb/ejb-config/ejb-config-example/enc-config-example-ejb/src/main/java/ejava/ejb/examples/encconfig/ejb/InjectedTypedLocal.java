package ejava.ejb.examples.encconfig.ejb;

import javax.ejb.Local;

@Local
public interface InjectedTypedLocal extends InjectedTyped {
    //local interface method definitions go here
    void localMethod();
}
