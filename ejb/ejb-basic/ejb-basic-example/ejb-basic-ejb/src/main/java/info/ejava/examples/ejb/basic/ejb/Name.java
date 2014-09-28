package info.ejava.examples.ejb.basic.ejb;

import java.io.Serializable;

/**
 * This class represents a serializable DTO object that can be used to 
 * send and receive data to/from a remote interface.
 */
public class Name implements Serializable {
	private static final long serialVersionUID = 1L;
	private String firstName;
	private String lastName;
	
	public Name(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Name [firstName=").append(firstName)
				.append(", lastName=").append(lastName)
				.append("]");
		return builder.toString();
	}
}
