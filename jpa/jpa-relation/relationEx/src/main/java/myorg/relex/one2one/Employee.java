package myorg.relex.one2one;

import java.util.Date;

import javax.persistence.*;

/**
 * Provides example of one-to-one unidirectional relationship 
 * using a primary key join.
 */
@Entity
@Table(name="RELATIONEX_EMPLOYEE")
public class Employee {
	@Id //pk value must be assigned, not generated
	private int id;
	
	@OneToOne(optional=false,fetch=FetchType.EAGER)
	@PrimaryKeyJoinColumn //informs provider the FK derived from PK
	private Person person;

	@Temporal(TemporalType.DATE)
	private Date hireDate;
	
	protected Employee() {}
	public Employee(Person person) {
		this.person = person;
		if (person != null) { id = person.getId(); }
	}

	public int getId() { return person.getId(); }
	public Person getPerson() { return person; }

	public Date getHireDate() { return hireDate; }
	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}
}
