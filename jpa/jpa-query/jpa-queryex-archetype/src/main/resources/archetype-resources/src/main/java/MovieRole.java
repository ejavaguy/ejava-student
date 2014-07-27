#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import javax.persistence.*;

@Entity
@Table(name="QUERYEX_MOVIEROLE")
@IdClass(MovieRolePK.class)
public class MovieRole {
	@Id
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(name="MOVIE_ID")
	private Movie movie;

	@Id
	@Column(name="MOVIE_ROLE", length=32)
	private String role;
	
	@ManyToOne(optional=false, fetch=FetchType.EAGER,
			cascade={CascadeType.DETACH})
	@JoinColumn(name="ACTOR_ID")
	private Actor actor;

	public MovieRole() {}
	public MovieRole(String role) { this(null, role); }
	public MovieRole(Movie movie, String role) {
		this.movie = movie;
		this.role = role;
	}

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
