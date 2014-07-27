package myorg.relex.one2one;

import javax.persistence.*;

/**
 * Target of uni-directional relationship
 */
@Entity
@Table(name="RELATIONEX_PERSON")
public class Person {
	@Id @GeneratedValue
	private int id;
	private String name;
	
	public int getId() { return id; }
	
	public String getName() { return name; }
	public void setName(String name) {
		this.name = name; 
	}
}
