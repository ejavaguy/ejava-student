package ejava.projects.esales.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class provides a thin example of how to setup an Address business
 * object class for the project. Only a few fields are mapped and we
 * will make full use of JPA annotations over an orm.xml file in this 
 * example.
 * 
 * @author jcstaff
 *
 */
@SuppressWarnings("serial")
@Entity @Table(name="ESALES_ADDRESS")
public class Address implements Serializable {
	private long id;
	private String name;
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
	
	@Id @GeneratedValue @Column(name="ID")
	public long getId() {
		return id;
	}
	private void setId(long id) {
		this.id = id;
	}
	
	@Column(name="NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="CITY")
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
