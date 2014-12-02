package org.myorg.jpatickets.bl;

public class UnavailableException extends Exception {
    private static final long serialVersionUID = 1L;

    public UnavailableException(String message, Throwable cause) { super(message, cause); }

    public UnavailableException(String message) { super(message); }
}
