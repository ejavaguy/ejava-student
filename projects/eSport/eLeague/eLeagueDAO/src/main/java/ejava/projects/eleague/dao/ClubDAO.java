package ejava.projects.eleague.dao;

import java.util.List;

import ejava.projects.eleague.bo.Venue;

/**
 * This interface defines the data access methods defined for the Club
 * portion of the League.
 */
public interface ClubDAO {
	/**
	 * This method will create a new venue in the DB and update the provided
	 * object with the PK of that entity.
	 * @param venue
	 * @throws ClubDAOException
	 */
	void createVenue(Venue venue) 
		throws ClubDAOException;
	
	/**
	 * This method will return all venues in the DB between the start
	 * and count values. Provide 0s to turn off paging.
	 * @param start
	 * @param count
	 * @throws ClubDAOException
	 */
	List<Venue> getVenues(int start, int count)
	    throws ClubDAOException;
}
