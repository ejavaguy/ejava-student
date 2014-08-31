package ejava.examples.jndidemo.ejb;

import javax.ejb.Local;

import ejava.examples.jndidemo.Scheduler;

@Local
public interface CookLocal extends Scheduler {

}
