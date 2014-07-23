package myorg.entityex.annotated;

import javax.persistence.*;

@Entity
@Table(name="ENTITYEX_BEAR")
public class Bear {
	@Embeddable
	public static class Name {
		@Column(name="FIRST_NAME", length=16)
		private String firstName;
		private String lastName;

		public String getFirstName() { return firstName; }
		public Name setFirstName(String firstName) { this.firstName = firstName; return this; }
		
		public String getLastName() { return lastName; }
		public Name setLastName(String lastName) { this.lastName = lastName; return this; }
	}
	
	@Embeddable
	public static class Street {
		private int number;
		private String name;
		
		public int getNumber() { return number; }
		public Street setNumber(int number) { this.number = number; return this; }
		
		public String getName() { return name; }
		public Street setName(String name) { this.name = name; return this; }
	}
	
	@Embeddable
	public static class Address {
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
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@AttributeOverrides({
		@AttributeOverride(name="lastName", column=@Column(name="LAST_NAME", length=16))
	})
	@Embedded
	private Name name;
	@AttributeOverrides({
		@AttributeOverride(name="street.name", column=@Column(name="STREET_NAME", length=16)),
	})
	@Embedded
	private Address address;
	
	public int getId() { return id; }
	public void setId(int id) {
		this.id = id;
	}
	
	public Name getName() { return name; }
	public void setName(Name name) {
		this.name = name;
	}
	
	public Address getAddress() { return address; }
	public void setAddress(Address address) {
		this.address = address;
	}
}
