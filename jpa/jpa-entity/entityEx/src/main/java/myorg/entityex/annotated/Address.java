package myorg.entityex.annotated;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {
	@AttributeOverrides({
		@AttributeOverride(name="number", column=@Column(name="STREET_NUMBER")),
	})
	private Street street; //a second level of embedded
	@Column(name="CITY", length=16)
	private String city;
	@Column(name="STATE", length=16)
	private String state;

	public Street getStreet() { return street; }
	public Address setStreet(Street street) { this.street = street; return this; }
	
	public String getCity() { return city; }
	public Address setCity(String city) { this.city = city; return this; }
	
	public String getState() { return state; }
	public Address setState(String state) { this.state = state; return this; }
}