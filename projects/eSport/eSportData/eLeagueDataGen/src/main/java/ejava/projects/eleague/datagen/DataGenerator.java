package ejava.projects.eleague.datagen;

import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.projects.eleague.dao.ELeagueDAO;
import ejava.projects.eleague.dao.JPALeagueDAO;
import ejava.projects.eleague.dto.Club;
import ejava.projects.eleague.dto.Contact;
import ejava.projects.eleague.dto.ContactRoleType;
import ejava.projects.eleague.dto.Contest;
import ejava.projects.eleague.dto.Division;
import ejava.projects.eleague.dto.ELeague;
import ejava.projects.eleague.dto.Season;
import ejava.projects.eleague.dto.Team;
import ejava.projects.eleague.dto.TeamSeason;
import ejava.projects.eleague.dto.Venue;

public class DataGenerator {
	private static final Logger log = LoggerFactory.getLogger(DataGenerator.class);
	private ELeagueDAO dao;
	private int refId=1;
	public static final String OUTPUT_FILE = 
		"ejava.projects.eleague.datagen.outputFile";
	private Marshaller m;
	
	protected int nextRefId() { return ++refId; }
	
	public DataGenerator() throws JAXBException {
		JAXBContext jaxbc = JAXBContext.newInstance(ELeague.class);
		m = jaxbc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	}
	
	public void setELeagueDAO(ELeagueDAO dao) {
		this.dao = dao;
	}
	
	public int generate(Writer writer, int auctionCount) 
	    throws Exception {
		//ELeague league = new SampleGen().createLeague();
	    
	    ELeague league = dao.getLeague();
	    assignRefIds(league);
		
		m.marshal(league, writer);
		
		return 1;
	}
	
	protected void assignRefIds(ELeague league) {
	    for (Contact contact:league.getContact()) {
	        if (contact.getRefid() == null ||
	                contact.getRefid().length() == 0) {
	            contact.setRefid("Contact-" + nextRefId());
	        }
            //log.debug("contactId=" + contact.getId());
	    }
	    for (Season season : league.getSeason()) {
	        if (season.getRefid() == null ||
	                season.getRefid().length() == 0) {
	            season.setRefid("Season-" + nextRefId());
	        }
	        //log.debug("seasonId=" + season.getId());
	        for (Division division : season.getDivision()) {
	            if (division.getRefid() == null ||
	                    division.getRefid().length() == 0) {
	                division.setRefid("Division-" + nextRefId());
	            }
	            //log.debug("divisionId=" + division.getId());
	            for (TeamSeason ts : division.getTeamSeason()) {
	                if (ts.getRefid() == null ||
	                        ts.getRefid().length() == 0) {
	                    ts.setRefid("TS-" + nextRefId());
	                }
	                //log.debug("tsId=" + ts.getId());
	            }
	        }
	        for (Contest contest : season.getContest()) {
	            if (contest.getRefid() == null ||
	                    contest.getRefid().length() == 0) {
	                contest.setRefid("Contest-" + nextRefId());
	            }
	            log.debug("contestId=" + contest.getId() + 
	                    ", ref=" + contest.getRefid());
	        }
	    }
	    for (Club club:league.getClub()) {
	        if (club.getRefid() == null ||
	                club.getRefid().length() == 0) {
	            club.setRefid("Club-" + nextRefId());
	        }
	        for (Team team : club.getTeam()) {
	            if (team.getRefid() == null ||
	                    team.getRefid().length() == 0) {
	                team.setRefid("Team-" + nextRefId());
	            }
	            for (@SuppressWarnings("unused") ContactRoleType contact : 
	            	team.getContactRole()) {
	            }	        
	        }
	        for (Venue venue : club.getVenue()) {
	            if (venue.getRefid() == null || 
	                    venue.getRefid().length() == 0) {
	                venue.setRefid("Venue-" + nextRefId());
	            }
	        }
	    }
	    for (Season season : league.getSeason()) {
	        for (Contest contest : season.getContest()) {
                log.debug("contest id=" + contest.getId() +
                        ":=" + contest.getRefid() +
                        ", venueId=" + ((Venue)contest.getLocation()).getId() +
                        ":" + ((Venue)contest.getLocation()).getRefid() +
                        ", homeId=" + ((TeamSeason)contest.getHomeTeam()).getId() +
                        ":" + ((TeamSeason)contest.getHomeTeam()).getRefid() +
                        ", awayId=" + ((TeamSeason)contest.getAwayTeam()).getId() +
                        ":" + ((TeamSeason)contest.getAwayTeam()).getRefid());
	        }
	    }
	}
	
	public static DataGenerator createDataGenerator(
			Map<String, String> props) throws JAXBException {		
		EntityManagerFactory emf = props != null ?
			Persistence.createEntityManagerFactory(
					JPALeagueDAO.PERSISTENCE_UNIT, props) :
			Persistence.createEntityManagerFactory(
					JPALeagueDAO.PERSISTENCE_UNIT);
		EntityManager em = emf.createEntityManager();
		
		ELeagueDAO dao = new JPALeagueDAO();
		((JPALeagueDAO)dao).setEntityManager(em);
		
		DataGenerator gen = new DataGenerator();
		gen.setELeagueDAO(dao);
		
		return gen;
	}
	
	public static Map<String, String> getProps(String prefix) {
		Map<String, String> props = new HashMap<String, String>();
		Properties sysProps = System.getProperties();
		for(Iterator<Object> itr=sysProps.keySet().iterator(); itr.hasNext();) {
			String key = (String)itr.next();
			if (key.startsWith(prefix + ".")) {
				String name = key.substring(prefix.length()+1);
				String value = sysProps.getProperty(key);
				props.put(name, value);
			}
		}
		return props;
	}	
}
