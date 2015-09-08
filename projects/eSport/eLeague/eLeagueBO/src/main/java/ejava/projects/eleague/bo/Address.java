package ejava.projects.eleague.bo;

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
@Entity @Table(name="ELEAGUE_ADDR")
public class Address implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String city;
	
	public Address() {} 	
	public Address(long id) {
		setId(id); //use the set method to remove the unused warning
	}
	
	public Address(long id, String city) {
		this.id = id;
		this.city = city;
	}
	
	@Id @GeneratedValue @Column(name="ID")
	public long getId() {
		return id;
	}
	private void setId(long id) {
		this.id = id;
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
		text.append(", city=" + city);
		return text.toString();
	}
}
