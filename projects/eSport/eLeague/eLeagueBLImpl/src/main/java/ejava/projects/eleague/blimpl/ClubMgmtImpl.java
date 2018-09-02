package ejava.projects.eleague.blimpl;

import java.util.List;

import ejava.projects.eleague.bl.ClubMgmt;
import ejava.projects.eleague.bl.ClubMgmtException;
import ejava.projects.eleague.bo.Venue;
import ejava.projects.eleague.dao.ClubDAO;

/**
 * This class provides a simple example of the account mgmt implementation.
 */
public class ClubMgmtImpl implements ClubMgmt {
	private ClubDAO clubDAO;
	
	public void setClubDAO(ClubDAO clubDAO) {
		this.clubDAO = clubDAO;;
	}

	public List<Venue> getVenues(int index, int count) 
	    throws ClubMgmtException {
		
		if (count < 0) {
			throw new ClubMgmtException("count must be >= 0");
		}
		return clubDAO.getVenues(index, count);
	}
}
