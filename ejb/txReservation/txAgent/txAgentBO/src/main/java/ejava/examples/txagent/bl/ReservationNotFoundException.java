package ejava.examples.txagent.bl;

@SuppressWarnings("serial")
public class ReservationNotFoundException extends AgentReservationException {
    public ReservationNotFoundException() {}
    public ReservationNotFoundException(String msg) { super(msg); }
    public ReservationNotFoundException(String msg, Throwable ex) {super(msg, ex);}
    public ReservationNotFoundException(Throwable ex) { super(ex); }
}
