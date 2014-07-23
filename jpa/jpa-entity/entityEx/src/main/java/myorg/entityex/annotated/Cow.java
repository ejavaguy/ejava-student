package myorg.entityex.annotated;

import javax.persistence.*;

@Entity
@Table(name="ENITYEX_COW")
public class Cow {
	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name="name", column=@Column(name="NAME", length=16))
	})
	private CowPK pk;
	private int weight;

	public Cow() {}
	public Cow(CowPK cowPK) {
		this.pk = cowPK;
	}
	
	public CowPK getPk() { return pk; }
	public void setPk(CowPK pk) {
		this.pk = pk;
	}
	
	public int getWeight() { return weight; }
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
