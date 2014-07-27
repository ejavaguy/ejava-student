package myorg.relex.collection;


import javax.persistence.*;

/**
 * This class is provides an example of an entity that implements hashCode/equals 
 * using its business identity. Note that it is not always easy to derive a business Id
 * for an entity class.
 */
@Entity
@Table(name="RELATIONEX_SHIP")
public class ShipByBusinessId extends Ship {
	@Override
	public int peekHashCode() {
		return (name==null ? 0 : name.hashCode()) + 
			   (created==null ? 0 : (int)created.getTime());
	}

	@Override
	public boolean equals(Object obj) {
		try {
            if (this == obj) { return logEquals(obj, true); }
			boolean equals = name.equals(((ShipByBusinessId)obj).name) &&
					created.getTime() == (((ShipByBusinessId)obj).created.getTime());
			return logEquals(obj, equals);
		} catch (Exception ex) {
			return logEquals(obj, false);
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + 
				", name=" + name + 
				", created=" + (created==null ? 0 : created.getTime()); 
	}
}
