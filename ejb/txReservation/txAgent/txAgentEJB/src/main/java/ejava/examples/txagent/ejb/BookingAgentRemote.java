package ejava.examples.txagent.ejb;

import javax.ejb.Remote;

import ejava.examples.txagent.bl.BookingAgent;

@Remote
public interface BookingAgentRemote extends BookingAgent {

}
