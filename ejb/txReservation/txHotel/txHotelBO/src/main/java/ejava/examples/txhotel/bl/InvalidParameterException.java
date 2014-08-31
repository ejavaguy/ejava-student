package ejava.examples.txhotel.bl;

@SuppressWarnings("serial")
public class InvalidParameterException extends HotelReservationException {
    public InvalidParameterException() {}
    public InvalidParameterException(String msg) { super(msg); }
    public InvalidParameterException(String msg, Throwable ex) {super(msg, ex);}
    public InvalidParameterException(Throwable ex) { super(ex); }
}
