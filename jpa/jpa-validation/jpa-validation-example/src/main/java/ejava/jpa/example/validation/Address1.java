package ejava.jpa.example.validation;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * This class provides an example of using GroupSequences where you can 
 * organize validation groups into a sequence which will short circuit
 * once one of the groups fails.
 */
@Entity
@Table(name="VALIDATION_ADDRESS")
@CityStateOrZip(groups=PreCheck.class)
public class Address1 {
	@Id @GeneratedValue
	private int id;
		
	@Column(name="STREET", length=32, nullable=false)
	@NotNull(message="street not supplied")
	@Size(max=32, message="street name too large", groups=DBChecks.class)
	@Pattern(regexp="^[0-9A-Za-z\\ ]+$", groups=DataChecks.class, 
	         message="street must be numbers and letters")
	private String street;
	
	@Column(name="CITY", length=20, nullable=false)
	@NotNull(message="city not supplied")
	@Size(max=20, message="city name too large", groups=DBChecks.class)
	@Pattern(regexp="^[a-zA-Z\\ ]+$", groups=DataChecks.class, 
	         message="city must be upper and lower case characters")
	private String city;
	
	@Column(name="STATE", length=2, nullable=false)
	@NotNull(message="state not supplied")
	@Size(min=2, max=2, message="state wrong size", groups=DBChecks.class)
	@Pattern(regexp="^[A-Z][A-Z]$", groups=DataChecks.class, 
	         message="state must be upper case letters")
	private String state;
	
	@Column(name="ZIP", length=5, nullable=false)
	@NotNull(message="zipcode not supplied")
	@Size(min=5, max=5, message="zipcode wrong size", groups=DBChecks.class)
	@Pattern(regexp="^[0-9][0-9][0-9][0-9][0-9]$", groups=DataChecks.class, 
	         message="zipcode must be numeric digits")
	private String zip;

	
	public int getId() { return id; }

	public String getStreet() { return street; }
	public Address1 setStreet(String street) {
		this.street = street;
		return this;
	}

	public String getCity() { return city; }
	public Address1 setCity(String city) {
		this.city = city;
		return this;
	}

	public String getState() { return state; }
	public Address1 setState(String state) {
		this.state = state;
		return this;
	}

	public String getZip() { return zip; }
	public Address1 setZip(String zip) {
		this.zip = zip;
		return this;
	}
	
	@Override
	public String toString() {
		return (street==null?"null":street) + " " + 
	           (city==null?"null":city) + ", " + 
			   (state==null?"null":state) + " " + 
	           (zip==null?"null":zip);
	}
	
}
