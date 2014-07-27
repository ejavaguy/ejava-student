package myorg.relex.one2one;

import javax.persistence.*;

/**
 * This class demonstrates a one-to-one, uni-directional relationship
 * where the foreign key is used to define the primary key with the
 * use of @MapsId
 */
@Entity
@Table(name="RELATIONEX_COACH")
public class Coach {
	public enum Type {HEAD, ASSISTANT };
	@Id //provider sets to FK value with help from @MapsId 
	private int id;

	@OneToOne(optional=false, fetch=FetchType.EAGER)
	@MapsId //informs provider the PK is derived from FK
	private Person person;

	@Enumerated(EnumType.STRING) @Column(length=16)
	private Type type;

	public Coach() {}	
	public Coach(Person person) {
		this.person = person;
	}
	
	public int getId() { return person==null ? 0 : person.getId(); }
	public Person getPerson() { return person; }

	public Type getType() { return type; }
	public void setType(Type type) {
		this.type = type;
	}
}
