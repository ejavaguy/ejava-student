package myorg.queryex.criteria;

import static org.junit.Assert.*;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import myorg.queryex.Actor;
import myorg.queryex.Director;
import myorg.queryex.Movie;
import myorg.queryex.MovieRating;
import myorg.queryex.MovieRole;
import myorg.queryex.Person;
import myorg.queryex.QueryBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CriteriaTest extends QueryBase {
	private static final Logger log = LoggerFactory.getLogger(CriteriaTest.class);
	private EntityManager em2; //second persistence context for parallel queries
	
	@Before
	public void init() {
		if (emf!=null) {
			em2=emf.createEntityManager();
		}
	}
	
	@After
	public void destroy() {
		destroy(em2);
	}
	
	private static void destroy(EntityManager emgr) {
		if (emgr!=null && emgr.isOpen()) {
			if (emgr.getTransaction().isActive()) {
				if (emgr.getTransaction().getRollbackOnly()) {
					emgr.getTransaction().rollback();
				} else {
					emgr.getTransaction().commit();
				}
			}
			emgr.close();
		}
	}
	

	/**
	 * This test demonstrates a basic criteria query as a starting point.
	 * Notice how "p" is designed as the query root in both styles of query
	 * and all expressions from that point are derived from the root.
	 */
	@Test
	public void testBasicQuery() {
		log.info("*** testBasicQuery ***");
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select p from Person p ")
			.append("where p.firstName=:firstName");
		TypedQuery<Person> lquery = em.createQuery(qlString.toString(), Person.class)
			.setParameter("firstName", "Ron");
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Person> cqdef = cb.createQuery(Person.class);
		Root<Person> p = cqdef.from(Person.class); 
		TypedQuery<Person> cquery = em2.createQuery(cqdef
				.select(p)
				.where(cb.equal(p.get("firstName"), "Ron")));
		
		log.debug("execute JPAQL query");
		List<Person> lresults = lquery.getResultList();
		log.debug("accessing jpaql results");
		log.debug("jpaql results  =" + lresults);
		
		log.debug("execute Criteria API query");
		List<Person> cresults = cquery.getResultList();		
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);
		
		log.debug("comparing query results");
		assertEquals("unexpected results",  lresults, cresults);
	}
	
	/**
	 * This test demonstrates the use of "from" in the Criteria to define 
	 * multiple query roots. In this example we are querying from both 
	 * Director and Person entities and forming our query based off these 
	 * query roots.
	 */
	@Test
	public void testFrom() {
		log.info("*** testFrom ***");
		
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select d from Director d, " +  //from
					"Person p ")                    //from
			.append("where " +
					"p=d.person and " +
					"p.firstName=:firstName");
		TypedQuery<Director> lquery = em.createQuery(qlString.toString(), Director.class)
			.setParameter("firstName", "Ron");
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Director> cqdef = cb.createQuery(Director.class);
		Root<Director> d = cqdef.from(Director.class); //from
		Root<Person> p = cqdef.from(Person.class);     //from
		TypedQuery<Director> cquery = em2.createQuery(cqdef
				.select(d)
				.where(cb.and(
						cb.equal(d.get("person"), p)), 
						cb.equal(p.get("firstName"), "Ron"))
						);
		
		log.debug("execute JPAQL query");
		List<Director> lresults = lquery.getResultList();
		log.debug("accessing jpaql results");
		log.debug("jpaql results  =" + lresults);
		em.clear();
		
		log.debug("execute Criteria API query");
		List<Director> cresults = cquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);
		
		log.debug("comparing query results");
		assertEquals("unexpected results",  lresults, cresults);
	}

	/**
	 * This test demonstrates the use of a path expression. JPAQL uses a 
	 * name(dot)name path notation. The criteria API uses a changed set of 
	 * get(name).get(name).
	 */
	@Test
	public void testPath() {
		log.info("*** testPath ***");
		
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select d from Director d ")
			.append("where " +
					"d.person.firstName" +  //Path expression
					"=:firstName");
		TypedQuery<Director> lquery = em.createQuery(qlString.toString(), Director.class)
			.setParameter("firstName", "Ron");
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Director> cqdef = cb.createQuery(Director.class);
		Root<Director> d = cqdef.from(Director.class);
		TypedQuery<Director> cquery = em2.createQuery(cqdef
				.select(d)//non-portable to eliminate redundant select() matching from()
				.where(cb.equal( 
						d.get("person").get("firstName") //Path expression
						,"Ron"))
					);
		
		log.debug("execute JPAQL query");
		List<Director> lresults = lquery.getResultList();
		log.debug("jpaql results  =" + lresults);
		log.debug("accessing criteria results");
		
		log.debug("execute Criteria API query");
		List<Director> cresults = cquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);

		log.debug("comparing query results");
		assertEquals("unexpected results",  lresults, cresults);
	}
	
	/**
	 * This test demonstrates the use of select() to be passed a path 
	 * expression instead of the query root. This also demonstrates the
	 * use of setting distinct against the CriteriaQuery. The path expressions
	 * require a template parameter that expresses the return type of the 
	 * query -- which is not the query root type in this case.
	 */
	@Test
	public void testSingleSelect() {
		log.info("*** testSingleSelect ***");
		
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
				//passing an path expression to select rather than root
			.append("select distinct p.firstName " +     //select
					"from Person p ")
			.append("where p.firstName like :pattern");
		TypedQuery<String> lquery = em.createQuery(qlString.toString(), String.class)
			.setParameter("pattern", "R%");
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<String> cqdef = cb.createQuery(String.class);
		Root<Person> p = cqdef.from(Person.class);
		TypedQuery<String> cquery = em2.createQuery(cqdef
				//passing an path expression to select() rather than root
			.select(p.<String>get("firstName"))   //select
			.distinct(true)
				//path expressions must agree with type used in createQuery
			.where(cb.like(p.<String>get("firstName"),"R%"))
			);
		
		log.debug("execute JPAQL query");
		List<String> lresults = lquery.getResultList();
		log.debug("jpaql results  =" + lresults);
		log.debug("accessing criteria results");
		
		log.debug("execute Criteria API query");
		List<String> cresults = cquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);

		log.debug("comparing query results");
		assertEquals("unexpected results",  lresults, cresults);
	}

	/**
	 * This test demonstrates the ability to provide multiple elements in the 
	 * select clause. In this example we are also ordering the results so they
	 * can be accurately compared. In this specific example -- we use an Object[]
	 * for the query return type.
	 */
	@Test
	public void testMultiSelect() {
		log.info("*** testMultiSelect ***");
		
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select distinct p.firstName, p.lastName " +
					"from Person p ")
			.append("where p.firstName like :pattern " +
					"order by p.firstName ASC");
		TypedQuery<Object[]> lquery = em.createQuery(qlString.toString(), Object[].class)
			.setParameter("pattern", "R%");
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Object[]> cqdef = cb.createQuery(Object[].class);
		Root<Person> p = cqdef.from(Person.class);
		TypedQuery<Object[]> cquery = em2.createQuery(cqdef
				.select(cb.array(p.get("firstName"), p.get("lastName")))  
				.distinct(true)
				.where(cb.like(p.<String>get("firstName"),"R%"))
				.orderBy(cb.asc(p.get("firstName")))
			);
		
		log.debug("execute JPAQL query");
		List<Object[]> lresults = lquery.getResultList();
		log.debug("accessing criteria results");
		for (Object[] row: lresults) {
			log.debug("jpaql results  =" + row[0] + " " + row[1]);
		}
		
		log.debug("execute Criteria API query");
		List<Object[]> cresults = cquery.getResultList();
		log.debug("accessing criteria results");
		for (Object[] row: lresults) {
			log.debug("criteria results  =" + row[0] + " " + row[1]);
		}

		log.debug("comparing query results");
		Iterator<Object[]> litr = lresults.iterator();
		Iterator<Object[]> citr = cresults.iterator();
		while (litr.hasNext() && citr.hasNext()) {
			Object[] lrow = litr.next(); Object[] crow = citr.next();
			for (int i=0; i<2; i++) {
				assertEquals("unexpected tuple value:" + i, lrow[i], crow[i]);
			}
		}
	}

	static private class FirstLast {
		public String first;
		public String last;
		@SuppressWarnings("unused")
		public FirstLast(String first, String last) {
			this.first = first;
			this.last = last;
		}
		public String toString() { return first + " " + last; }
	}

	/**
	 * This test is the same as the prior example except that it used a constructor
	 * of a transient object to return type-safe results over the user of Object[]
	 */
	@Test
	public void testMultiSelectConstructorExpression() {
		log.info("*** testMultiSelectConstructorExpression ***");
		
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append(String.format("select distinct new %s(p.firstName, p.lastName) ", 
					FirstLast.class.getName()) +
					"from Person p ")
			.append("where p.firstName like :pattern " +
					"order by p.firstName ASC");
		TypedQuery<FirstLast> lquery = em.createQuery(qlString.toString(), FirstLast.class)
			.setParameter("pattern", "R%");
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<FirstLast> cqdef = cb.createQuery(FirstLast.class);
		Root<Person> p = cqdef.from(Person.class);
		TypedQuery<FirstLast> cquery = em2.createQuery(cqdef
				.select(cb.construct(FirstLast.class, p.get("firstName"), p.get("lastName")))  
				//.multiselect(p.get("firstName"), p.get("lastName")) //shorthand	  
				.distinct(true)
				.where(cb.like(p.<String>get("firstName"),"R%"))
				.orderBy(cb.asc(p.get("firstName")))
			);
		
		log.debug("execute JPAQL query");
		List<FirstLast> lresults = lquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("jpaql results   =" + lresults);
		
		log.debug("execute Criteria API query");
		List<FirstLast> cresults = cquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("criteria results=" + cresults);

		log.debug("comparing query results");
		Iterator<FirstLast> litr = lresults.iterator();
		Iterator<FirstLast> citr = cresults.iterator();
		while (litr.hasNext() && citr.hasNext()) {
			FirstLast lrow = litr.next(); FirstLast crow = citr.next();
			assertEquals("unexpected tuple value:first", lrow.first, crow.first);
			assertEquals("unexpected tuple value:last", lrow.last, crow.last);
		}
	}

	/**
	 * This example is the same as before except it is using the JPA Tuple interface
	 * for the return type.
	 */
	@Test
	public void testMultiSelectTuple() {
		log.info("*** testMultiSelectTuple ***");
		
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select distinct p.firstName as fname, p.lastName as lname " +
					"from Person p ")
			.append("where p.firstName like :pattern " +
					"order by p.firstName ASC");
		TypedQuery<Tuple> lquery = em.createQuery(qlString.toString(), Tuple.class)
			.setParameter("pattern", "R%");
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Tuple> cqdef = cb.createTupleQuery();
		Root<Person> p = cqdef.from(Person.class);
		TypedQuery<Tuple> cquery = em2.createQuery(cqdef
				.select(cb.tuple(
						p.get("firstName").alias("fname"), 
						p.get("lastName").alias("lname"))) 
				//.multiselect(  //shorthand
				//		p.get("firstName").alias("fname"), 
				//		p.get("lastName").alias("lname")) 
				.distinct(true)
				.where(cb.like(p.<String>get("firstName"),"R%"))
				.orderBy(cb.asc(p.get("firstName")))
			);
		
		log.debug("execute JPAQL query");
		List<Tuple> lresults = lquery.getResultList();
		log.debug("accessing criteria results");
		for (Tuple t: lresults) {
			log.debug("jpaql results  =" + 
					t.get("fname",String.class) + " " + 
					t.get("lname", String.class));
		}
		
		log.debug("execute Criteria API query");
		List<Tuple> cresults = cquery.getResultList();
		log.debug("accessing criteria results");
		for (Tuple t: cresults) {
			log.debug("criteria results  =" + 
					t.get("fname",String.class) + " " + 
					t.get("lname", String.class));
		}

		log.debug("comparing query results");
		Iterator<Tuple> litr = lresults.iterator();
		Iterator<Tuple> citr = cresults.iterator();
		while (litr.hasNext() && citr.hasNext()) {
			Tuple lt = litr.next(); Tuple ct = citr.next();
			for (String alias: new String[]{"fname","lname"}) {
				assertEquals("unexpected tuple value:" + alias, 
					lt.get(alias,String.class), ct.get(alias, String.class));
			}
		}
	}

	/**
	 * This test method demonstrates extending the query of the root to 
	 * related objects through a join. 
	 */
	@Test
	public void testJoin() {
		log.info("*** testJoin ***");
		
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select distinct m from Movie m " +
					"LEFT JOIN m.director d " +
					"LEFT JOIN d.person p ")
			.append("where p.firstName=:firstName ")
			.append("order by m.releaseDate ASC ");
		TypedQuery<Movie> lquery = em.createQuery(qlString.toString(), Movie.class)
			.setParameter("firstName", "Ron");
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Movie> cqdef = cb.createQuery(Movie.class);
		Root<Movie> m = cqdef.from(Movie.class);
		Join<Movie, Director> d = m.join("director", JoinType.LEFT);
		Join<Director, Person> p = d.join("person", JoinType.LEFT);
		TypedQuery<Movie> cquery = em2.createQuery(cqdef
				.select(m)
				.distinct(true)
				.where(cb.equal(p.get("firstName"), "Ron"))
				.orderBy(cb.asc(m.get("releaseDate")))
			);
		
		log.debug("execute JPAQL query");
		List<Movie> lresults = lquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("jpaql results  =" + lresults); 
		
		log.debug("execute Criteria API query");
		List<Movie> cresults = cquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("criteria results  =" +  cresults);

		log.debug("comparing query results");
		Iterator<Movie> litr = lresults.iterator();
		Iterator<Movie> citr = cresults.iterator();
		while (litr.hasNext() && citr.hasNext()) {
			Movie lm = litr.next(); Movie cm = citr.next();
			assertTrue(String.format("unequal movies: (%s) (%s)", lm, cm), 
					lm.equals(cm));
			assertTrue(String.format("unequal directors: (%s) (%s)", 
					lm.getDirector() , cm.getDirector()), 
					lm.getDirector().equals(cm.getDirector()));
			Iterator<MovieRole> lmrItr = lm.getCast().iterator();
			Iterator<MovieRole> cmrItr = cm.getCast().iterator();
			while (lmrItr.hasNext() && cmrItr.hasNext()) {
				MovieRole lmr=lmrItr.next(); MovieRole cmr=cmrItr.next();
				assertTrue(String.format("unequal role: (%s) (%s)", lmr , cmr), 
						lmr.equals(cmr));
			}
		}
		em2.close();
	}

	/**
	 * This class demonstrates the use of a FETCH JOIN -- where related 
	 * entities are retrieved from the database as a side-effect of executing
	 * the query. 
	 */
	@Test
	@SuppressWarnings("unused")
	public void testFetchJoin() {
		log.info("*** testFetchJoin ***");

		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select distinct m from Movie m " +
					"LEFT JOIN FETCH m.director d " +
					"LEFT JOIN FETCH m.cast role " +
					"LEFT JOIN FETCH d.person dp " +
					"LEFT JOIN FETCH role.actor actor " +
					"LEFT JOIN FETCH actor.person ap ")
			.append("order by m.releaseDate ASC");
		TypedQuery<Movie> lquery = em.createQuery(qlString.toString(), Movie.class);
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Movie> cqdef = cb.createQuery(Movie.class);
		Root<Movie> m = cqdef.from(Movie.class);
		Fetch<Movie, Director> d = m.fetch("director", JoinType.LEFT);
		Fetch<Director, Person> dp = d.fetch("person", JoinType.LEFT);
		Fetch<Movie, MovieRole> role = m.fetch("cast", JoinType.LEFT);
		Fetch<MovieRole, Actor> actor = role.fetch("actor", JoinType.LEFT);
		Fetch<Actor, Person> ap = actor.fetch("person", JoinType.LEFT);
		TypedQuery<Movie> cquery = em2.createQuery(cqdef
				.select(m)
				.distinct(true)
				.orderBy(cb.asc(m.get("releaseDate")))
			);
		
		log.debug("execute JPAQL query");
		List<Movie> lresults = lquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("jpaql results  =" + lresults); 
		
		log.debug("execute Criteria API query");
		List<Movie> cresults = cquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("criteria results  =" +  cresults);

		log.debug("closing entity managers since all data FETCHed");
		em.close(); em=null; em2.close();
		
		log.debug("comparing query results");
		Iterator<Movie> litr = lresults.iterator();
		Iterator<Movie> citr = cresults.iterator();
		while (litr.hasNext() && citr.hasNext()) {
			Movie lm = litr.next(); Movie cm = citr.next();
			assertTrue(String.format("unequal movies: (%s) (%s)", lm, cm), 
					lm.equals(cm));
			assertTrue(String.format("unequal directors: (%s) (%s)", 
					lm.getDirector() , cm.getDirector()), 
					lm.getDirector().equals(cm.getDirector()));
			Iterator<MovieRole> lmrItr = lm.getCast().iterator();
			Iterator<MovieRole> cmrItr = cm.getCast().iterator();
			while (lmrItr.hasNext() && cmrItr.hasNext()) {
				MovieRole lmr=lmrItr.next(); MovieRole cmr=cmrItr.next();
				assertTrue(String.format("unequal role: (%s) (%s)", lmr , cmr), 
						lmr.equals(cmr));
			}
		}
	}

	/**
	 * This test demonstrates use of the were clause with a single boolean
	 * expression.
	 */
	@Test
	public void testWhereBooleanExpression() {
		log.info("*** testWhereBooleanExpression ***");
		
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select distinct m from Movie m " +
					"where m.rating=:rating ")
			.append("order by m.releaseDate ASC");
		TypedQuery<Movie> lquery = em.createQuery(qlString.toString(), Movie.class)
			.setParameter("rating", MovieRating.R);
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Movie> cqdef = cb.createQuery(Movie.class);
		Root<Movie> m = cqdef.from(Movie.class);
		cqdef.select(m)
			.where(cb.equal(m.get("rating"), MovieRating.R))
			.orderBy(cb.asc(m.get("releaseDate")));
		TypedQuery<Movie> cquery = em2.createQuery(cqdef);
		
		log.debug("execute JPAQL query");
		List<Movie> lresults = lquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("jpaql results  =" + lresults); 
		
		log.debug("execute Criteria API query");
		List<Movie> cresults = cquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("criteria results  =" +  cresults);

		log.debug("comparing query results");
		Iterator<Movie> litr = lresults.iterator();
		Iterator<Movie> citr = cresults.iterator();
		while (litr.hasNext() && citr.hasNext()) {
			Movie lm = litr.next(); Movie cm = citr.next();
			assertTrue(String.format("unequal movies: (%s) (%s)", lm, cm), 
					lm.equals(cm));
		}
	}

	/**
	 * This test provides an example of using multiple predicates in the 
	 * where clause. It also provides an example of using parameters -- which
	 * was leveraged to provide a TemporalType.DATE comparison. Using parameters
	 * does provide a syntax a little closer to 1:1 with JPAQL -- but the 
	 * effort it takes to setup and supply parameter makes the Criteria
	 * approach much more verbose. 
	 */
	@Test
	public void testWherePredicates() {
		log.info("*** testWherePredicates ***");
		
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select distinct m from Movie m " +
					"INNER JOIN m.genres genre " +
					"JOIN FETCH m.genres ")
			.append("where m.rating=:rating " +
					"and genre like :genre " +
					"and m.releaseDate >=:releaseDate ")
			.append("order by m.releaseDate ASC ");
		TypedQuery<Movie> lquery = em.createQuery(qlString.toString(), Movie.class)
			.setParameter("rating", MovieRating.R)
			.setParameter("genre", "%Comedy%")
			.setParameter("releaseDate", 
					new GregorianCalendar(1990, 0, 0).getTime(), 
					TemporalType.DATE);
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Movie> cqdef = cb.createQuery(Movie.class);
		Root<Movie> m = cqdef.from(Movie.class);
		Join<Movie, String> genre = m.join("genres");
		m.fetch("genres"); //eager load genres		
		ParameterExpression<Date> releaseDateParam = 
				cb.parameter(Date.class, "releaseDate");
		ParameterExpression<MovieRating> ratingParam = 
				cb.parameter(MovieRating.class, "rating");		
		ParameterExpression<String> genreParam = 
				cb.parameter(String.class, "genre");
		cqdef.select(m).distinct(true)
			.where(cb.equal(m.get("rating"), ratingParam),
				   cb.like(genre, genreParam),
				   cb.greaterThanOrEqualTo(
						m.<Date>get("releaseDate"), releaseDateParam))
			.orderBy(cb.desc(m.get("releaseDate")));
		TypedQuery<Movie> cquery = em2.createQuery(cqdef)
			.setParameter("rating", MovieRating.R)
			.setParameter("genre", "%Comedy%")
			.setParameter("releaseDate", 
					new GregorianCalendar(1990,0,0).getTime(), 
					TemporalType.DATE);
		
		log.debug("execute JPAQL query");
		List<Movie> lresults = lquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("jpaql results  =" + lresults); 
		for (Movie movie : lresults) {
			log.debug("jpaql results  =" +  movie + movie.getGenres());
		}
		
		log.debug("execute Criteria API query");
		List<Movie> cresults = cquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("criteria results=" + lresults); 
		for (Movie movie : cresults) {
			log.debug("criteria results=" +  movie + movie.getGenres());
		}

		log.debug("comparing query results");
		Iterator<Movie> litr = lresults.iterator();
		Iterator<Movie> citr = cresults.iterator();
		while (litr.hasNext() && citr.hasNext()) {
			Movie lm = litr.next(); Movie cm = citr.next();
			assertTrue(String.format("unequal movies: (%s) (%s)", lm, cm), 
					lm.equals(cm));
			assertArrayEquals("unexpected genres", 
					lm.getGenres().toArray(new String[]{}), 
					cm.getGenres().toArray(new String[]{})
					);
		}
	}
	
	/**
	 * This test provides a demonstration of building up the wehere clause
	 * Predicate in an incremental manner.
	 */
	@Test
	public void testIncrementalWherePredicates() {
		log.info("*** testIncrementalWherePredicates ***");
		
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select distinct m from Movie m " +
					"INNER JOIN m.genres genre " +
					"JOIN FETCH m.genres ");
		//iteratively build up the where clause -- supply first in-line
		qlString.append(String.format("where m.rating='%s' ", MovieRating.R.name()));
		qlString.append("and genre like :genre ");
		qlString.append("and m.releaseDate >=:releaseDate ");
		//add the trailing order by clause
		qlString.append("order by m.releaseDate ASC ");
		//form the base query
		TypedQuery<Movie> lquery = em.createQuery(qlString.toString(), Movie.class);
		//incrementally add parameter values 
		lquery.setParameter("genre", "%Comedy%");
		lquery.setParameter("releaseDate", 
					new GregorianCalendar(1990, 0, 0).getTime(), 
					TemporalType.DATE);
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Movie> cqdef = cb.createQuery(Movie.class);
		Root<Movie> m = cqdef.from(Movie.class);
		Join<Movie, String> genre = m.join("genres");
		m.fetch("genres"); //eager load genres		
		cqdef.select(m).distinct(true)
			.orderBy(cb.desc(m.get("releaseDate")));		
		//incrementally build up the where clause -- supply first in-line
		Predicate predicate = cb.conjunction();
		predicate = cb.and(predicate, 
			cb.equal(m.get("rating"), MovieRating.R));
		predicate = cb.and(predicate, 
			cb.like(genre, 
				    cb.parameter(String.class,"genre")));
		predicate = cb.and(predicate, 
			cb.greaterThanOrEqualTo(m.<Date>get("releaseDate"), 
				    cb.parameter(Date.class,"releaseDate")));
		cqdef.where(predicate);

		TypedQuery<Movie> cquery = em2.createQuery(cqdef);
		cquery.setParameter("genre", "%Comedy%");
		cquery.setParameter("releaseDate", 
					new GregorianCalendar(1990,0,0).getTime(), 
					TemporalType.DATE);
		
		log.debug("execute JPAQL query");
		List<Movie> lresults = lquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("jpaql results  =" + lresults); 
		for (Movie movie : lresults) {
			log.debug("jpaql results  =" +  movie + movie.getGenres());
		}
		
		log.debug("execute Criteria API query");
		List<Movie> cresults = cquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("criteria results=" + lresults); 
		for (Movie movie : cresults) {
			log.debug("criteria results=" +  movie + movie.getGenres());
		}

		log.debug("comparing query results");
		Iterator<Movie> litr = lresults.iterator();
		Iterator<Movie> citr = cresults.iterator();
		while (litr.hasNext() && citr.hasNext()) {
			Movie lm = litr.next(); Movie cm = citr.next();
			assertTrue(String.format("unequal movies: (%s) (%s)", lm, cm), 
					lm.equals(cm));
			assertArrayEquals("unexpected genres", 
					lm.getGenres().toArray(new String[]{}), 
					cm.getGenres().toArray(new String[]{})
					);
		}
	}

	/**
	 * This test provides a demonstration of using literal values. Many of the 
	 * Criteria API work correctly with many built-in data types, but there are
	 * times when you need to wrap a literal value of type T as an Expression<T>
	 * or to express a null value. In this case the literal(T value) or 
	 * nullLiteral(T valueType) is used to build the required expression.
	 */
	@Test
	public void testBasicLiterals() {
		log.info("*** testBasicQuery ***");
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select p from Person p ")
			.append("where " +
					"p.birthDate is null " +
					"or p.birthDate > :birthDate ")
			.append("order by lastName ASC");
		TypedQuery<Person> lquery = em.createQuery(qlString.toString(), Person.class)
			.setParameter("birthDate", new GregorianCalendar(1950, 0, 0).getTime());
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Person> cqdef = cb.createQuery(Person.class);
		Root<Person> p = cqdef.from(Person.class); 
		cqdef.select(p)				
			.where(cb.or(
				cb.equal(p.get("birthDate"), cb.nullLiteral(Date.class)),
				cb.greaterThan(p.<Date>get("birthDate"), 
					cb.literal(new GregorianCalendar(1950, 0, 0).getTime()))
			))
			.orderBy(cb.asc(p.get("lastName")));
		TypedQuery<Person> cquery = em2.createQuery(cqdef);
		
		log.debug("execute JPAQL query");
		List<Person> lresults = lquery.getResultList();
		log.debug("accessing jpaql results");
		log.debug("jpaql results  =" + lresults);
		for (Person person: lresults) {
			log.debug("jpaql results  =" + person + ", " + person.getBirthDate());
		}
		
		log.debug("execute Criteria API query");
		List<Person> cresults = cquery.getResultList();		
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);
		for (Person person: cresults) {
			log.debug("critera results=" + person + ", " + person.getBirthDate());
		}
		
		log.debug("comparing query results");
		assertEquals("unexpected results",  lresults, cresults);
	}

	/**
	 * This test demonstrates a subquery. It also demonstrates a multi-select
	 * so that that targeted objects are eagerly loaded by the query. The 
	 * subquery is used to locate all Movies with Kevin Bacon. The outer query
	 * locates all actors that were also in a Kevin Bacon movie.
	 */
	@Test
	public void testSubQuery() {
		log.info("*** testSubQuery ***");
		//build JPAQL query
			//build the subquery
		StringBuilder subqlString = new StringBuilder()
			.append("select m from Movie m " +
					"JOIN m.cast role ")
			.append("where " +
					"role.actor.person.firstName = 'Kevin' " +
					"and role.actor.person.lastName = 'Bacon'");
			//build the outer query
		StringBuilder qlString = new StringBuilder()
			.append("select a as actor, m as movie, p as person " +
					"from Actor a, Movie m, Person p " +
					"JOIN a.roles role ")
			.append("where ")
			.append("not (p.firstName='Kevin' and p.lastName='Bacon') ")
			.append("and m=role.movie ")
			.append("and p=a.person ")
			.append(String.format("and role.movie in (%s) ", 
						subqlString.toString()))
			.append("order by p.lastName ASC");
		log.debug(qlString.toString());
		TypedQuery<Tuple> lquery = em.createQuery(qlString.toString(), Tuple.class);
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Tuple> cqdef = cb.createQuery(Tuple.class);
		Root<Actor> a = cqdef.from(Actor.class);
		Root<Movie> m = cqdef.from(Movie.class);
		Root<Person> p = cqdef.from(Person.class);
		Join<Actor, MovieRole> role = a.join("roles");

		//build the subquery
		Subquery<Movie> sqdef = cqdef.subquery(Movie.class);
		Root<Movie> m2 = sqdef.from(Movie.class);
		Join<Movie,MovieRole> r2 = m2.join("cast");
		sqdef.select(m2)
		  .where(cb.equal(r2.get("actor").get("person").get("firstName"),"Kevin"),
				 cb.equal(r2.get("actor").get("person").get("lastName"),"Bacon"));
		  
			//build outer query
		cqdef.multiselect(a.alias("actor"),m.alias("movie"),p.alias("person"))
			.where(
				   cb.not(cb.and(
							cb.equal(p.get("firstName"),"Kevin"),
							cb.equal(p.get("lastName"), "Bacon")
							) //and
					), //not+and
				   cb.equal(m, role.get("movie")),
				   cb.equal(p, a.get("person")),
				   cb.in(m).value(sqdef)
				) //where
		     .orderBy(cb.asc(p.get("lastName")));
		TypedQuery<Tuple> cquery = em2.createQuery(cqdef);

		log.debug("execute JPAQL query");
		List<Tuple> lresults = lquery.getResultList();
		log.debug("accessing jpaql results");
		log.debug("jpaql results  =" + lresults);
		for (Tuple row: lresults) {
			log.debug("jpaql results  =" + row.get(0) + ", " + row.get(1));
		}
		
		log.debug("execute Criteria API query");
		List<Tuple> cresults = cquery.getResultList();		
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);
		for (Tuple row : cresults) {
			log.debug("critera results=" + row.get(0) + ", " + row.get(1));
		}

		log.debug("comparing query results");
		Iterator<Tuple> litr = lresults.iterator();
		Iterator<Tuple> citr = cresults.iterator();
		while (litr.hasNext() && citr.hasNext()) {
			Actor lactor = litr.next().get("actor",Actor.class);
			Actor cactor = citr.next().get("actor",Actor.class);
			assertTrue(
				String.format("different actors (%s) (%s)", lactor, cactor), 
				lactor.equals(cactor));
		}
		assertFalse("lopsided number of entities", 
				litr.hasNext() || citr.hasNext());
	}

	
	@Test
	public void testSubQuery2() {
		log.info("*** testSubQuery2 ***");
		//build JPAQL query
			//build the subquery
		StringBuilder subqlString = new StringBuilder()
			.append("select m from Movie m " +
					"JOIN m.cast role ")
			.append("where " +
					"role.actor.person.firstName = 'Kevin' " +
					"and role.actor.person.lastName = 'Bacon'");
			//build the outer query
		StringBuilder qlString = new StringBuilder()
			.append("select role as role, a as actor " +
					"from MovieRole role, Actor a " +
					"JOIN FETCH role.movie m " +
					"JOIN FETCH a.person p ")
			.append("where ")
			.append("not (p.firstName='Kevin' and p.lastName='Bacon') ")
			.append("and m=role.movie ")
			.append("and a=role.actor ")
			.append("and p=a.person ")
			.append(String.format("and role.movie in (%s) ", 
					subqlString.toString()))
			.append("order by p.lastName ASC");
		log.debug(qlString.toString());
		TypedQuery<Tuple> lquery = em.createQuery(
				qlString.toString(), Tuple.class);
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Tuple> cqdef = cb.createQuery(Tuple.class);
		Root<MovieRole> role = cqdef.from(MovieRole.class);
		Root<Actor> a = cqdef.from(Actor.class);
		Join<MovieRole,Movie> m = role.join("movie");
		Join<Actor,Person> p = a.join("person");
		role.fetch("movie");
		a.fetch("person");

		//build the subquery
		Subquery<Movie> sqdef = cqdef.subquery(Movie.class);
		Root<Movie> m2 = sqdef.from(Movie.class);
		Join<Movie,MovieRole> r2 = m2.join("cast");
		sqdef.select(m2)
		  .where(cb.equal(r2.get("actor").get("person").get("firstName"),"Kevin"),
				 cb.equal(r2.get("actor").get("person").get("lastName"),"Bacon"));
		  
			//build outer query
		cqdef.multiselect(role.alias("role"),a.alias("actor"))
			.where(
				   cb.not(cb.and(
							cb.equal(p.get("firstName"),"Kevin"),
							cb.equal(p.get("lastName"), "Bacon")
							) //and
					), //not+and
				   cb.equal(m, role.get("movie")),
				   cb.equal(a, role.get("actor")),
				   cb.equal(p, a.get("person")),
				   cb.in(m).value(sqdef)
				) //where
		     .orderBy(cb.asc(p.get("lastName")));
		TypedQuery<Tuple> cquery = em2.createQuery(cqdef);

		log.debug("execute JPAQL query");
		List<Tuple> lresults = lquery.getResultList();
		log.debug("accessing jpaql results");
		log.debug("jpaql results  =" + lresults);
		for (Tuple r: lresults) {
			log.debug("jpaql results  =" + 
					r.get("actor") + ", " + 
					r.get("role", MovieRole.class).getMovie());
		}
		
		log.debug("execute Criteria API query");
		List<Tuple> cresults = cquery.getResultList();		
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);
		for (Tuple row : cresults) {
			log.debug("critera results=" + 
					row.get("actor") + ", " + 
					row.get("role", MovieRole.class).getMovie());
		}

		log.debug("comparing query results");
		Iterator<Tuple> litr = lresults.iterator();
		Iterator<Tuple> citr = cresults.iterator();
		while (litr.hasNext() && citr.hasNext()) {
			Actor lactor = litr.next().get("actor", Actor.class);
			Actor cactor = citr.next().get("actor",Actor.class);
			assertTrue(String.format("different actors (%s) (%s)", lactor, cactor), 
					lactor.equals(cactor));
		}
		assertFalse("lopsided number of entities", litr.hasNext() || citr.hasNext());
	}


	/**
	 * The query above was purposely changed to demonstrate a correlated 
	 * sub-query. The previous approach determined which movies matched the 
	 * movies Kevin Bacon played in -- where the sub-query only had to execute 
	 * once since it did not depend on the outer query. In this approach a
	 * the sub-query must be executed for every row in the outer query since 
	 * we changed the algo to -- find which movies our actors have played in
	 * also had Kevin Bacon playing in them as well. In this later approach
	 * the sub-query depends on the movie of the outer loop but only returns 
	 * 0..1 rows. This is just example of what it means and how to do it -- and
	 * not a judgement on good/bad at this point.  
	 */
	@Test
	public void testCorrelatedSubQuery() {
		log.info("*** testCorrelatedSubQuery ***");
		//build JPAQL query
			//build the subquery
		StringBuilder subqlString = new StringBuilder()
			.append("select m2 from Movie m2 " +
					"JOIN m2.cast role ")
			.append("where " +
					"m2=m1 " +          //correlated subquery
					"and role.actor.person.firstName = 'Kevin' " +
					"and role.actor.person.lastName = 'Bacon'");
			//build the outer query
		StringBuilder qlString = new StringBuilder()
			.append("select role as role, a as actor " +
					"from MovieRole role, Actor a " +
					"JOIN FETCH role.movie as m1 " +
					"JOIN FETCH a.person p ")
			.append("where not (p.firstName='Kevin' and p.lastName='Bacon') " +
					"and m1=role.movie " +
					"and role.actor=a " + 
					"and a.person=p ")
			.append(String.format("and exists (%s)", subqlString.toString()))
			.append("order by p.lastName ASC");
		log.debug(qlString.toString());
		TypedQuery<Tuple> lquery = em.createQuery(
				qlString.toString(), Tuple.class);
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Tuple> cqdef = cb.createQuery(Tuple.class);
		Root<MovieRole> role = cqdef.from(MovieRole.class);
		Root<Actor> a = cqdef.from(Actor.class);
		Join<MovieRole,Movie> m = role.join("movie");
		Join<Actor,Person> p = a.join("person");
		role.fetch("movie");
		a.fetch("person");

		//build the subquery
		Subquery<Movie> sqdef = cqdef.subquery(Movie.class);
		Root<Movie> m2 = sqdef.from(Movie.class);
		Join<Movie,MovieRole> r2 = m2.join("cast");
		sqdef.select(m2)
		  .where(cb.equal(m2, m),     //correlated subquery
				 cb.equal(r2.get("actor").get("person").get("firstName"),"Kevin"),
				 cb.equal(r2.get("actor").get("person").get("lastName"),"Bacon"));
		  
			//build outer query
		cqdef.multiselect(role.alias("role"),a.alias("actor"))
			.where(
				   cb.not(cb.and(
						cb.equal(p.get("firstName"),"Kevin"),
						cb.equal(p.get("lastName"), "Bacon")
					)), //not+and
				   cb.equal(m, role.get("movie")),
				   cb.equal(a, role.get("actor")),
				   cb.equal(p, a.get("person")),
				   cb.exists(sqdef)
				) //where
		     .orderBy(cb.asc(p.get("lastName")));
		TypedQuery<Tuple> cquery = em2.createQuery(cqdef);

		log.debug("execute JPAQL query");
		List<Tuple> lresults = lquery.getResultList();
		log.debug("accessing jpaql results");
		log.debug("jpaql results  =" + lresults);
		for (Tuple r: lresults) {
			log.debug("jpaql results  =" + 
					r.get("actor") + ", " + 
					r.get("role", MovieRole.class).getMovie());
		}
		
		log.debug("execute Criteria API query");
		List<Tuple> cresults = cquery.getResultList();		
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);
		for (Tuple row : cresults) {
			log.debug("critera results=" + 
					row.get("actor") + ", " + 
					row.get("role", MovieRole.class).getMovie());
		}

		log.debug("comparing query results");
		Iterator<Tuple> litr = lresults.iterator();
		Iterator<Tuple> citr = cresults.iterator();
		while (litr.hasNext() && citr.hasNext()) {
			Actor lactor = litr.next().get("actor", Actor.class);
			Actor cactor = citr.next().get("actor",Actor.class);
			assertTrue(String.format("different actors (%s) (%s)", lactor, cactor), 
					lactor.equals(cactor));
		}
		assertFalse("lopsided number of entities", litr.hasNext() || citr.hasNext());
	}

	/**
	 * This test demonstrates a small improvement/simplification to the 
	 * correlated join -- but a compexity to the Criteria API expression.
	 * We eliminated separate select on Movie in the subquery and the 
	 * evaluation of m1=m2. Instead we used the Movie from the outer query 
	 * directly within the sub-query -- saving an evaluation. This, however,
	 * caused us to use the Subquery.correlate() method to base the from()
	 * in the sub-query on a property in the outer query.
	 */
	@Test
	public void testCorrelatedSubQuery2() {
		log.info("*** testCorrelatedSubQuery2 ***");
		//build JPAQL query
			//build the subquery
		StringBuilder subqlString = new StringBuilder()
			.append("select role " +
					"from m.cast role ")  //correlated subquery
			.append("where " +
					"role.actor.person.firstName = 'Kevin' " +
					"and role.actor.person.lastName = 'Bacon'");
		//build the outer query
		StringBuilder qlString = new StringBuilder()
			.append("select role as role, a as actor " +
					"from MovieRole role, Actor a " +
					"JOIN FETCH role.movie as m " +
					"JOIN FETCH a.person p ")
			.append("where not (p.firstName='Kevin' and p.lastName='Bacon') " +
					"and m=role.movie " +
					"and role.actor=a " + 
					"and a.person=p ")
			.append(String.format("and exists (%s)", subqlString.toString()))
			.append("order by p.lastName ASC");
		log.debug(qlString.toString());
		TypedQuery<Tuple> lquery = em.createQuery(
				qlString.toString(), Tuple.class);
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Tuple> cqdef = cb.createQuery(Tuple.class);
		Root<MovieRole> role = cqdef.from(MovieRole.class);
		Root<Actor> a = cqdef.from(Actor.class);
		Join<MovieRole,Movie> m = role.join("movie");
		Join<Actor,Person> p = a.join("person");
		role.fetch("movie");
		a.fetch("person");

		//build the subquery
		Subquery<MovieRole> sqdef = cqdef.subquery(MovieRole.class);
		Join<MovieRole,Movie> m2 = sqdef.correlate(m);  //correlated subquery
		Join<Movie,MovieRole> r2 = m2.join("cast");
		sqdef.select(r2)
		  .where(
				 cb.equal(r2.get("actor").get("person").get("firstName"),"Kevin"),
				 cb.equal(r2.get("actor").get("person").get("lastName"),"Bacon"));
		  
			//build outer query
		cqdef.multiselect(role.alias("role"),a.alias("actor"))
			.where(
				   cb.not(cb.and(
						cb.equal(p.get("firstName"),"Kevin"),
						cb.equal(p.get("lastName"), "Bacon")
					)), //not+and
				   cb.equal(m, role.get("movie")),
				   cb.equal(a, role.get("actor")),
				   cb.equal(p, a.get("person")),
				   cb.exists(sqdef)
				) //where
		     .orderBy(cb.asc(p.get("lastName")));
		TypedQuery<Tuple> cquery = em2.createQuery(cqdef);

		log.debug("execute JPAQL query");
		List<Tuple> lresults = lquery.getResultList();
		log.debug("accessing jpaql results");
		log.debug("jpaql results  =" + lresults);
		for (Tuple r: lresults) {
			log.debug("jpaql results  =" + 
					r.get("actor") + ", " + 
					r.get("role", MovieRole.class).getMovie());
		}
		
		log.debug("execute Criteria API query");
		List<Tuple> cresults = cquery.getResultList();		
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);
		for (Tuple row : cresults) {
			log.debug("critera results=" + 
					row.get("actor") + ", " + 
					row.get("role", MovieRole.class).getMovie());
		}

		log.debug("comparing query results");
		Iterator<Tuple> litr = lresults.iterator();
		Iterator<Tuple> citr = cresults.iterator();
		while (litr.hasNext() && citr.hasNext()) {
			Actor lactor = litr.next().get("actor", Actor.class);
			Actor cactor = citr.next().get("actor",Actor.class);
			assertTrue(String.format("different actors (%s) (%s)", lactor, cactor), 
					lactor.equals(cactor));
		}
		assertFalse("lopsided number of entities", litr.hasNext() || citr.hasNext());
	}

	/**
	 * This test method demonstrates the use of the in() operator. We used 
	 * it earlier in the first subquery. This time we are expressing literal
	 * values.
	 */
	@Test
	public void testIn() {
		log.info("*** testIn ***");
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select distinct m from Movie m " +
					"JOIN m.genres genre " +
					"JOIN FETCH m.genres ")
			.append("where genre in ('Drama', 'Comedy') ")
			.append("order by m.releaseDate ASC");
		log.debug(qlString.toString());
		TypedQuery<Movie> lquery = em.createQuery(
				qlString.toString(), Movie.class);
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Movie> cqdef = cb.createQuery(Movie.class);
		Root<Movie> m = cqdef.from(Movie.class);
		Join<Movie,String> genre = m.join("genres");
		m.fetch("genres");
		cqdef.select(m).distinct(true)
			.where( //value per-menthod
					cb.in(genre).value("Drama").value("Comedy"),
					//shorthand
					genre.in("Drama", "Comedy")
				) 
		     .orderBy(cb.asc(m.get("releaseDate")));
		TypedQuery<Movie> cquery = em2.createQuery(cqdef);

		log.debug("execute JPAQL query");
		List<Movie> lresults = lquery.getResultList();
		log.debug("accessing jpaql results");
		log.debug("jpaql results  =" + lresults);
		for (Movie r: lresults) {
			log.debug("jpaql results  =" +  
					r + ", " + 
					r.getGenres());
		}
		
		log.debug("execute Criteria API query");
		List<Movie> cresults = cquery.getResultList();		
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);
		for (Movie r : cresults) {
			log.debug("critera results=" + 
					r + ", " + 
					r.getGenres());
		}

		log.debug("comparing query results");
		Iterator<Movie> litr = lresults.iterator();
		Iterator<Movie> citr = cresults.iterator();
		while (litr.hasNext() && citr.hasNext()) {
			Movie lm = litr.next();
			Movie cm = citr.next();
			assertTrue(String.format("different movies (%s) (%s)", lm, cm), 
					lm.getTitle().equals(cm.getTitle()));
			assertEquals("unexpected genres", lm.getGenres(), cm.getGenres());
		}
		assertFalse("lopsided number of entities", litr.hasNext() || citr.hasNext());
	}

	/**
	 * This test method provides an example of using a case statement to control
	 * the query output based on values encountered within the results.
	 */
	@Test
	public void testCase() {
		log.info("*** testCase ***");
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select p.firstName, p.lastName, case " +
					"when p.birthDate between '1920-01-01' and '1929-12-31' THEN 20 " +
					"when p.birthDate between '1930-01-01' and '1939-12-31' THEN 30 " +
					"when p.birthDate between '1940-01-01' and '1949-12-31' THEN 40 " +
					"when p.birthDate between '1950-01-01' and '1959-12-31' THEN 50 " +
					"else '-1' " +
					"end ")
			.append("from Person p " +
					"order by lastName ASC");
		log.debug(qlString.toString());
		TypedQuery<Object[]> lquery = em.createQuery(
				qlString.toString(), Object[].class);
		
		//build criteria API query
		CriteriaBuilder cb = em2.getCriteriaBuilder();
		CriteriaQuery<Object[]> cqdef = cb.createQuery(Object[].class);
		Root<Person> p = cqdef.from(Person.class);
		cqdef.multiselect(p.get("firstName"), p.get("lastName"), 
			cb.selectCase()
				.when(cb.between(p.<Date>get("birthDate"), 
					cb.literal(new GregorianCalendar(1920, 
							Calendar.JANUARY, 1).getTime()), 
					cb.literal(new GregorianCalendar(1929, 
							Calendar.DECEMBER,31).getTime())), 20)
				.when(cb.between(p.<Date>get("birthDate"), 
					cb.literal(new GregorianCalendar(1930, 
							Calendar.JANUARY, 1).getTime()), 
					cb.literal(new GregorianCalendar(1939, 
							Calendar.DECEMBER,31).getTime())), 30)
				.when(cb.between(p.<Date>get("birthDate"), 
					cb.literal(new GregorianCalendar(1940, 
							Calendar.JANUARY, 1).getTime()), 
					cb.literal(new GregorianCalendar(1949, 
							Calendar.DECEMBER,31).getTime())), 40)
				.when(cb.between(p.<Date>get("birthDate"), 
					cb.literal(new GregorianCalendar(1950, 
							Calendar.JANUARY, 1).getTime()), 
					cb.literal(new GregorianCalendar(1959, 
							Calendar.DECEMBER,31).getTime())), 50)
				.otherwise(-1))
	     .orderBy(cb.asc(p.get("lastName")));
		TypedQuery<Object[]> cquery = em2.createQuery(cqdef);

		log.debug("execute JPAQL query");
		List<Object[]> lresults = lquery.getResultList();
		log.debug("accessing jpaql results");
		log.debug("jpaql results  =" + lresults);
		for (Object[] r: lresults) {
			log.debug("jpaql results  =" +  
					r[0] + " " + r[1] + " born in " + r[2] +"s");
		}
		
		log.debug("execute Criteria API query");
		List<Object[]> cresults = cquery.getResultList();		
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);
		for (Object[] r : cresults) {
			log.debug("criteria results=" +  
					r[0] + " " + r[1] + " born in " + r[2] +"s");
		}

		log.debug("comparing query results");
		Iterator<Object[]> litr = lresults.iterator();
		Iterator<Object[]> citr = cresults.iterator();
		while (litr.hasNext() && citr.hasNext()) {
			assertEquals("unexpected dates", litr.next()[2], citr.next()[2]);
		}
		assertFalse("lopsided number of entities", litr.hasNext() || citr.hasNext());
	}
	
	/**
	 * This test provides a quick sanity check of using a multiselect over a 
	 * FETCH JOIN
	 */
	@Test
	public void testFetch() {
		log.debug("*** testFetch ***");
		
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select m as movie, role as role, a as actor, p as person " +
		            "from Movie m, MovieRole role, Actor a, Person p ")
			.append("where role.movie=m " +
					"and role.actor=a " +
					"and a.person=p " +
					"order by m.releaseDate ASC");
		log.debug(qlString.toString());
		TypedQuery<Tuple> lquery = em.createQuery(
				qlString.toString(), Tuple.class);

		log.debug("execute JPAQL query");
		List<Tuple> lresults = lquery.getResultList();
		em.close();
		log.debug("accessing jpaql results");
		log.debug("jpaql results  =" + lresults);
		for (Tuple r: lresults) {
			log.debug("jpaql results  =" + r.get("movie"));  
		}
	}

	@Test
	public void testCoalesce() {
		log.debug("*** testCoalesce ***");
		
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select coalesce(m.id, m.title) ")
			.append("from Movie m " +
					"order by m.releaseDate ASC");
		log.debug(qlString.toString());
		TypedQuery<Tuple> lquery = em.createQuery(
				qlString.toString(), Tuple.class);

		log.debug("execute JPAQL query");
		List<Tuple> lresults = lquery.getResultList();
		log.debug("accessing jpaql results");
		log.debug("jpaql results  =" + lresults);
		for (Tuple r: lresults) {
			log.debug("jpaql results  =" + r.get(0));  
		}
	}
}
