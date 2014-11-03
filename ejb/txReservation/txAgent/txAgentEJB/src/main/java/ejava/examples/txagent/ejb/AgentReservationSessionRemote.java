package ejava.examples.txagent.ejb;

import javax.ejb.Remote;

import ejava.examples.txagent.bl.AgentReservationSession;

@Remote
public interface AgentReservationSessionRemote extends AgentReservationSession {
}
