package myorg.relex.collection;

import javax.persistence.*;

/**
 * This class is provides an example of an entity that implements hashCode/equals 
 * using the default java.lang.Object implementation. Note this implementation is instance-specific. 
 * No other instance will report the same value even if they represent the same row in the DB.
 */
@Entity
@Table(name="RELATIONEX_SHIP")
public class ShipByDefault extends Ship {
	@Override
	public int peekHashCode() {
		return super.objectHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		try {
            if (this == obj) { return logEquals(obj, true); }
			boolean equals = super.equals(obj);
			return logEquals(obj, equals);
		} catch (Exception ex) {
			return logEquals(obj, false);
		}
	}
}
