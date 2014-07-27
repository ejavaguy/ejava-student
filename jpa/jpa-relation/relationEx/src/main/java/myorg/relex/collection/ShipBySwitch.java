package myorg.relex.collection;


import javax.persistence.*;

/**
 * This class is provides an example of an entity that implements hashCode/equals 
 * using its database assigned primary key if it exists and defaults to the 
 * java.lang.Object definition if not yet assigned.  Note that this technique causes
 * a change in hashCode/equals after the persist() takes place -- invalidating anything
 * previously cached for the identity.
 */
@Entity
@Table(name="RELATIONEX_SHIP")
public class ShipBySwitch extends Ship {
	@Override
	public int peekHashCode() {
		return id==0 ? super.objectHashCode() : id;
	}

	@Override
	public boolean equals(Object obj) {
		try {
            if (this == obj) { return logEquals(obj, true); }
			boolean equals = (id==0) ? super.equals(obj) :
				id==((ShipBySwitch)obj).id;
			return logEquals(obj, equals);
		} catch (Exception ex) {
			return logEquals(obj, false);
		}
	}
}
