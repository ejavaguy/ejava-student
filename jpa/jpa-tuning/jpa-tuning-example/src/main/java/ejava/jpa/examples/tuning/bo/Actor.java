package ejava.jpa.examples.tuning.bo;

import java.util.Comparator;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="JPATUNE_ACTOR")
public class Actor {
	@Id
	private String id;

	@OneToOne(optional=false, fetch=FetchType.EAGER,
			cascade={CascadeType.PERSIST, CascadeType.DETACH})
	@MapsId
	@JoinColumn(name="PERSON_ID")
	private Person person;
	
	@OneToMany(mappedBy="actor", 
			cascade={CascadeType.PERSIST, CascadeType.REMOVE})
	private Set<MovieRole> roles= new TreeSet<MovieRole>(new Comparator<MovieRole>() {
		@Override
		public int compare(MovieRole lhs, MovieRole rhs) {
			if (lhs.getMovie() != null && rhs.getMovie() != null) {
				return lhs.getMovie().compareTo(rhs.getMovie());
			}
			return 0;
		}
	});

	protected Actor() {}
	public Actor(Person person) {
		this.person = person;
	}
	
	public Person getPerson() { return person; }
	public String getFirstName() { return person==null?null : person.getFirstName(); }
	public String getLastName() { return person==null?null : person.getLastName(); }
	public String getModName() { return person==null?null : person.getModName(); }
	public Date getBirthDate() { return person==null?null : person.getBirthDate(); }
	public Actor setFirstName(String name) { if (person!=null){ person.setFirstName(name);} return this;}
	public Actor setLastName(String name) { if (person!=null){ person.setLastName(name);} return this;}
	public Actor setModName(String name) { if (person!=null){ person.setModName(name);} return this;}
	public Actor setBirthDate(Date date) { if (person!=null){ person.setBirthDate(date);} return this;}

	public Set<MovieRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<MovieRole> roles) {
		this.roles = roles;
	}
	
	@Override
	public int hashCode() {
		return (person==null ? 0 : person.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		try { 
			if (this==obj) { return true; }
			if (obj==null) { return false; }
			Actor rhs = (Actor)obj;
			if (person == null) {
				if (rhs.person != null) { return false; }
			} else if (!person.equals(rhs.person)) {
				return false;
			}
			return true;
		} catch (Exception ex) { return false; }
	}
	
	@Override
	public String toString() {
		return person.toString(); 
	}
}
