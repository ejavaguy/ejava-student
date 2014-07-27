package myorg.relex.many2one;

import javax.persistence.*;
/**
 * This class is an example of a parent in a many-to-one, uni-directional relation where the 
 * primary key of the child is derived from the primary key of the parent. 
 */
@Entity
@Table(name="RELATIONEX_ITEMTYPE")
public class ItemType {
	@Id @GeneratedValue
	private int id;
	
	@Column(length=20, nullable=false)
	private String name;
	
	protected ItemType() {}
	public ItemType(String name) {
		this.name = name;
	}

	public int getId() { return id; }

	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return id +":" + name;
	}
}