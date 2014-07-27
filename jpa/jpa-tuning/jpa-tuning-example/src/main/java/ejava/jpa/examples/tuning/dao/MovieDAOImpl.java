package ejava.jpa.examples.tuning.dao;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.jpa.examples.tuning.bo.Actor;
import ejava.jpa.examples.tuning.bo.Movie;
import ejava.jpa.examples.tuning.bo.MovieRating;
import ejava.jpa.examples.tuning.bo.MovieRole;
import ejava.jpa.examples.tuning.bo.Person;

public class MovieDAOImpl {
	private static final Log log = LogFactory.getLog(MovieDAOImpl.class);
	private EntityManager em;
	
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	private class DateParam {
		public final Date date;
		public final TemporalType ttype;
		public DateParam(Date date, TemporalType ttype) {
			this.date = date;
			this.ttype = ttype;
		}
		@Override
		public String toString() {
			if (date==null) { return null; }
			switch (ttype) {
			case DATE:
				return new SimpleDateFormat("yyyy-MM-dd").format(date);
			case TIME:
				return new SimpleDateFormat("HH:mm:ss.SSS").format(date);
			case TIMESTAMP:
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date);
			}
			return null;
		}
	}
	private class QueryLogger<T> {
		private Class<T> resultType;
		private String jpaql;
		private String orderBy;
		private Integer offset;
		private Integer limit;
		private Map<String, Object> params = new HashMap<String, Object>();
		
		public QueryLogger(String jpaql, Class<T> resultType) {
			this.resultType = resultType;
			this.jpaql = jpaql;
		}
		public QueryLogger<T> setParameter(String key, Object value) {
			params.put(key, value);
			return this;
		}
		public QueryLogger<T> setParameter(String key, Date value, TemporalType ttype) {
			params.put(key, new DateParam(value, ttype));
			return this;
		}
		public QueryLogger<T> setFirstResult(int offset) {
			this.offset = offset;
			return this;
		}
		public QueryLogger<T> setMaxResults(int limit) {
			this.limit=limit;
			return this;
		}
		public void setOrderBy(String orderBy) {
			this.orderBy = orderBy;
		}
		public T getSingleResult() {
			log.info(toString());
			return makeQuery().getSingleResult();
		}
		public List<T> getResultList() {
			log.info(toString());
			return makeQuery().getResultList();
		}
		protected TypedQuery<T> makeQuery() {
			String queryString = orderBy==null ? jpaql : jpaql + " order by " + orderBy;
			TypedQuery<T> query = em.createQuery(queryString, resultType);
			for (Entry<String, Object> param: params.entrySet()) {
				if (param.getValue() instanceof DateParam) {
					DateParam dparam = (DateParam)param.getValue();
					query.setParameter(param.getKey(), dparam.date, dparam.ttype);
				} else {
					query.setParameter(param.getKey(), param.getValue());
				}
			}
			if (offset != null) {
				query.setFirstResult(offset);
			}
			if (limit != null) {
				query.setMaxResults(limit);
			}
			return query;
		}
		public String toString() {
			StringBuilder text = new StringBuilder();
			text.append("\"").append(jpaql);
			if (orderBy != null) {
				text.append(" order by ").append(orderBy);
			}
			text.append("\"");
			if (!params.isEmpty()) {
				text.append(", params=").append(params);
			}
			if (offset != null) {
				text.append(", offset=").append(offset);
			}
			if (limit != null) {
				text.append(", limit=").append(limit);
			}
			return text.toString();
		}
	}
	
	/**
	 * Helper method to log the JPAQL portion of the query.
	 * @param jpaql
	 * @param resultClass
	 * @return
	 */
	protected <T> QueryLogger<T> createQuery(String jpaql, Class<T> resultClass) {
		return new QueryLogger<T>(jpaql, resultClass);
	}
	
	/**
	 * Helper method to add paging parameters to a query
	 * @param query
	 * @param offset
	 * @param limit
	 * @return
	 */
	protected <T> QueryLogger<T> withPaging(QueryLogger<T> query, Integer offset, Integer limit, String orderBy) {
    	if (offset != null && offset > 0) {
    		query.setFirstResult(offset);
    	}
    	if (limit != null && limit > 0) {
    		query.setMaxResults(limit);
    	}
    	if (orderBy != null) {
    		query.setOrderBy(orderBy);
    	}
    	return query;
	}

	/**
	 * Returns a Person instance for the Kevin Bacon actor who
	 * played in Tremors.
	 * @return
	 */
	public Person getKevinBacon() {
    	return createQuery(
    			"select r.actor.person " +
    			"from MovieRole r " +
    			"where r.movie.title = 'Tremors' and " +
    			"r.actor.person.lastName='Bacon' and " +
    			"r.actor.person.firstName='Kevin'", Person.class)
    			.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getKevinBaconMovieIds(Integer offset, Integer limit, String orderBy) {
		TypedQuery<String> query = (TypedQuery<String>) em.createNativeQuery(
			String.format(
				"select m.id from JPATUNE_MOVIE m " +
				"join JPATUNE_MOVIEROLE role on role.movie_id = m.id " +
				"join JPATUNE_ACTOR a on a.person_id = role.actor_id " +
				"join JPATUNE_PERSON p on p.id = a.person_id " +
				"where p.last_name='Bacon' and p.first_name='Kevin' and m.plot is not null " +
				"%s", orderBy==null ? "" : " order by " + orderBy)
				);
		if (offset!=null) {
			query.setFirstResult(offset);
		}
		if (limit!=null) {
			query.setMaxResults(limit);
		}
		return query.getResultList();
	}

	/**
	 * Find people who are 1 step from Kevin Bacon.
	 * @param p
	 * @param offset
	 * @param limit
	 * @return
	 */
    public List<Person> oneStepFromPerson0(Person p, Integer offset, Integer limit) {
    	return withPaging(createQuery(
			"select a.person from Actor a " +
			"join a.roles ar " +
			"join a.person ap " +
			"where ar.movie in (select m from Movie m " +
			    "inner join m.cast mr " +
		        "inner join mr.actor ma " +
		        "inner join ma.person mp " +
			    "where mp.id = :id))" +
			 "and ap.id not = :id", Person.class)
			 .setParameter("id", p.getId()), offset, limit, null)
			.getResultList();
    }
    
	/**
	 * Find people who are 1 step from Person.
	 * @param p
	 * @param offset
	 * @param limit
	 * @param orderBy
	 * @return
	 */
    public Collection<Person> oneStepFromPersonByDAO(Person p) {
    	Collection<Person> result = new HashSet<Person>();
    	//performing core query
    	List<String> movieIds = createQuery(
    			"select role.movie.id from MovieRole role " +
    			"where role.actor.person.id=:personId", String.class)
    			.setParameter("personId", p.getId())
    			.getResultList();
    	
    	//loop through results and issue sub-queries
    	for (String mid: movieIds) {
    	    List<Person> people = createQuery(
    	    		"select role.actor.person from MovieRole role " +
    	    		"where role.movie.id=:movieId", Person.class)
    	    		.setParameter("movieId", mid)
    	    		.getResultList();
    		result.addAll(people);
    	}
    	return result;
    }    
    public Collection<Person> oneStepFromPersonByDAO(Person p, Integer offset, Integer limit, String orderBy) {
    	Collection<Person> result = new HashSet<Person>();
    	//performing core query
    	List<String> movieIds = createQuery(
    			"select role.movie.id from MovieRole role " +
    			"where role.actor.person.id=:personId", String.class)
    			.setParameter("personId", p.getId())
    			.getResultList();
    	
    	//loop through results and issue sub-queries
    	int pos=0;
    	for (String mid: movieIds) {
    	    List<Person> people = createQuery(
    	    		"select role.actor.person from MovieRole role " +
    	    		"where role.movie.id=:movieId", Person.class)
    	    		.setParameter("movieId", mid)
    	    		.getResultList();
    	    if (offset==null || (offset!=null && pos+people.size() > offset)) {
    	    	for(Person pp: people) {
    	    		if (offset==null || pos>=offset) {
        	    		result.add(pp);
        	    	    if (limit!=null && result.size() >= limit) { break; }
    	    		}
    	    		pos+=1; 
    	    	}
    	    } else {
    	    	pos+=people.size();
    	    }
    	    if (limit!=null && result.size() >= limit) { break; }
    	}
    	return result;
    }

	/**
	 * Find people who are 1 step from Person.
	 * @param p
	 * @param offset
	 * @param limit
	 * @param orderBy
	 * @return
	 */
    public List<Person> oneStepFromPersonByDB(Person p) {
    	return createQuery(
    			"select distinct role.actor.person from MovieRole role " +
    			"where role.movie.id in (" +
    			    "select m.id from Movie m " +
    			    "join m.cast role2 " +
    			    "where role2.actor.person.id=:id)", Person.class)
   			 .setParameter("id", p.getId())
    	     .getResultList();
    }
    public List<Person> oneStepFromPersonByDB(Person p, Integer offset, Integer limit, String orderBy) {
    	return withPaging(createQuery(
    			"select distinct role.actor.person from MovieRole role " +
    			"where role.movie.id in (" +
    			    "select m.id from Movie m " +
    			    "join m.cast role2 " +
    			    "where role2.actor.person.id=:id)", Person.class), offset, limit, orderBy)
   			 .setParameter("id", p.getId())
    	     .getResultList();
    }

    
    
    protected static class Pair<T,U> {
    	public final T query;   //main part of query
    	public final U queryTerm; //where queryTerm ...
    	public Pair(T first, U second) { this.query=first; this.queryTerm=second; }
    }

    //find Movies person acted in
    protected Pair<Subquery<Movie>,Void> getMoviesForPerson(AbstractQuery<String> parentQuery, Person person) {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
    	Subquery<Movie> qdef = parentQuery.subquery(Movie.class);
    	Root<Movie> m = qdef.from(Movie.class);
    	Join<Movie, MovieRole> role = m.join("cast", JoinType.INNER);
    	Join<MovieRole, Actor> a = role.join("actor", JoinType.INNER);
    	Join<Actor, Person> p = a.join("person", JoinType.INNER);
    	qdef.select(m)
    	  .where(cb.equal(p.get("id"), person.getId()));
    	return new Pair<Subquery<Movie>,Void>(qdef,null);
    }

    //find Movies people acted in
    protected Pair<Subquery<Movie>, Join<Actor, Person>> getMoviesForPersonIds(AbstractQuery<String> parentQuery) {
    	Subquery<Movie> qdef = parentQuery.subquery(Movie.class);
    	Root<Movie> m = qdef.from(Movie.class);
    	Join<Movie, MovieRole> role = m.join("cast", JoinType.INNER);
    	Join<MovieRole, Actor> a = role.join("actor", JoinType.INNER);
    	Join<Actor, Person> p = a.join("person", JoinType.INNER);
    	qdef.select(m).distinct(true);
    	
    	//qdef.where(cb.in(p.get("id")).value(subq));
    	return new Pair<Subquery<Movie>,Join<Actor, Person>>(qdef, p);
    }

    //find People.id who acted in Movies                      
    protected Pair<Subquery<String>,Join<MovieRole, Movie>> getPersonIdsInMovie(AbstractQuery<?> parentQuery) {
    	Subquery<String> qdef = parentQuery.subquery(String.class);    	
    	Root<MovieRole> role = qdef.from(MovieRole.class);    	
    	Join<MovieRole, Movie> m = role.join("movie", JoinType.INNER);
    	Join<MovieRole, Actor> a = role.join("actor", JoinType.INNER);
    	Join<Actor, Person> p = a.join("person", JoinType.INNER);
    	qdef.select(p.<String>get("id")).distinct(true);
    	
    	//qdef.where(cb.in(m).value(subq));
    	return new Pair<Subquery<String>,Join<MovieRole, Movie>>(qdef,m);
    }

    protected Pair<Subquery<String>,Join<MovieRole, Movie>> nthRemoved(
    		AbstractQuery<String> parentQuery, Path<Movie> parentTerm, Person person) {
    	CriteriaBuilder cb = em.getCriteriaBuilder();

    	//subquery that returns Movies for Person.ids
		Pair<Subquery<Movie>,Join<Actor, Person>> mq = getMoviesForPersonIds(parentQuery);
		parentQuery.where(cb.in(parentTerm).value(mq.query));
    	
		//subquery that returns Person.ids for Movies
		Pair<Subquery<String>,Join<MovieRole, Movie>> pq = getPersonIdsInMovie(mq.query);
    	mq.query.where(cb.in(mq.queryTerm.get("id")).value(pq.query));

		return pq;
    }
    
    protected CriteriaQuery<Person> getPeopleQuery2(Person person, int steps) {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
    	CriteriaQuery<Person> rootq = cb.createQuery(Person.class);
    	Root<Person> p = rootq.from(Person.class);
    	rootq.select(p);

    	//subquery that returns Person.ids for actors in Movies
    	Pair<Subquery<String>,Join<MovieRole, Movie>> associateIds=getPersonIdsInMovie(rootq);
    	rootq.where(cb.in(p.get("id")).value(associateIds.query));

    	//loop for each step-1
		Pair<Subquery<String>,Join<MovieRole, Movie>> nextParent=associateIds;
    	for (int i=0; i<steps-1; i++) {
    	    nextParent = nthRemoved(nextParent.query, nextParent.queryTerm, person);
    	}
    	
		//subquery that returns Movies for target Person
		Pair<Subquery<Movie>, Void> targetMovies = getMoviesForPerson(nextParent.query, person);
		nextParent.query.where(cb.in(nextParent.queryTerm).value(targetMovies.query));
    	
    	return rootq;
    }
    
    
    
    protected CriteriaQuery<Person> getPeopleQuery(Person person, int steps) {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
    	CriteriaQuery<Person> rootq = cb.createQuery(Person.class);
    	Root<Person> p = rootq.from(Person.class);
    	rootq.select(p);
    	
		//3rd removed
    	//subquery that returns Person.ids for Movies
		Pair<Subquery<String>,Join<MovieRole, Movie>> pq3 = getPersonIdsInMovie(rootq);
    	rootq.where(cb.in(p.get("id")).value(pq3.query));
    	
    	//subquery that returns Movies for Person.ids
		Pair<Subquery<Movie>,Join<Actor, Person>> mq2 = getMoviesForPersonIds(pq3.query);
		pq3.query.where(cb.in(pq3.queryTerm).value(mq2.query));
    	
		//2nd removed
    	//subquery that returns Person.ids for Movies
		Pair<Subquery<String>,Join<MovieRole, Movie>> pq2 = getPersonIdsInMovie(mq2.query);
    	mq2.query.where(cb.in(mq2.queryTerm.get("id")).value(pq2.query));

    	//subquery that returns Movies for Person.ids
		Pair<Subquery<Movie>,Join<Actor, Person>> mq1 = getMoviesForPersonIds(pq2.query);
		pq2.query.where(cb.in(pq2.queryTerm).value(mq1.query));
    	
    	//1st removed
		//subquery that returns Person.ids for Movies
		Pair<Subquery<String>,Join<MovieRole, Movie>> pq1 = getPersonIdsInMovie(mq1.query);
    	mq1.query.where(cb.in(mq1.queryTerm.get("id")).value(pq1.query));
    	
		//subquery that returns Movies for target Person
		Pair<Subquery<Movie>, Void> tq = getMoviesForPerson(pq1.query, person);
		pq1.query.where(cb.in(pq1.queryTerm).value(tq.query));
    	
    	return rootq;
    }
    
    public List<Person> stepsFromPerson(Person person, int steps, Integer offset, Integer limit) {
    	CriteriaQuery<Person> qdef= getPeopleQuery2(person, steps);
    	TypedQuery<Person> query = em.createQuery(qdef);
    	if (offset!=null) { query.setFirstResult(offset); }
    	if (limit!=null)  { query.setMaxResults(limit); }
    	return query.getResultList();
    }
    
    /**
     * Returns a bulk, unordered page of movies. This will cause a full
     * table scan since there is no reason to consult the index.
     * @param offset
     * @param limit
     * @param orderBy
     * @return
     */
	public List<Movie> getMovies(Integer offset, Integer limit, String orderBy) {
		return withPaging(createQuery(
				"select m from Movie m", Movie.class), 
				offset, limit, orderBy).getResultList();
	}
	
	/**
	 * Returns an unordered page of movies matching the supplied rating -- but
	 * calling upper() on the DB value. This will cause an index to be bypassed 
	 * except for an upper() function index.
	 * @param rating
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<Movie> getMoviesByRatingUpperFunction(MovieRating rating, Integer offset, Integer limit) {
		return withPaging(createQuery(
				"select m from Movie m " +
				"where upper(m.rating) = :rating", Movie.class)
				.setParameter("rating", rating.mpaa().toUpperCase()), 
				offset, limit, null).getResultList();
	}

	/**
	 * Returns an unordered page of movies matching the supplied rating -- but
	 * calling lower() on the DB value. This will cause an index to be bypassed
	 * except for a lower() function index.
	 * @param rating
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<Movie> getMoviesByRatingLowerFunction(MovieRating rating, Integer offset, Integer limit) {
		return withPaging(createQuery(
				"select m from Movie m " +
				"where lower(m.rating) = :rating", Movie.class)
				.setParameter("rating", rating.mpaa().toLowerCase()), 
				offset, limit, null).getResultList();
	}

	/**
	 * Returns an unordered page of movies matching the supplied rating --
	 * without calling any function()s on the stored data. If the column 
	 * contains an index, it will be used. 
	 * @param rating
	 * @param offset
	 * @param limit
	 * @param orderBy
	 * @return
	 */
	public List<Movie> getMoviesByRatingValue(MovieRating rating, Integer offset, Integer limit, String orderBy) {
		return withPaging(createQuery(
				"select m from Movie m " +
				"where m.rating = :rating", Movie.class)
				.setParameter("rating", rating.mpaa()), 
				offset, limit, orderBy).getResultList();
	}

	/**
	 * Returns an unordered page of movies that have a title "like" the one passed
	 * in. Note the difference in index behavior when there is a wildcard at the 
	 * beginning or end of the searched title
	 * @param title
	 * @param offset
	 * @param limit
	 * @param orderBy
	 * @return
	 */
	public List<Movie> getMoviesLikeTitle(String title, Integer offset, Integer limit, String orderBy) {
		return withPaging(createQuery(
				"select m from Movie m " +
				"where m.title like :title", Movie.class)
				.setParameter("title", title), 
				offset, limit, orderBy).getResultList();
	}

	/**
	 * Returns movies exactly matching the provided title.
	 * @param title
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<Movie> getMoviesEqualsTitle(String title, Integer offset, Integer limit) {
		return withPaging(createQuery(
				"select m from Movie m " +
				"where m.title = :title", Movie.class)
				.setParameter("title", title), 
				offset, limit, null).getResultList();
	}

	/**
	 * Returns a list of ratings for movies that exactly match the provided title.
	 * @param title
	 * @param offset
	 * @param limit
	 * @param orderBy
	 * @return
	 */
	public List<String> getRatingsByTitle(String title, Integer offset, Integer limit, String orderBy) {
		return withPaging(createQuery(
				"select m.rating from Movie m " +
				"where m.title = :title", String.class)
				.setParameter("title", title), 
				offset, limit, orderBy).getResultList();
	}

	/**
	 * Returns ratings that match the title like criteria.
	 * @param title
	 * @param offset
	 * @param limit
	 * @param orderBy
	 * @return
	 */
	public List<String> getRatingsLikeTitle(String title, Integer offset, Integer limit, String orderBy) {
		return withPaging(createQuery(
				"select m.rating from Movie m " +
				"where m.title like :title", String.class)
				.setParameter("title", title), 
				offset, limit, orderBy).getResultList();
	}

	/**
	 * Returns an unordered page of titles that match a specified rating. This query
	 * will be impacted by a presence of an index on the rating column and the presence
	 * of the title column with the rating column as a part of a composite index.
	 * @param rating
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<String> getTitlesByRating(MovieRating rating, Integer offset, Integer limit) {
		return withPaging(createQuery(
				"select m.title from Movie m " +
				"where m.rating = :rating", String.class)
				.setParameter("rating", rating.mpaa()), 
				offset, limit, null).getResultList();
	}


	public List<Movie> getMoviesByTitleAndReleaseDate(String title, Date releaseDate, Integer offset, Integer limit) {
		return withPaging(createQuery(
				"select m from Movie m " +
				"where m.title = :title and m.releaseDate = :releaseDate", Movie.class)
				.setParameter("title", title) 
				.setParameter("releaseDate", releaseDate, TemporalType.DATE),
				offset, limit, null).getResultList();
	}

	public List<Movie> getMoviesByReleaseDateAndTitle(String title, Date releaseDate, Integer offset, Integer limit) {
		return withPaging(createQuery(
				"select m from Movie m " +
				"where m.releaseDate = :releaseDate and m.title = :title", Movie.class)
				.setParameter("title", title)
				.setParameter("releaseDate", releaseDate, TemporalType.DATE),
				offset, limit, null).getResultList();
	}

	public List<Movie> getMoviesByTitleAndReleaseDateAndRating(String title, Date releaseDate, MovieRating rating, Integer offset, Integer limit) {
		return withPaging(createQuery(
				"select m from Movie m " +
				"where m.title = :title and m.releaseDate = :releaseDate and m.rating=:rating", Movie.class)
				.setParameter("title", title) 
				.setParameter("releaseDate", releaseDate, TemporalType.DATE)
				.setParameter("rating", rating.mpaa()),
				offset, limit, null).getResultList();
	}

	public List<Movie> getMoviesByReleaseDate(Date releaseDate, Integer offset, Integer limit) {
		return withPaging(createQuery(
				"select m from Movie m " +
				"where m.releaseDate = :releaseDate", Movie.class)
				.setParameter("releaseDate", releaseDate, TemporalType.DATE),
				offset, limit, null).getResultList();
	}

	public List<Movie> getMoviesByReleaseDateAndRating(Date releaseDate, MovieRating rating, Integer offset, Integer limit) {
		return withPaging(createQuery(
				"select m from Movie m " +
				"where m.releaseDate = :releaseDate and m.rating=:rating", Movie.class)
				.setParameter("releaseDate", releaseDate, TemporalType.DATE)
				.setParameter("rating", rating.mpaa()),
				offset, limit, null).getResultList();
	}
	
	public List<Movie> getMoviesByRole(String role, Integer offset, Integer limit, String orderBy) {
		return withPaging(createQuery(
				"select m from Movie m " +
				"join m.cast as r " +
				"where r.role=:role", Movie.class)
				.setParameter("role", role),
				offset, limit, orderBy).getResultList();
	}

	public List<Movie> getMoviesByLikeRole(String role, Integer offset, Integer limit, String orderBy) {
		return withPaging(createQuery(
				"select m from Movie m " +
				"join m.cast as r " +
				"where r.role like :role", Movie.class)
				.setParameter("role", role),
				offset, limit, orderBy).getResultList();
	}

	public List<MovieRole> getRolesByMovie(String title, Date releaseDate, Integer offset, Integer limit, String orderBy) {
		return withPaging(createQuery(
				"select r from MovieRole r " +
				"join r.movie m " +
				"where m.title=:title and m.releaseDate=:releaseDate", MovieRole.class)
				.setParameter("title", title)
				.setParameter("releaseDate", releaseDate, TemporalType.DATE),
				offset, limit, orderBy).getResultList();
	}

	public Movie getMovieById(String id) {
		return em.find(Movie.class, id);
	}
	
	public Movie getMovieByIdUnfetched(String id) {
		List<Movie> movies=createQuery(
				String.format("select new %s(m.id, m.minutes, m.rating, m.releaseDate, m.title) ", Movie.class.getName()) +
				"from Movie m " +
				"where id=:id", Movie.class)
				.setParameter("id", id)
				.getResultList();
		return movies.isEmpty() ? null : movies.get(0);
	}
	
	public Movie getMovieFetchedByIdFetched(String id) {
		List<Movie> movies = createQuery(
				"select m from Movie m " +
				"left join fetch m.genres " +
				"left join fetch m.director d " +
				"left join fetch d.person " +
				"left join fetch m.cast role " +
				"left join fetch role.actor a " +
				"left join fetch a.person " +
				"where m.id=:id", Movie.class)
				.setParameter("id", id)
				.getResultList();
		return movies.isEmpty() ? null : movies.get(0);
	}
	
	public int getMovieCastCountByDAORelation(String movieId) {
		Movie m = em.find(Movie.class, movieId);
		return m==null ? 0 : m.getCast().size();
	}

	public int getMovieCastCountByDAO(String movieId) {
		return createQuery(
				"select role " +
				"from Movie m " +
				"join m.cast role " +
				"where m.id=:id", MovieRole.class)
				.setParameter("id", movieId)
				.getResultList().size();
	}
	
	public int getMovieCastCountByDB(String movieId) {
		return createQuery(
				"select count(role) " +
				"from Movie m " +
				"join m.cast role " +
				"where m.id=:id", Number.class)
				.setParameter("id", movieId)
				.getSingleResult().intValue();
	}

	public int getCastCountForMovie(String movieId) {
		return createQuery(
				"select count(*) " +
				"from MovieRole role " +
				"where role.movie.id=:id", Number.class)
				.setParameter("id", movieId)
				.getSingleResult().intValue();
	}

	public List<Object[]> getMovieAndDirector(Integer offset, Integer limit, String orderBy) {
		return withPaging(createQuery(
				"select m.title, p.firstName, p.lastName " +
				"from Movie m " +
				"join m.director d " +
				"join d.person p", Object[].class),
				offset, limit, orderBy)
				.getResultList();
	}
	
	
	
	
}
