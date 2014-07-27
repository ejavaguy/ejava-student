package ejava.jpa.examples.tuning.bo;

import java.util.UUID;

import javax.persistence.*;

@Entity
@Table(name="JPATUNE_MOVIEROLE")
//@IdClass(MovieRolePK.class)
public class MovieRole {
	@Id
	@Column(name="ID", length=36)
	private String id;
	
	//@Id
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(name="MOVIE_ID")
	private Movie movie;

	//@Id
	@Column(name="MOVIE_ROLE", length=32, nullable=false)
	private String role;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY,
			cascade={CascadeType.DETACH})
	@JoinColumn(name="ACTOR_ID")
	private Actor actor;

	public MovieRole() {
		id=UUID.randomUUID().toString();
	}
	public MovieRole(String id, Movie movie, String role) {
		this.id = id;
		this.movie = movie;
		this.role = role;
	}
	public MovieRole(Movie movie, String role) {
		this();
		this.movie = movie;
		this.role = role;
	}
	
	public String getId() { return id; }

	public Movie getMovie() { return movie; }
	MovieRole setMovie(Movie movie) { this.movie=movie; return this; }
	public String getRole() { return role; }

	public Actor getActor() { return actor; }
	public MovieRole setActor(Actor actor) {
		this.actor = actor;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder text = new StringBuilder()
			.append(movie.getTitle())
			.append(" ")
			.append(role);
		if (actor != null) {
			text.append(" ").append(actor);
		}
		return text.toString();
	}
}
