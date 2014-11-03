package myorg.entityex.annotated;

import javax.persistence.*;

@Entity
@Table(name="ENTITYEX_BEAR")
public class Bear {
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
