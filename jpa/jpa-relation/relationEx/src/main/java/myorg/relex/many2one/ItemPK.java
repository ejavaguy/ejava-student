package myorg.relex.many2one;

import java.io.Serializable;
import javax.persistence.*;
/**
 * This class provides an example primary key class for a child entity that
 * derives one of its primary key values from its parent entity in a many-to-one
 * relationship.
 */
@SuppressWarnings("serial")
@Embeddable
public class ItemPK implements Serializable {
	@Column(name="TYPE_ID_PK")
	private int typeId; //unique value from parent ItemType.id	
	@Column(name="NUMBER_PK")
	private int number; //unique value assigned to instances of Item

	public int getTypeId() { return typeId; }
	public ItemPK setTypeId(int typeId) {
		this.typeId = typeId;
		return this;
	}	
	public int getNumber() { return number; }
	public ItemPK setNumber(int number) {
		this.number = number;
		return this;
	}
	
	@Override
	public int hashCode() {
		return typeId + number;
	}	
	@Override
	public boolean equals(Object obj) {
		try {
			if (this == obj) { return true; }
			ItemPK rhs = (ItemPK) obj;
			return typeId==rhs.typeId && number==rhs.number;
		} catch (Exception ex) { return false; }
	}
	@Override
	public String toString() {
		return "(typeId=" + typeId + ",number=" + number + ")";
	}
}
