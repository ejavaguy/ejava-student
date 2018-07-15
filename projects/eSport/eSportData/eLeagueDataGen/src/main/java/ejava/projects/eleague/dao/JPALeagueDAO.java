package ejava.projects.eleague.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.projects.eleague.dto.Club;
import ejava.projects.eleague.dto.Contact;
import ejava.projects.eleague.dto.ContactRoleType;
import ejava.projects.eleague.dto.Contest;
import ejava.projects.eleague.dto.Division;
import ejava.projects.eleague.dto.ELeague;
import ejava.projects.eleague.dto.LeagueMetadata;
import ejava.projects.eleague.dto.Season;
import ejava.projects.eleague.dto.Team;
import ejava.projects.eleague.dto.TeamSeason;
import ejava.projects.eleague.dto.Venue;

public class JPALeagueDAO implements ELeagueDAO {
	private static final Logger log = LoggerFactory.getLogger(JPALeagueDAO.class);
	public static final String PERSISTENCE_UNIT = "eLeagueData";
	private EntityManager em;
	
	private static int MAX_VENUES_PER_CLUB = 2;

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	public void clear() {
		em.clear();
	}

    public List<Club> getClubsForSeason(List<Season> seasons) {
        // TODO Auto-generated method stub
        return null;
    }

    public ELeague getLeague() throws Exception {
        //we should only expect to get 1 league
        LeagueMetadata md = (LeagueMetadata)em.createQuery(
                "select lg from LeagueMetadata lg")
                .getSingleResult();
        ELeague league = new ELeague();
        league.setLeagueMetadata(md);
        if (md.getContact() != null) {
            league.getContact().add((Contact)md.getContact());
        }
        
        List<Club> clubs = getClubs();
        league.getClub().addAll(clubs);
        for (Club club : clubs) {
            //log.debug("club:" + club.getName() 
              //      + ", venues=" + club.getVenue().size());
            while (club.getVenue().size() > MAX_VENUES_PER_CLUB) {
                int index = club.getVenue().size() - 1;
                @SuppressWarnings("unused")
				Venue venue = club.getVenue().remove(index);
            }
            if (club.getVenue().size() == 0) {
                throw new Exception("no Venues for Club:" + club.getName());
            }
            log.debug("club:" + club.getName() 
                    + ", venues=" + club.getVenue().size());

            if (club.getContact() != null) {
                league.getContact().add(club.getContact());
            }
            for (Team team : club.getTeam()) {
                int managerCount = 0;
                int coachCount = 0;
                List<ContactRoleType> newContacts = 
                    new ArrayList<ContactRoleType>();
                for (ContactRoleType role : team.getContactRole()) {
                    if (role.getContact() != null) {
                        league.getContact().add((Contact)role.getContact());
                    }
                    String roleName = role.getRole();
                    if (roleName.contains("oach")) {
                        role.setRole("COACH");
                    }
                    if (roleName.contains("Mgr") || 
                            roleName.contains("Manager")){
                        role.setRole("MANAGER");
                    }
                    if (!role.getRole().equals("COACH") && 
                            !role.getRole().equals("MANAGER")) {
                        throw new Exception("unexpected role:" + 
                                role.getRole());
                    }
                    //lets cut down on the number of coachs and managers
                    if (role.getRole().equals("MANAGER") &&
                            ++managerCount <= 1) {
                        newContacts.add(role);
                    }
                    else if (role.getRole().equals("COACH") &&
                            ++coachCount <= 1) {
                        newContacts.add(role);
                    }
                }
                team.getContactRole().clear();
                team.getContactRole().addAll(newContacts);
            }
        }
        
        List<Season> seasons = getSeasons();
        league.getSeason().addAll(seasons);
        
        if (seasons.size() > 0) {
            int index = seasons.size() - 1;
            league.setCurrentSeason(seasons.get(index));    
        }
        
        //the following logic assumes some knowledge of how many clubs and
        //seasons there are.
        Iterator<Club> citr = league.getClub().iterator();
        Iterator<Season> sitr = league.getSeason().iterator();
        Club[] clubs2004 = new Club[2];
        Season season2004 = sitr.next();
        for (int i=0; i<clubs2004.length; i++) {
            clubs2004[i] = citr.next();
            getTeamsForFirstSeason(
                    clubs2004[i], league.getSeason(), season2004, 1);
        }
        getSeasonSchedule(league, season2004);
        getScores(season2004);
        
        Club[] clubs2005 = new Club[2];        
        Season season2005 = sitr.next();
        for (Club club : clubs2004) {
            getTeamsForNextSeason(club, season2004, season2005);
            getTeamsForFirstSeason(club, league.getSeason(), season2005, 2);
        }
        for (int i=0; i<clubs2005.length; i++) {
            clubs2005[i] = citr.next();
            getTeamsForFirstSeason(
                    clubs2005[i], league.getSeason(), season2005, 2);
        }
        getSeasonSchedule(league, season2005);
        getScores(season2005);

        Club[] clubs2006 = new Club[5];        
        Season season2006 = sitr.next();
        for (Club club : clubs2004) {
            getTeamsForNextSeason(club, season2005, season2006);
            getTeamsForFirstSeason(club, league.getSeason(), season2006, 2);
        }
        for (Club club : clubs2005) {
            getTeamsForNextSeason(club, season2005, season2006);
            getTeamsForFirstSeason(club, league.getSeason(), season2006, 2);
        }
        for (int i=0; i<clubs2006.length; i++) {
            clubs2006[i] = citr.next();
            getTeamsForFirstSeason(
                    clubs2006[i], league.getSeason(), season2006, 10);
        }
        getSeasonSchedule(league, season2006);
        getScores(season2006);

        Season season2007 = sitr.next();
        for (Club club : clubs2004) {
            getTeamsForNextSeason(club, season2006, season2007);
            getTeamsForFirstSeason(club, league.getSeason(), season2007, 10);
        }
        for (Club club : clubs2005) {
            getTeamsForNextSeason(club, season2006, season2007);
            getTeamsForFirstSeason(club, league.getSeason(), season2007, 10);
        }
        for (Club club : clubs2006) {
            getTeamsForNextSeason(club, season2006, season2007);
            getTeamsForFirstSeason(club, league.getSeason(), season2007, 10);
        }

        return league;
    }
    
