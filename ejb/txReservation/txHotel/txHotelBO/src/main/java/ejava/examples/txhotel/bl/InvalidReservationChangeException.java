package ejava.examples.txhotel.bl;

@SuppressWarnings("serial")
public class InvalidReservationChangeException extends HotelReservationException {
    public InvalidReservationChangeException() {}
    public InvalidReservationChangeException(String msg) { super(msg); }
    public InvalidReservationChangeException(String msg, Throwable ex) {super(msg, ex);}
    public InvalidReservationChangeException(Throwable ex) { super(ex); }
}
