package myorg.queryex;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.*;

@Entity
@Table(name="QUERYEX_DIRECTOR")
public class Director {
	@Id
	private String id;
	
	@OneToOne(optional=false, fetch=FetchType.EAGER,
			cascade={CascadeType.PERSIST, CascadeType.DETACH})
	@JoinColumn(name="PERSON_ID")
	@MapsId
	private Person person;

	@OneToMany(mappedBy="director", 
			cascade={CascadeType.PERSIST, CascadeType.DETACH})	
	private Set<Movie> movies;

	protected Director() {}	
	public Director(Person person) {
		this.person = person;
	}

	public Person getPerson() { return person; }
	public String getFirstName() { return person==null?null : person.getFirstName(); }
	public String getLastName() { return person==null?null : person.getLastName(); }
	public Date getBirthDate() { return person==null?null : person.getBirthDate(); }
	public Director setFirstName(String name) { if (person!=null){ person.setFirstName(name);} return this;}
	public Director setLastName(String name) { if (person!=null){ person.setLastName(name);} return this;}
	public Director setBirthDate(Date date) { if (person!=null){ person.setBirthDate(date);} return this;}

	public Set<Movie> getMovies() {
		if (movies == null) {
			movies = new TreeSet<Movie>();
		}
		return movies;
	}
	protected void setMovies(Set<Movie> movies) {
		this.movies = movies;
	}
	public Director addMovie(Movie...movie) {
		if (movie != null) {
			for (Movie m : movie) {
				getMovies().add(m);
			}
		}
		return this;
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
			Director rhs = (Director)obj;
			if (person == null) {
				if (rhs.getPerson() != null) { return false; }
			} else if (!person.equals(rhs.getPerson())) {
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
