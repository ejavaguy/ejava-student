package myorg.relex.one2one;

import javax.persistence.*;

/**
 * This class provides an example of the inverse side of a
 * one-to-one bi-directional relationship.
 */
@Entity
@Table(name="RELATIONEX_APPLICANT")
public class Applicant {
	@Id @GeneratedValue
	private int id;
	@Column(length=32)
	private String name;

	@OneToOne(
            mappedBy="applicant", //identifies property on owning side
			fetch=FetchType.LAZY)
//	@Transient
	private Application application;

	public Applicant(){}
	public Applicant(int id) {
		this.id = id;
	}
	
	public int getId() { return id; }

	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}

	public Application getApplication() { return application; }
	public void setApplication(Application application) {
		this.application = application;
	}
}
