package ejava.examples.txagent.bl;

@SuppressWarnings("serial")
public class InvalidReservationChangeException extends AgentReservationException {
    public InvalidReservationChangeException() {}
    public InvalidReservationChangeException(String msg) { super(msg); }
    public InvalidReservationChangeException(String msg, Throwable ex) {super(msg, ex);}
    public InvalidReservationChangeException(Throwable ex) { super(ex); }
}