    public List<Club> getClubs() {
        @SuppressWarnings("unchecked")
		List<Club> clubs = (List<Club>)em.createQuery(
                "select c from Club c")
                .getResultList();
        for (Club club : clubs) {
            log.debug(club.getName() + " has " + club.getTeam().size() + " teams");
        }
        return clubs;
    }


    //no seasons in DB - just a stub so far
    public List<Season> getSeasons() {
        List<Season> seasons = new ArrayList<Season>();
        
        Season season = new Season();
        seasons.add(season);
        season.setId(20041);
        Calendar cal = new GregorianCalendar();
        cal.set(2004, Calendar.MARCH, 01, 0, 0, 0);
        season.setStarts(cal.getTime());
        cal.set(2004, Calendar.JUNE, 01, 0, 0, 0);
        season.setEnds(cal.getTime());
        season.setName("Spring 2004");        
        List<Division> divisions = getDivisions(
                new String[] { "U13" },
                new String[] { "AA" } );
        season.getDivision().addAll(divisions);
        
        season = new Season();
        seasons.add(season);
        season.setId(20051);
        cal = new GregorianCalendar();
        cal.set(2005, Calendar.MARCH, 01, 0, 0, 0);
        season.setStarts(cal.getTime());
        cal.set(2005, Calendar.JUNE, 01, 0, 0, 0);
        season.setEnds(cal.getTime());
        season.setName("Spring 2005");        
        divisions = getDivisions(
                new String[] { "U13", "U14" },
                new String[] { "AA" , "A"} );
        season.getDivision().addAll(divisions);
        
        season = new Season();
        seasons.add(season);
        season.setId(20061);
        cal = new GregorianCalendar();
        cal.set(2006, Calendar.MARCH, 01, 0, 0, 0);
        season.setStarts(cal.getTime());
        cal.set(2006, Calendar.JUNE, 01, 0, 0, 0);
        season.setEnds(cal.getTime());
        season.setName("Spring 2006");        
        divisions = getDivisions(
                new String[] { "U13", "U14", "U15" },
                new String[] { "AA" , "A", "B" } );
        season.getDivision().addAll(divisions);
        
        season = new Season();
        seasons.add(season);
        season.setId(20071);
        cal = new GregorianCalendar();
        cal.set(2007, Calendar.MARCH, 01, 0, 0, 0);
        season.setStarts(cal.getTime());
        cal.set(2050, Calendar.JUNE, 01, 0, 0, 0);
        season.setEnds(cal.getTime());
        season.setName("Spring NeverEnds");        
        divisions = getDivisions(
                new String[] { "U13", "U14", "U15" },
                new String[] { "AA" , "A", "B" } );
        season.getDivision().addAll(divisions);
        
        return seasons;
    }
    
    long divisionId = 0;
    
    //we don't have divisions in DB either:(
    public List<Division> getDivisions(
            String[] groups, String[] levels) {
        List<Division> divisions = new ArrayList<Division>();
        
        int grouping = 0;
        for (String group : groups) {
            grouping += 1;
            int levelno = 0;
            for (String level : levels) {
                levelno += 1;
                Division division = new Division();
                division.setId(++divisionId);
                division.setGroup(group);
                division.setLevel(level);
                division.setRanking((grouping * 10) + levelno);
                divisions.add(division);
            }
        }
        
        return divisions;
    }
    
