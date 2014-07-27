package myorg.relex.one2manybi;

import java.io.Serializable;

/**
 * This class provides an example of an IdClass used by a child entity in a 
 * many-to-one, bi-directional relationship where half of its primary key is
 * derived form the parentId;
 */
public class TirePK implements Serializable {
	private static final long serialVersionUID = -6028270454708159105L;
	private int car;   //shared primary key value from parent and child, name matches child rel
	private TirePosition position; //child primary key value unique within parent
	
	protected TirePK() {}
	public TirePK(int carId, TirePosition position) {
		this.car=carId;
		this.position=position;
	}
	
	public int getAutoId() { return car; }
	public TirePosition getPosition() { return position; }
	
	@Override
	public int hashCode() {
		return car + (position==null?0:position.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if (this==obj) { return true; }
			TirePK rhs = (TirePK)obj;
			return car==rhs.car && position==rhs.position;
		} catch (Exception ex) { return false; }
	}	
}
