package ejava.examples.txhotel.bl;

@SuppressWarnings("serial")
public class HotelReservationException extends Exception {
    public HotelReservationException() {}
    public HotelReservationException(String msg) { super(msg); }
    public HotelReservationException(String msg, Throwable ex) {super(msg, ex);}
    public HotelReservationException(Throwable ex) { super(ex); }
}
