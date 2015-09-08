package ejava.projects.eleague.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import ejava.projects.eleague.bo.Venue;
import ejava.projects.eleague.dao.ClubDAO;
import ejava.projects.eleague.dao.ClubDAOException;

/**
 * This class provides a sparse example of a JPA DAO for the class project.
 * It is put in place here to demonstrate some of the end-to-end use cases,
 * 
 * @author jcstaff
 *
 */
public class JPAClubDAO implements ClubDAO {
	private EntityManager em;
	
	/**
	 * This method injects an entity manager to be used by all DAO methods.
	 * @param em
	 */
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public void createVenue(Venue venue) {
        em.persist(venue);
	}

	@SuppressWarnings("unchecked")
	@Override
    public List<Venue> getVenues(int index, int count) 
        throws ClubDAOException {
	    return (List<Venue>)em.createQuery("select v from Venue v")
	                             .setFirstResult(index)
	                             .setMaxResults(count)
	                             .getResultList();
	}
}
