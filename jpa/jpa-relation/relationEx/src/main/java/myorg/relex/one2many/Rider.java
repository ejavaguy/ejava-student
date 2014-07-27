package myorg.relex.one2many;

import javax.persistence.*;

/**
 * This class provides an example of an entity class on the many side of a one-to-many, 
 * uni-directional relationship that will be referenced through a JoinTable.
 */
@Entity
@Table(name="RELATIONEX_RIDER")
public class Rider {
    @Id @GeneratedValue
    private int id;
    @Column(length=32)
    private String name;
    
    public Rider() {}	
    public Rider(int id) {
		this.id = id;
	}

	public int getId() { return id; }
	
	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
}
