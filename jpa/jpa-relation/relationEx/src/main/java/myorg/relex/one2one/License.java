package myorg.relex.one2one;

import java.util.Date;
import javax.persistence.*;

/**
 * This class provides an example of a recipient of cascade actions. 
 */
@Entity
@Table(name="RELATIONEX_LICENSE")
public class License {
	@Id @GeneratedValue
	private int id;
	@Temporal(TemporalType.DATE)
	private Date renewal;
	
	public int getId() { return id; }

	public Date getRenewal() { return renewal; }
	public void setRenewal(Date renewal) {
		this.renewal = renewal;
	}
}
