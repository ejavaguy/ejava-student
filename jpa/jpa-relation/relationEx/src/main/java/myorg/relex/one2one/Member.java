package myorg.relex.one2one;

import javax.persistence.*;

/**
 * Provides example of one-to-one unidirectional relationship 
 * using join table.
 */
@Entity
@Table(name="RELATIONEX_MEMBER")
public class Member {
	public enum Role { PRIMARY, SECONDARY};

	@Id @GeneratedValue
	private int id;
	
	@OneToOne(optional=false,fetch=FetchType.LAZY)
	@JoinTable(name="RELATIONEX_MEMBER_PERSON",
		joinColumns={
			@JoinColumn(name="MEMBER_ID", referencedColumnName="ID"),
		}, inverseJoinColumns={
			@JoinColumn(name="PERSON_ID", referencedColumnName="ID"),
		}
	)
	private Person person;

	@Enumerated(EnumType.STRING)
	@Column(length=16)
	private Role role;
	
	protected Member() {}
	public Member(Person person) {
		this.person = person;
	}
	
	public int getId() { return id; }
	public Person getPerson() { return person; }

	public Role getRole() { return role; }
	public void setRole(Role role) {
		this.role = role;
	}
}
