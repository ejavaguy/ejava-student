package info.ejava.examples.ejb.ejbjpa.bl;

public class RoomUnavailableExcepton extends Exception {
    private static final long serialVersionUID = 1L;

    public RoomUnavailableExcepton(String message, Throwable cause) {
        super(message, cause);
    }

    public RoomUnavailableExcepton(String message) {
        super(message);
    }
}
