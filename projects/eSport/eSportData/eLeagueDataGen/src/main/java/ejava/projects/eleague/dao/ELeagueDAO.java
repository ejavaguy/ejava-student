package ejava.projects.eleague.dao;

import java.util.List;

import ejava.projects.eleague.dto.Club;
import ejava.projects.eleague.dto.ELeague;
import ejava.projects.eleague.dto.Season;

public interface ELeagueDAO {
    public ELeague getLeague() throws Exception;
	public List<Season> getSeasons();
	public List<Club> getClubsForSeason(List<Season> seasons);
	public void clear(); 
}
