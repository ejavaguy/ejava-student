package myorg.relex.many2one;

import java.util.Date;

import javax.persistence.*;

/**
 * This class provides an example of a child entity that derives its primary key from 
 * the parent/one side of a many-to-one relation.
 */
@Entity
@Table(name="RELATIONEX_ITEM")
public class Item {
	@EmbeddedId
	private ItemPK id;
	
	@ManyToOne(optional=false)
	@MapsId("typeId") //refers to the ItemPK.typeId property
	@JoinColumn(name="TYPE_ID")
	private ItemType itemType;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	protected Item() {}
	public Item(ItemType itemType, int number) {		
		this.itemType = itemType;
			//typeId in PK auto-mapped to itemType FK
		this.id = new ItemPK().setNumber(number);
	}

	public ItemPK getId() { return id; }
	public ItemType getItemType() { return itemType; }

	public Date getCreated() { return created; }
	public void setCreated(Date created) {
		this.created = created;
	}
	
	@Override
	public String toString() {
		return (itemType==null?null:itemType) + "pk=" + id;
	}
}
