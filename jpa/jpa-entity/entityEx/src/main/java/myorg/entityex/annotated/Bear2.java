package myorg.entityex.annotated;

import javax.persistence.*;

@Entity
@Table(name="ENTITYEX_BEAR2")
@SecondaryTables({
	@SecondaryTable(name="ENTITYEX_BEAR2_NAME"),
	@SecondaryTable(name="ENTITYEX_BEAR2_ADDRESS")
})
public class Bear2 {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column(table="ENTITYEX_BEAR2_NAME", name="FIRST_NAME", length=16)
	private String firstName;
	@Column(table="ENTITYEX_BEAR2_NAME", name="LAST_NAME", length=16)
	private String lastName;
	
	@Column(table="ENTITYEX_BEAR2_ADDRESS", name="STREET_NUMBER", length=16)
	private int streetNumber;
	@Column(table="ENTITYEX_BEAR2_ADDRESS", name="STREET_NAME", length=16)
	private String streetName;
	@Column(table="ENTITYEX_BEAR2_ADDRESS", name="CITY", length=16)
	private String city;
	@Column(table="ENTITYEX_BEAR2_ADDRESS", name="STATE", length=16)
	private String state;
	
	public int getId() { return id; }
	public Bear2 setId(int id) { this.id = id; return this; }
	
	public String getFirstName() { return firstName; }
	public Bear2 setFirstName(String firstName) {
		this.firstName = firstName; return this;
	}
	
	public String getLastName() { return lastName; }
	public Bear2 setLastName(String lastName) {
		this.lastName = lastName; return this;
	}
	
	public int getStreetNumber() { return streetNumber; }
	public Bear2 setStreetNumber(int streetNumber) { 
		this.streetNumber = streetNumber; return this; 
	}
	
	public String getStreetName() { return streetName; }
	public Bear2 setStreetName(String streetName) { 
		this.streetName = streetName; return this; 
	}
	
	public String getCity() { return city; }
	public Bear2 setCity(String city) { 
		this.city = city; return this; 
	}
	
	public String getState() { return state; }
	public Bear2 setState(String state) { 
		this.state = state; return this; 
	}
}