    private void getScores(Season season) {
        Random rand = new Random();
        for (Contest contest : season.getContest()) {
            contest.setHomeScore(rand.nextInt(5));
            contest.setAwayScore(rand.nextInt(5));
        }
    }
    
    
    private long tsId=0;
    public void getTeamsForFirstSeason(
            Club club, List<Season> seasons, Season firstSeason, int maxTeams) 
        throws Exception {
        int divisionIndex = 0;
        
        int teamCount = 0;
        for (Team team : club.getTeam()) {
            if (!isFirstSeason(seasons, team)) {
                continue;
            }
            Division division = getNextDivision(firstSeason, divisionIndex++);
            if (division != null) {
                TeamSeason ts = new TeamSeason();
                division.getTeamSeason().add(ts);
                ts.setId(++tsId);
                ts.setTeam(team);                
            }
            else {
                throw new Exception("no first Division available for" + 
                        firstSeason.getName());
            }
            if (++teamCount >= maxTeams) {
                break;
            }
        }
    }
    
    public boolean isFirstSeason(List<Season> seasons, Team team) {
        boolean firstSeason = true;
        
        for (Season season : seasons) {
            if (getDivisionForTeam(season, team) != null) {
                firstSeason = false;
                break;
            }
        }
        
        return firstSeason;
    }
    
    public void getTeamsForNextSeason(
            Club club, Season lastSeason, Season nextSeason) {
        for (Team team : club.getTeam()) {
            Division lastDivision = getDivisionForTeam(lastSeason, team);
            if (lastDivision != null) {
                int ranking = lastDivision.getRanking();
                Division nextDivision = 
                    getDivisionByRank(nextSeason, ranking + 10);
                if (nextDivision != null) {
                    TeamSeason ts = new TeamSeason();
                    nextDivision.getTeamSeason().add(ts);
                    ts.setId(++tsId);
                    ts.setTeam(team);
                }
            }
        }
    }

    protected Division getNextDivision(Season season, int index) {
        Division division = null;
        
        if (season.getDivision().size() > 0) {
            int pos = index % season.getDivision().size();
            division = season.getDivision().get(pos);
        }
                
        return division;
    }

    public Division getDivisionForTeam(Season season, Team team) {
        Division division = null;
        
        for (Division d : season.getDivision()) {
            for (TeamSeason ts : d.getTeamSeason()) {
                if (((Team)ts.getTeam()).getId() == team.getId()) {
                    division = d;
                    break;
                }
            }
        }
        
        return division;
    }
    
    public Division getDivisionByRank(Season season, int rank) {
        Division division = null;
        
        for (Division d : season.getDivision()) {
            if (d.getRanking() == rank) {
                division = d;
            }
        }
        
        return division;
    }
    
    long contestId=0;
    //we don't have contests, so we'll have to dummy them up
    protected void getSeasonSchedule(ELeague league, Season season) {
        for(Division division : season.getDivision()) {
            int numTeams = division.getTeamSeason().size();
            for (int i=0; i<numTeams; i++) {
                for (int j=numTeams-1; j>= 0; j--) {
                    if (i!=j) {
                        TeamSeason homeTeam = division.getTeamSeason().get(i);
                        TeamSeason awayTeam = division.getTeamSeason().get(j);
                        scheduleContest(league, season, homeTeam, awayTeam);
                    }
                }
            }
        }
    }
    
    protected Venue getField(ELeague league, Season season, Team team) {
        List<Venue> fields = new ArrayList<Venue>();
        
        //find all fields for club
        for (Club club : league.getClub()) {
            for (Team clubTeam : club.getTeam()) {
                if (team.getId() == clubTeam.getId()) { 
                    fields = club.getVenue();
                    break;
                }
            }
        }
        
        //find least used venue within club
        Venue field = null;
        int least = 0;
        for (Venue venue: fields) {
            int games = getContestsForVenue(season, venue).size();
            if (field == null || games < least) {
                field = venue;
                least = games;
            }
        }
        
        return field;
    }
    
    private Contest scheduleContest(
            ELeague league, Season season, 
            TeamSeason homeTeam, TeamSeason awayTeam) {
        Contest contest = null;
        
        List<Venue> fields = getFieldsForTeam(league, (Team)homeTeam.getTeam());
        for (Venue field : fields) {
            Calendar gameTime = getStartTime(season, field, homeTeam, awayTeam);
            if (gameTime != null) {
                contest = new Contest();
                contest.setId(++contestId);
                contest.setHomeTeam(homeTeam);
                contest.setAwayTeam(awayTeam);
                contest.setLocation(field);
                contest.setStarts(gameTime.getTime());
                contest.setDuration(2*3600*1000);
                contest.setAwayScore(-1);
                contest.setHomeScore(-1);
                log.debug("contest id=" + contest.getId() +
                        ", venueId=" + ((Venue)contest.getLocation()).getId() +
                        ", homeId=" + ((TeamSeason)contest.getHomeTeam()).getId() +
                        ", awayId=" + ((TeamSeason)contest.getAwayTeam()).getId());
                season.getContest().add(contest);
                break;
            }
        }
        
        return contest;
    }
    
