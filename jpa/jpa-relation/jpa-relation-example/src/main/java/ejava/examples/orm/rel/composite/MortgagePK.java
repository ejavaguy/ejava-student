package ejava.examples.orm.rel.composite;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MortgagePK implements Serializable {
	private static final long serialVersionUID = -8980087154312518474L;
	@Column(name="PK_HOUSE_ID") //overridden
	private int houseId;
	@Column(name="PK_MORTGAGE_ID") //overridden
	private int mortgageId;
	
	public MortgagePK() {}
	public MortgagePK(int houseId, int doorId) {
		this.houseId=houseId;
		this.mortgageId=doorId;
	}
	
	public int getHouseId() { return houseId; }
	public int getMortgageId() { return mortgageId; }
	
	@Override
	public int hashCode() {
		return houseId + mortgageId;
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if (obj==null) { return false; }
			if (this==obj) { return true; }
			MortgagePK rhs = (MortgagePK)obj;
			return houseId==rhs.houseId && mortgageId==rhs.mortgageId;
		} catch (Exception ex) { return false; }
	}
}
