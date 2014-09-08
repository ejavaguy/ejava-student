package myorg.entityex.annotated;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Name {
	@Column(name="FIRST_NAME", length=16)
	private String firstName;
	private String lastName;

	public String getFirstName() { return firstName; }
	public Name setFirstName(String firstName) { this.firstName = firstName; return this; }
	
	public String getLastName() { return lastName; }
	public Name setLastName(String lastName) { this.lastName = lastName; return this; }
}