package ejava.examples.orm.rel.composite;

import java.io.Serializable;

public class ResidentPK implements Serializable {
	private static final long serialVersionUID = -8638522228922200157L;
	private int house;
	private int residentId;
	
	public ResidentPK() {}
	public ResidentPK(int houseId, int residentId) {
		this.house=houseId;
		this.residentId=residentId;
	}
	
	public int getHouseId() { return house; }
	public int getResidentId() { return residentId; }
	
	@Override
	public int hashCode() {
		return house + residentId;
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if (obj==null) { return false; }
			if (this==obj) { return true; }
			ResidentPK rhs = (ResidentPK)obj;
			return house==rhs.house && residentId==rhs.residentId;
		} catch (Exception ex) { return false; }
	}	
}
