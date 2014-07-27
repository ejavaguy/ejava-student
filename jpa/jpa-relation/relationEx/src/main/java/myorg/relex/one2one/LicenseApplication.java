package myorg.relex.one2one;

import java.util.Date;
import javax.persistence.*;

/**
 * This class provides an example initiation of cascade actions to a 
 * related entity.
 */
@Entity
@Table(name="RELATIONEX_LICAPP")
public class LicenseApplication {
	@Id @GeneratedValue
	private int id;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;
	
	@OneToOne(optional=false, fetch=FetchType.EAGER, 
			cascade={
				CascadeType.PERSIST,
				CascadeType.DETACH,
				CascadeType.REMOVE,
				CascadeType.REFRESH,
				CascadeType.MERGE
			})
	private License license;
	
	public LicenseApplication() {}
	public LicenseApplication(License license) {
		this.license = license;
	}
	
	public int getId() { return id; }
	public License getLicense() { return license; }

	public Date getUpdated() { return updated; }
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
}
