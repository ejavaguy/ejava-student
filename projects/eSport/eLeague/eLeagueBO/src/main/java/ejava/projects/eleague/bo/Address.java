package ejava.projects.eleague.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class provides a thin example of how to setup an Address business
 * object class for the project. Only a few fields are mapped and we
 * will make full use of JPA annotations over an orm.xml file in this 
 * example.
 */
@Entity @Table(name="ELEAGUE_ADDR")
public class Address implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) 
	@Column(name="ID")
	private long id;

	@Column(name="CITY", length=20)
	private String city;
	
	public Address() {} 	
	public Address(long id) {
		setId(id); //use the set method to remove the unused warning
	}
	
	public Address(long id, String city) {
		this.id = id;
		this.city = city;
	}
	
	public long getId() {
		return id;
	}
	private void setId(long id) {
		this.id = id;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	public String toString() {
		StringBuilder text = new StringBuilder();
		text.append("id=").append(id);
		text.append(", city=").append(city);
		return text.toString();
	}
}
