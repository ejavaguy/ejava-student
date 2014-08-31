package ejava.examples.txagent.bl;

@SuppressWarnings("serial")
public class AgentReservationException extends Exception {
    public AgentReservationException() {}
    public AgentReservationException(String msg) { super(msg); }
    public AgentReservationException(String msg, Throwable ex) {super(msg, ex);}
    public AgentReservationException(Throwable ex) { super(ex); }
}
