package info.ejava.examples.ejb.basic.ejb;

import java.io.Serializable;
import java.util.Date;

/**
 * This class represents a serializable DTO object that can be used to 
 * send and receive data to/from a remote interface.
 */
public class Greeting implements Serializable {
	private static final long serialVersionUID = 1L;
	private Date date;
	private String message;

	public Greeting(Date date, String message) {
		this.date = date;
		this.message = message;
	}

	public String getGreeting() {
		return date + ":" + message;
	}

	@Override
	public String toString() {
		return getGreeting();
	}
}
