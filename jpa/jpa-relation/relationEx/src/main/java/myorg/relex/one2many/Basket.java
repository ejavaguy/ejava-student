package myorg.relex.one2many;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * This entity class provides an example of mapping a collection of non-entity/embeddable class instances
 * to a dependent/child table and relating the child table to this entity table using a foreign key. 
 */
@Entity
@Table(name="RELATIONEX_BASKET")
public class Basket {
	@Id @GeneratedValue
	private int id;
	
	@ElementCollection
	@CollectionTable(
			name="RELATIONEX_BASKET_PRODUCE",
			joinColumns=@JoinColumn(name="BASKET_ID"))
	@AttributeOverrides({
		@AttributeOverride(name="name", column=@Column(name="ITEM_NAME")),
		@AttributeOverride(name="color", column=@Column(name="ITEM_COLOR"))
	})
	private List<Produce> contents;
	
	@Column(length=16)
	private String name;

	public int getId() { return id; }

	public List<Produce> getContents() {
		if (contents == null) { contents = new ArrayList<Produce>(); }
		return contents;
	}
	public void setContents(List<Produce> contents) {
		this.contents = contents;
	}

	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
}
