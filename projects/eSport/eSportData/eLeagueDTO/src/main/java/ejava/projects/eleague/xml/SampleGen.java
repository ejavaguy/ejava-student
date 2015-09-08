package ejava.projects.eleague.xml;

import java.util.Calendar;

import java.util.GregorianCalendar;
import java.util.Random;

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

/**
 * This class provides the ability to generate a sample set of League 
 * DTOs and XML document. Of note, this is is to be used for initial 
 * implementation testing only. It is not the official data set required
 * for submission with the project.
 * 
 * @author jcstaff
 *
 */
public class SampleGen {
    private int refid=0;
    private long id=0;

    protected String nextRefId() { return "ref-" + Integer.toString(++refid); }
    protected long nextId() { return ++id; }

    protected Club createClub(
            ELeague league, String name, String rep, int teams) {
        Club club = new Club();
        club.setId(nextId());
        club.setRefid(nextRefId());
        club.setName(name);

        Contact clubRep = new Contact();
        club.setContact(clubRep);
        league.getContact().add(clubRep);
        clubRep.setId(nextId());
        clubRep.setRefid(nextRefId());
        clubRep.setName("joe " + rep);
        clubRep.setEMail(rep + "@" + name + ".org");
        clubRep.setLogin(rep);
        
        Venue field1 = new Venue();
        club.getVenue().add(field1);
        field1.setId(nextId());
        field1.setRefid(nextRefId());
        field1.setName(name + " Park");
        field1.setAdcPage("AA/13/5F");
        field1.setDirections("get there and turn left");
        field1.setStreet1("8830 " + name + " Blvd");
        field1.setCity("Columbia");
        field1.setState("MD");
        field1.setZip("21045");
        
        for (int i=0; i<teams; i++) {
            Team team = new Team();
            club.getTeam().add(team);
            team.setId(nextId());
            team.setRefid(nextRefId());
            team.setName(name + " team# " + i);

            ContactRoleType coach = new ContactRoleType();
            team.getContactRole().add(coach);
            coach.setRole("COACH");

            Contact coachContact = new Contact();
            coach.setContact(coachContact);
            league.getContact().add(coachContact);
            coachContact.setRefid(nextRefId());
            
            if (i==0) {
                coach.setId(clubRep.getId());
                coachContact.setName(clubRep.getName());
                coachContact.setEMail(clubRep.getEMail());
                coachContact.setLogin(clubRep.getLogin());
            }
            else {
                coach.setId(nextId());
                coachContact.setName("coach #" + i);
                coachContact.setEMail("coach." + i + "@" + name + ".org");
                coachContact.setLogin(name + "_" + i);

                ContactRoleType manager = new ContactRoleType();
                team.getContactRole().add(manager);
                Contact managerContact = new Contact();
                league.getContact().add(managerContact);
                manager.setContact(managerContact);
                manager.setId(nextId());
                manager.setRole("MANAGER");
                managerContact.setRefid(nextRefId());
                managerContact.setName("manage #" + i);
                managerContact.setEMail("manager." + i + "@" + name + ".org");
                managerContact.setLogin(name + "_m" + i);
            }
        }

        return club;
    }
    
    protected Season createSeason(
            ELeague league, int year) {
        Season season = new Season();
        season.setId(nextId());
        season.setRefid(nextRefId());
        season.setName("Spring " + Integer.toString(year));
        
        Calendar cal = new GregorianCalendar();
        cal.set(2007, Calendar.MARCH, 01);
        season.setStarts(cal.getTime());
        cal.set(2007, Calendar.JUNE, 01);
        season.setEnds(cal.getTime());
        
        String groups[] = { "U11", "U13", "U15"};
        for (int g=0; g<groups.length; g++) {
            String levels[] = new String[]{ "B", "A", "AA"};
            for (int i=0; i<levels.length; i++) {
                Division division = new Division();
                season.getDivision().add(division);
                division.setId(nextId());
                division.setRefid(nextRefId());
                division.setGroup(groups[g]);
                division.setLevel(levels[i]);
                division.setRanking(g*10 + i);
                
                Contact coordinator = new Contact();
                league.getContact().add(coordinator);
                division.setContact(coordinator);
                coordinator.setId(nextId());
                coordinator.setRefid(nextRefId());
                coordinator.setName("Mr. " + groups[g] + "-" + levels[i]);
                coordinator.setLogin(groups[g] + levels[i]);                
            }
        }
        
        return season;
    }
    
