package myorg.relex.one2one;

import javax.persistence.*;

/**
 * Provides example of one-to-one unidirectional relationship 
 * using foreign key.
 */
@Entity
@Table(name="RELATIONEX_PLAYER")
public class Player {
	public enum Position { DEFENSE, OFFENSE, SPECIAL_TEAMS};
	@Id @GeneratedValue
	private int id;
	@Enumerated(EnumType.STRING)
	@Column(length=16)
	private Position position;
	
	@OneToOne(optional=false,fetch=FetchType.EAGER)
	@JoinColumn(name="PERSON_ID")
	private Person person;
	
	public int getId() { return id; }

	public Person getPerson() { return person; }
	public void setPerson(Person person) {
		this.person = person;
	}

	public Position getPosition() { return position; }
	public void setPosition(Position position) {
		this.position = position;
	}
}
