package ejava.examples.txagent.ejb;

import javax.ejb.Local;

import ejava.examples.txagent.bl.AgentReservationSession;

@Local
public interface AgentReservationSessionLocal extends AgentReservationSession {
}
