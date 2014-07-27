package myorg.queryex;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;

public class MovieFactory {
	private EntityManager em;
	
	public MovieFactory setEntityManager(EntityManager em) {
		this.em = em;
		return this;
	}
	
	public void populate() {
		Actor a1 = new Actor(new Person("p1").setFirstName("Kevin").setLastName("Bacon")
				.setBirthDate(new GregorianCalendar(1958, Calendar.JULY, 8).getTime()));
		Actor a2 = new Actor(new Person("p2").setFirstName("John").setLastName("Belushi")
				.setBirthDate(new GregorianCalendar(1949, Calendar.JANUARY, 24).getTime()));		
		Actor a3 = new Actor(new Person("p3").setFirstName("Tim").setLastName("Matheson")
				.setBirthDate(new GregorianCalendar(1947, Calendar.DECEMBER, 31).getTime()));
		Actor a4 = new Actor(new Person("p4").setFirstName("Dustin").setLastName("Hoffman")
				.setBirthDate(new GregorianCalendar(1937, Calendar.AUGUST, 8).getTime()));
		Actor a5 = new Actor(new Person("p5").setFirstName("Robert").setLastName("De Niro")
				.setBirthDate(new GregorianCalendar(1943, Calendar.AUGUST, 17).getTime()));
		for (Actor a: new Actor[]{a1, a2, a3, a4, a5}) {
			em.persist(a);
		}
		
		Director d1 = new Director(new Person("d1").setFirstName("John").setLastName("Landis")
				.setBirthDate(new GregorianCalendar(1950, Calendar.AUGUST, 3).getTime()));
		Director d2 = new Director(new Person("d2").setFirstName("Herbert").setLastName("Ross")
				.setBirthDate(new GregorianCalendar(1927, Calendar.MAY, 13).getTime()));
		Director d3 = new Director(new Person("d3").setFirstName("Ron").setLastName("Underwood"));
		Director d4 = new Director(new Person("d4").setFirstName("Ron").setLastName("Howard")
				.setBirthDate(new GregorianCalendar(1954, Calendar.MARCH, 1).getTime()));
		Director d5 = new Director(new Person("d5").setFirstName("Barry").setLastName("Levinson")
				.setBirthDate(new GregorianCalendar(1942, Calendar.APRIL, 6).getTime()));
		for (Director d: new Director[]{d1, d2, d3, d4, d5}) {
			em.persist(d);
		}
		
		
		Movie m1 = new Movie("m1").setTitle("Animal House").setReleaseDate(new GregorianCalendar(1978, Calendar.JUNE, 1).getTime())
				.setDirector(d1)
				.setRating(MovieRating.R)
				.setMinutes(60+49)
				.addGenres("Comedy")
				.addRole(
					new MovieRole("Chip Diller").setActor(a1),
					new MovieRole("John Blutarsky").setActor(a2),
					new MovieRole("Eric Stratton").setActor(a3)
					);

		Movie m2 = new Movie("m2").setTitle("Footloose").setReleaseDate(new GregorianCalendar(1984, Calendar.FEBRUARY, 17).getTime())
				.setDirector(d2)
				.setRating(MovieRating.PG)
				.setMinutes(60+47)
				.addGenres("Drama")
				.addRole(
					new MovieRole("Ren McCormack").setActor(a1)
					);

		Movie m3 = new Movie("m3").setTitle("Tremors").setReleaseDate(new GregorianCalendar(1990, Calendar.JANUARY, 19).getTime())
				.setDirector(d3)
				.setRating(MovieRating.PG13)
				.setMinutes(60+36)
				.addGenres("Horror")
				.addRole(
					new MovieRole("Valentine McKee").setActor(a1)
					);

		Movie m4 = new Movie("m4").setTitle("Apollo 13").setReleaseDate(new GregorianCalendar(1995, Calendar.JUNE, 30).getTime())
				.setDirector(d4)
				.setRating(MovieRating.PG)
				.setMinutes((2*60)+20)
				.addGenres("Docudrama", "Historical Film", "Space Adventure", "Drama")
				.addRole(
					new MovieRole("Jack Swigert").setActor(a1)
					);

		Movie m5 = new Movie("m5").setTitle("Sleepers").setReleaseDate(new GregorianCalendar(1996, Calendar.OCTOBER, 18).getTime())
				.setDirector(d5)
				.setRating(MovieRating.R)
				.setMinutes((2*60)+32)
				.addGenres("Buddy Film", "Courtroom Drama", "Crime Drama", "Reunion Films", "Crime", "Drama")	
				.addRole(
					new MovieRole("Sean Nokes").setActor(a1),
					new MovieRole("Danny Snyder").setActor(a4)
					);

		Movie m6 = new Movie("m6").setTitle("Wag The Dog").setReleaseDate(new GregorianCalendar(1997, Calendar.DECEMBER, 25).getTime())
				.setDirector(d5)
				.setRating(MovieRating.R)
				.setMinutes((1*60)+37)
				.addGenres("Media Satire", "Political Satire", "Comedy")
				.addRole(
						new MovieRole("Stanley Motss").setActor(a4),
						new MovieRole("Conrad Brean").setActor(a5)
					);	

		Movie m7 = new Movie("m7").setTitle("Diner").setReleaseDate(new GregorianCalendar(1982, Calendar.APRIL, 2).getTime())
				.setDirector(d5)
				.setRating(MovieRating.R)
				.setMinutes((1*60)+50)
				.addGenres("Coming-of-Age", "Ensemble Film", "Reunion Films", "Comedy Drama")
				.addRole(
					new MovieRole("Timothy Fenwick, Jr.").setActor(a1)
					);
		
		for (Movie m: new Movie[]{m1, m2, m3, m4, m5, m6, m7}) {
			em.persist(m);
		}
	}
}
