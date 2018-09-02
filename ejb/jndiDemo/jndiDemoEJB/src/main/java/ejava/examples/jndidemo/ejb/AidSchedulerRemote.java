package ejava.examples.jndidemo.ejb;

import ejava.examples.jndidemo.Scheduler;

/**
 * This interface is part of an EJB example to leverage the XML deployment
 * descriptor as much as possible. There will be very few annotations within
 * this set of examples.
 *
 */

//@Remote declared by ejb-jar.xml entry
public interface AidSchedulerRemote extends Scheduler {
}