    protected List<Venue> getFieldsForTeam(ELeague league, Team team) {
        List<Venue> fields = new ArrayList<Venue>();
        
        //find all fields for club
        for (Club club : league.getClub()) {
            for (Team clubTeam : club.getTeam()) {
                if (team.getId() == clubTeam.getId()) { 
                    fields = club.getVenue();
                    break;
                }
            }
        }
        
        return fields;
    }

    private Calendar getStartTime(
            Season season, Venue venue, 
            TeamSeason homeTeam, TeamSeason awayTeam) {
        List<Contest> homeSchedule = getContestsForTeam(season, homeTeam);
        List<Contest> awaySchedule = getContestsForTeam(season, awayTeam);
        List<Contest> venueSchedule = getContestsForVenue(season, venue);
        
        Calendar seasonStart = new GregorianCalendar();
        seasonStart.setTime(season.getStarts());
        Calendar seasonEnds = new GregorianCalendar();
        seasonEnds.setTime(season.getEnds());
        
        //advance to Sundays
        seasonStart.set(Calendar.MONTH, Calendar.MARCH);
        seasonStart.set(Calendar.DAY_OF_MONTH, 0);
        while (seasonStart.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            seasonStart.set(Calendar.DAY_OF_YEAR, 
                    seasonStart.get(Calendar.DAY_OF_YEAR) + 1);
        }

        //find a Sunday when everyone is free
        Calendar gameTime = new GregorianCalendar();
        gameTime.setTime(seasonStart.getTime());
        for(int i=seasonStart.get(Calendar.DAY_OF_YEAR); 
            i<seasonEnds.get(Calendar.DAY_OF_YEAR); i+= 7) {
            gameTime.set(Calendar.DAY_OF_YEAR, i);
            if (isAvailable(homeTeam, homeSchedule, gameTime.getTime()) &&
                    isAvailable(awayTeam, awaySchedule, gameTime.getTime())) {
                for (int j=9; j<16; j+=2) {
                    gameTime.set(Calendar.HOUR, j);
                    if (isAvailable(venue, venueSchedule, gameTime.getTime())) {
                        return gameTime;
                    }
                }
            }
        }
        
        return null;        
    }
    
    protected List<Contest> getContestsForVenue(Season season, Venue venue) {
        List<Contest> contests = new ArrayList<Contest>();
        
        for (Contest contest : season.getContest()) {
            if (((Venue)contest.getLocation()).getId() == venue.getId()) {
                contests.add(contest);    
            }
        }        
        return contests;
    }
    
    protected boolean isAvailable(
            Venue venue, List<Contest> schedule, Date gameTime) {
        boolean isAvailable = true;
        
        Calendar gameDay = new GregorianCalendar();
        gameDay.setTime(gameTime);
        for (Contest contest : schedule) {
            Calendar contestDay = new GregorianCalendar();
            contestDay.setTime(contest.getStarts());
            if (contestDay.get(Calendar.DAY_OF_YEAR) ==
                gameDay.get(Calendar.DAY_OF_YEAR) &&
                contestDay.get(Calendar.HOUR) ==
                    gameDay.get(Calendar.HOUR)) {
                isAvailable = false;
                break;
            }
        }

        return isAvailable;
    }
    
    protected List<Contest> getContestsForTeam(Season season, TeamSeason team) {
        List<Contest> contests = new ArrayList<Contest>();
        
        for (Contest contest : season.getContest()) {
            if (((TeamSeason)contest.getHomeTeam()).getId() == team.getId()) {
                contests.add(contest);
            }
        }
        
        return contests;
    }
    
    protected boolean isAvailable(
            TeamSeason team, List<Contest> schedule, Date gameTime) {
        boolean isAvailable = true;
        
        Calendar gameDay = new GregorianCalendar();
        gameDay.setTime(gameTime);
        for (Contest contest : schedule) {
            Calendar contestDay = new GregorianCalendar();
            contestDay.setTime(contest.getStarts());
            if (contestDay.get(Calendar.DAY_OF_YEAR) ==
                gameDay.get(Calendar.DAY_OF_YEAR)) {
                isAvailable = false;
                break;
            }
        }
        
        return isAvailable;
    }

}
