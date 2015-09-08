package ejava.projects.eleague.bl;

import java.util.List;

import ejava.projects.eleague.bo.Venue;

public interface ClubMgmt {
	List<Venue> getVenues(int start, int count) throws ClubMgmtException;
}
