package ejava.examples.txhotel.bl;

@SuppressWarnings("serial")
public class ReservationNotFoundException extends HotelReservationException {
    public ReservationNotFoundException() {}
    public ReservationNotFoundException(String msg) { super(msg); }
    public ReservationNotFoundException(String msg, Throwable ex) {super(msg, ex);}
    public ReservationNotFoundException(Throwable ex) { super(ex); }
}
