package ejava.jpa.examples.cache;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="JPACACHE_ADDRESS")
@Cacheable(false)
public class Address {
	@Id @GeneratedValue
	private int id;
	@Column(length=32)
	private String street;
	@Column(length=32)
	private String city;
	@Column(length=2)
	private String state;
	@Column(length=10)
	private String zip;
	
	public Address() {}
	public Address(int id) { this.id=id; }	
	public int getId()     { return id; }
	
	public String getStreet() { return street; }
	public void setStreet(String street) {
		this.street = street;
	}
	
	public String getCity() { return city; }
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() { return state; }
	public void setState(String state) {
		this.state = state;
	}
	
	public String getZip() { return zip; }
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	@Override
	public String toString() {
		return street + " " + city + " " + state + ", " + zip;
	}
	
}
