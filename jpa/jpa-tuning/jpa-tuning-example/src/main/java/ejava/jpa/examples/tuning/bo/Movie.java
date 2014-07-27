package ejava.jpa.examples.tuning.bo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.*;

@Entity
@Table(name="JPATUNE_MOVIE")
@NamedQueries({ 
	@NamedQuery(name="Movie.findByTitle", query=
		"select m from Movie m " +
		"where lower(m.title) like concat(concat('%',lower(:title)),'%')")
})
@SqlResultSetMappings({
	@SqlResultSetMapping(name="Movie.movieMapping", entities={
			@EntityResult(entityClass=Movie.class),
			@EntityResult(entityClass=Director.class),
			@EntityResult(entityClass=Person.class)
	}),
	@SqlResultSetMapping(name="Movie.movieMapping2", entities={
			@EntityResult(entityClass=Movie.class),
			@EntityResult(entityClass=Director.class),
			@EntityResult(entityClass=Person.class, fields={
				@FieldResult(name="id", column="p_id"),
				@FieldResult(name="firstName", column="first_name"),
				@FieldResult(name="lastName", column="last_name"),
				@FieldResult(name="birthDate", column="birth_date")
			})
	})
})
public class Movie implements Comparable<Movie>{
	@Id
	@Column(name="ID", length=36)
	private String id;
	
	@Column(name="TITLE", length=32, nullable=false)
	private String title;
	
	@Temporal(TemporalType.DATE)
	@Column(name="RELEASE_DATE")
	private Date releaseDate;
		
	@Column(name="RATING", length=6)
	private String rating;
	
	@Transient
	private MovieRating mrating; 
	@PostLoad private void fromDB() { mrating=MovieRating.getFromMpaa(rating); }
	
	@Column(name="MINUTES")
	private Integer minutes;
	
	@Column(name="PLOT", length=4000)
	private String plot;
	
	@ElementCollection(fetch=FetchType.LAZY)
    @CollectionTable(
        name="JPATUNE_MOVIEGENRE",
          joinColumns=@JoinColumn(name="MOVIE_ID"), 
          uniqueConstraints=@UniqueConstraint(columnNames={"MOVIE_ID", "GENRE"}))
    @Column(name="GENRE", length=20)
	private Set<String> genres;

	@ManyToOne(fetch=FetchType.LAZY,
			cascade={CascadeType.PERSIST, CascadeType.DETACH})
	@JoinColumn(name="DIRECTOR_ID")
	private Director director;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="movie", 
			cascade={CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REMOVE})	
	private Set<MovieRole> cast;

	
	protected Movie() {}	
	public Movie(String id) {
		this.id = id;
	}
	public Movie(String id, Integer minutes, String rating, Date releaseDate, String title) {
		this.id=id;
		this.minutes = minutes;
		this.rating=rating;
		this.mrating=MovieRating.getFromMpaa(rating);
		this.releaseDate = releaseDate;
		this.title=title;
	}
	
	public String getId() { return id; }
	
	public String getTitle() { return title; }
	public Movie setTitle(String title) {
		this.title = title;
		return this;
	}

	public Date getReleaseDate() { return releaseDate; }
	public Movie setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
		return this;
	}

	public MovieRating getRating() { return mrating; }
	public Movie setRating(MovieRating rating) {
		this.mrating = rating;
		this.rating = rating==null ? null : rating.mpaa();
		return this;
	}

	public Integer getMinutes() { return minutes; }
	public Movie setMinutes(Integer minutes) {
		this.minutes = minutes;
		return this;
	}

	public Set<String> getGenres() {
		if (genres == null) {
			genres = new TreeSet<String>();
		}
		return genres; 
	}
	public Movie setGenres(Set<String> genres) {
		this.genres = genres;
		return this;
	}
	public Movie addGenres(String...genre) {
		if (genre!=null) {
			for (String g : genre) {
				getGenres().add(g);
			}
		}
		return this;
	}
	
	public String getPlot() { return plot; }
	public Movie setPlot(String plot) {
		this.plot = plot;
		return this;
	}

	public Director getDirector() { return director; }
	public Movie setDirector(Director director) {
		this.director = director;
		return this;
	}

	public Set<MovieRole> getCast() {
		if (cast==null) {
			cast=new HashSet<MovieRole>();
		}
		return cast; 
	}
	protected void setCast(Set<MovieRole> cast) {
		this.cast = cast;
	}
	public Movie addRole(MovieRole...role) {
		if (role!=null) { 
			for (MovieRole r : role) {
				r.setMovie(this);
				getCast().add(r);
			}
		}
		return this;
	}
	@Override
	public int hashCode() {
		return (director== null ? 0 : director.hashCode()) +
				(minutes==null ? 0 : minutes) +
				(rating==null? 0 : rating.hashCode()) +
				(releaseDate==null? 0 : releaseDate.hashCode()) +
				(title==null ? 0 : title.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		Movie rhs = (Movie)obj;
		
		if (title==null) {
			if (rhs.title != null) { return false; }
		} else if (!title.equals(rhs.title)) {
			return false;
		}
		
		if (director==null) {
			if (rhs.director != null) { return false; }
		} else if (!director.equals(rhs.director)) {
			return false;
		}
		
		if (releaseDate == null) {
			if (rhs.releaseDate != null) { return false; }
		} else if (!releaseDate.equals(rhs.releaseDate)) {
			return false;
		}
		
		if (minutes == null) {
			if (rhs.minutes != null) { return false; }
		} else if (!minutes.equals(rhs.minutes)) {
			return false; 
		} 
		
		if (rating == null) {
			if (rhs.rating != null) { return false; }
		} else if (!rating.equals(rhs.rating)) {
			return false;
		}
		
		return true;			
	}
	
	@Override
	public int compareTo(Movie rhs) {
		if (rhs == null) { return 1; }
		
		if (title != null && rhs.title != null && title.compareTo(rhs.title)!=0) {
			return title.compareTo(rhs.title);
		}
		if (releaseDate != null && rhs.releaseDate != null && releaseDate.compareTo(rhs.releaseDate)!=0) {
			return releaseDate.compareTo(rhs.releaseDate);
		}
		return 0;
	}
	
	@Override
	public String toString() {
		DateFormat df = new SimpleDateFormat("yyyy");
		return title + (releaseDate==null ? "" : " (" + df.format(releaseDate) + ")"); 
	}
}