    protected void assignTeamsToDivision(Club club, Season season) {
        int divIndex = 10000;
        for (Team team : club.getTeam()) {
            TeamSeason divPlay = new TeamSeason();
            divPlay.setId(nextId());
            divPlay.setRefid(nextRefId());
            divPlay.setTeam(team);
            if (++divIndex >= season.getDivision().size()) {
                divIndex = 0;    
            }
            Division division = season.getDivision().get(divIndex);
            division.getTeamSeason().add(divPlay);
        }
        
    }
    
    protected Venue getField(ELeague league, Team team) {
        Venue field = null;
        for (Club club : league.getClub()) {
            for (Team clubTeam : club.getTeam()) {
                if (team.getId() == clubTeam.getId()) { 
                    field = club.getVenue().iterator().next();
                }
            }
        }
        return field;
    }
    
    protected void scheduleSeason(ELeague league, Season season) {
        for(Division division : season.getDivision()) {
            int numTeams = division.getTeamSeason().size();
            for (int i=0; i<numTeams; i++) {
                for (int j=numTeams-1; j>= 0; j--) {
                    if (i!=j) {
                        Contest contest = new Contest();
                        season.getContest().add(contest);
                        contest.setId(nextId());
                        contest.setRefid(nextRefId());
                        Calendar cal = new GregorianCalendar();
                        cal.setTime(season.getStarts());
                        int year = cal.get(Calendar.YEAR);
                        cal.set(year,Calendar.MARCH,1+i,8+((j%12)*2),0, 0);
                        contest.setStarts(cal.getTime());
                        TeamSeason homeTeam = division.getTeamSeason().get(i);
                        TeamSeason awayTeam = division.getTeamSeason().get(j);
                        contest.setHomeTeam(homeTeam);
                        contest.setAwayTeam(awayTeam);
                        contest.setDuration(2*3600*1000);
                        contest.setLocation(
                                getField(league, (Team)homeTeam.getTeam()));
                        contest.setAwayScore(-1);
                        contest.setHomeScore(-1);
                    }
                }
            }
        }
    }
    
    protected void reportScores(Season season) {
        Random random = new Random();
        for (Contest contest : season.getContest()) {
            contest.setHomeScore(random.nextInt(5));
            contest.setAwayScore(random.nextInt(5));
        }
    }
    
    protected LeagueMetadata createLeagueMetadata(ELeague league) {
        LeagueMetadata md = new LeagueMetadata();
        md.setName("Everyone Doit League");
        
        Contact coordinator = new Contact();
        league.getContact().add(coordinator);
        md.setContact(coordinator);
        coordinator.setRefid(nextRefId());
        coordinator.setId(nextId());
        coordinator.setName("John Doe");
        
        return md;
    }
    
    public ELeague createLeague() throws Exception {
        ELeague league = new ELeague();
        
        LeagueMetadata md = createLeagueMetadata(league);
        league.setLeagueMetadata(md);

        String club[] = new String[] {
            "RockemSockem", "GoGetems", "RedRiders", "Senators", "Sailors",
            "ChaChas", "Locos", "Greenhornets", "Predators", "Holsteins"
        };
        String rep[] = new String [] {
          "smith", "jones", "klink", "schultz", "kotter",
          "seaver", "sullivan", "marchabroda", "modell", "rozell"
        };
        for (int i=0; i<club.length; i++) {
            league.getClub().add(createClub(league, club[i], rep[i], i+1));
        }
        
        
        for (int year : new int[] { 2005, 2006, 2007}) {
            Season season = createSeason(league, year);
            league.getSeason().add(season);
            league.setCurrentSeason(season);
            
            for(Club c : league.getClub()) {
                assignTeamsToDivision(c, season);
            }
            
            scheduleSeason(league, season);
            reportScores(season);
        }
        Season season = createSeason(league, 2008);
        league.getSeason().add(season);
        league.setCurrentSeason(season);
        
        return league;     
    }
}
