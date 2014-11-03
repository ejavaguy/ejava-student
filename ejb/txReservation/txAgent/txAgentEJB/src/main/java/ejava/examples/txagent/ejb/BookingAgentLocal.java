package ejava.examples.txagent.ejb;

import javax.ejb.Local;

import ejava.examples.txagent.bl.BookingAgent;

@Local
public interface BookingAgentLocal extends BookingAgent {
}
