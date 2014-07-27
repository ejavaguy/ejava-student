package myorg.relex.one2one;

import javax.persistence.*;

/**
 * This entity class provides an example of an entity that
 * will get deleted when no longer referenced by its dependent
 * entity in a one-to-one relation. This is called orphan removal.
 */
@Entity
@Table(name="RELATIONEX_RESIDENCE")
public class Residence {
	@Id @GeneratedValue
	private int id;
	@Column(length=16, nullable=false)
	private String city;
	@Column(length=2, nullable=false)
	private String state;
	
	protected Residence() {}	
	public Residence(int id) { this.id = id; }
	public Residence(String city, String state) {
		this.city = city;
		this.state = state;
	}

	public int getId() { return id; }

	public String getCity() { return city; }
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() { return state;}
	public void setState(String state) {
		this.state = state;
	}
}
