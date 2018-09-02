package ejava.projects.esales.bo;

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
 *
 */
@SuppressWarnings("serial")
@Entity @Table(name="ESALES_ADDRESS")
public class Address implements Serializable {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="ID")
	private long id;

    @Column(name="NAME", length=10)
	private String name;

    @Column(name="CITY", length=20)
	private String city;
	
	public Address() {} 	
	public Address(long id) {
		setId(id); //use the set method to remove the unused warning
	}
	
	public Address(long id, String name, String city) {
		this.id = id;
		this.name = name;
		this.city = city;
	}
	
	public long getId() {
		return id;
	}
	private void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	public String toString() {
		StringBuilder text = new StringBuilder();
		text.append("id=" + id);
		text.append(", name=" + name);
		text.append(", city=" + city);
		return text.toString();
	}
}
