package myorg.entityex.annotated;

import javax.persistence.*;

@Entity
@Table(name="ENITYEX_COW2")
@IdClass(CowPK.class)
@AttributeOverrides({
	@AttributeOverride(name="name", column=@Column(name="NAME", length=16))	
})
public class Cow2 {
	@Id
	private String herd;
	@Id
	private String name;
	private int weight;

	public Cow2() {}
	public Cow2(String herd, String name) {
		this.herd = herd;
		this.name = name;
	}
	
	public String getHerd() { return herd; }
	public String getName() { return name; }

	public int getWeight() { return weight; }
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
