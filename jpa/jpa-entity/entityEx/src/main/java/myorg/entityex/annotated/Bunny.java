package myorg.entityex.annotated;

import javax.persistence.*;

@Entity
@Table(name="ENTITYEX_BUNNY")
@Access(AccessType.FIELD)
public class Bunny {
	@Id @GeneratedValue//(strategy=GenerationType.IDENTITY)
	private int id;
	private String name;
	
	public int getId() { return id; }
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
}
