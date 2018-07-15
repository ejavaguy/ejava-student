package ejava.projects.eleague.ejb;

import java.io.InputStream;

import static junit.framework.TestCase.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.projects.eleague.dto.Club;
import ejava.projects.eleague.dto.Contact;
import ejava.projects.eleague.dto.ContactRoleType;
import ejava.projects.eleague.dto.Division;
import ejava.projects.eleague.dto.ELeague;
import ejava.projects.eleague.dto.LeagueMetadata;
import ejava.projects.eleague.dto.Season;
import ejava.projects.eleague.dto.Team;
import ejava.projects.eleague.dto.TeamSeason;
import ejava.projects.eleague.dto.Venue;
import ejava.projects.eleague.xml.ELeagueParser;

/**
 * This class provides a sanity parse check of the XML file when deployed
 * to the server as an EJB. This test verifies the right XML classes get
 * deployed.
 * @author jcstaff
 *
 */
@Stateless
public class ParserTestEJB implements ParserTestRemote {
	private static final Logger log = LoggerFactory.getLogger(ParserTestEJB.class);
	
	@Resource(name="vals/xmlFile")
	private String xmlFile;
	
	@PostConstruct
	public void init() {
		log.debug("*** ParserTestEJB ***");
		log.debug("xmlFile=" + xmlFile);		
	}

	public void ingest() throws Exception {
		log.info("ingest");
		
		InputStream is = null;
		
		int clubs=0;
		int contacts=0;
		int md=0;
		int seasons=0;
		try {
			log.trace("getting input file:" + xmlFile);
			is = this.getClass().getResourceAsStream(xmlFile);
			if (is == null) {
				throw new Exception(xmlFile + " was not found");
			}
			
			log.trace("creating parser");
			ELeagueParser parser = new ELeagueParser(ELeague.class, is);
			
			log.trace("starting parse loop");
			Object object=null;
			do {
		        object = parser.getObject(
						"contact", "league-metadata", "club", "season");
		        if (object instanceof Club) {
		            log.debug("found Club");
		            testClub((Club)object);
		            clubs += 1;
		        }
		        else if (object instanceof Contact) {
		        	log.debug("found Contact");
		        	testContact((Contact)object);
		        	contacts += 1;
		        }
		        else if (object instanceof LeagueMetadata) {
		        	log.debug("found LeagueMetadata");
		        	testLeagueMetadata((LeagueMetadata)object);
		        	md += 1;
		        }
		        else if (object instanceof Season) {
		        	log.debug("found Season");
		        	testSeason((Season)object);
		        	seasons += 1;
		        }
		        else if (object != null) {
		            fail("object of unknown type:" + object);
		        }
			} while (object != null);
		}
		catch (Throwable ex) {
			log.error("error parsing doc",ex);
			throw new EJBException("error parsing doc:" + ex);
		}
		finally {
			if (is != null) is.close();
		}
		assertTrue("no clubs found", clubs > 0);
		assertTrue("no contacts found", contacts > 0);
		assertTrue("no metadata found", md > 0);
		assertTrue("no season found", seasons > 0);
		log.debug(clubs + " clubs found");
		log.debug(contacts + " contacts found");
		log.debug(md + " metadata found");
		log.debug(seasons + " seasons found");
	}
	
	void testSeason(Season season) {
		log.debug("{}", new StringBuilder()
			.append("season=")
			.append(season.getId())
			.append(", refId=")
			.append(season.getRefid())
			.append(", contests=")
			.append(season.getContest().size())
			.append(", divisions=")
			.append(season.getDivision().size())
			.append(", starts=")
			.append(season.getStarts())
			.append(", ends=")
			.append(season.getEnds())
			);
		assertNotNull("season id null", season.getId());
		assertNotNull("season name null", season.getName());
		assertNotNull("season refId null", season.getRefid());
		assertNotNull("season contest list null", season.getContest());
		assertNotNull("season ends null", season.getEnds());
		assertNotNull("season refId null", season.getRefid());
		assertNotNull("season starts null", season.getStarts());
		
		for (Division d : season.getDivision()) {
			testDivision(d);
		}
	}

	void testDivision(Division d) {
		log.debug("{}", new StringBuilder()
			.append("division=")
			.append(d.getId())
			.append(", group=")
			.append(d.getGroup())
			.append(", level=")
			.append(d.getLevel())
			.append("refId=")
			.append(d.getRefid())
			.append(", contact=")
			.append(d.getContact())
			.append(", ranking=")
			.append(d.getRanking())
			.append(", teams=")
			.append(d.getTeamSeason().size())
			);
		assertNotNull("division id null", d.getId());
		//assertNotNull("division contact null", d.getContact());
		assertNotNull("division group null", d.getGroup());
		assertNotNull("division level null", d.getLevel());
		assertNotNull("division ranking null", d.getRanking());
		assertNotNull("division refId null", d.getRefid());
		assertNotNull("division teamSeason null", d.getTeamSeason());
		
		for (TeamSeason ts : d.getTeamSeason()) {
			testTeamSeason(ts);
		}
	}

	void testTeamSeason(TeamSeason ts) {
		log.debug("{}", new StringBuilder()
			.append("teamSeason=")
			.append(ts.getId())
			.append(", refId=")
			.append(ts.getRefid())
			.append(", teamName=")
			.append(((Team)ts.getTeam()).getName())
				);
		assertNotNull("team season id null", ts.getId());
		assertNotNull("team season refId null", ts.getRefid());
		assertNotNull("team season team null", ts.getTeam());
		
		testTeam((Team)ts.getTeam());
	}

	void testLeagueMetadata(LeagueMetadata md) {
		log.debug("{}", new StringBuilder()
			.append("league=")
			.append(md.getName())
			.append(", contactName=")
			.append(((Contact)md.getContact()).getName()));
		assertNotNull("leage name null", md.getName());
		assertNotNull("league contact null", md.getContact());
		
		testContact((Contact)md.getContact());
	}

	void testContact(Contact contact) {
		log.debug("{}", new StringBuilder()
			.append("contact=")
			.append(contact.getId())
			.append(", name=")
			.append(contact.getName())
			.append(", login=")
			.append(contact.getLogin())
			.append(", refId=")
			.append(contact.getRefid()));
		assertNotNull("contact ID null", contact.getId());
		assertNotNull("contact name null", contact.getName());
		assertNotNull("contact refId null", contact.getRefid());
	}

	void testClub(Club club) {
		log.debug("{}", new StringBuilder()
                  .append("club=")
                  .append(club.getId())
                  .append(", name=")
                  .append(club.getName())
                  .append(", refId=")
                  .append(club.getRefid())
                  .append(", teams=")
                  .append(club.getTeam().size())
                  .append(", venues=")
                  .append(club.getVenue().size()));

		assertNotNull("club id null", club.getId());
		assertNotNull("club contact null", club.getContact());
		assertNotNull("club name null", club.getName());
		assertNotNull("club refId null", club.getRefid());
		assertNotNull("club team list null", club.getTeam());
		assertNotNull("club venue list null", club.getVenue());
		
		for (Team team : club.getTeam()) {
			testTeam(team);
		}
		for (Venue venue : club.getVenue()) {
			testVenue(venue);
		}
	}
	
	private void testVenue(Venue venue) {
		log.debug("{}", new StringBuilder()
			.append("venue=")
			.append(venue.getId())
			.append(", name=")
			.append(venue.getName())
			.append(", ADC=")
			.append(venue.getAdcPage())
			.append(", address={")
			.append(venue.getStreet1())
			.append(" ")
			.append(venue.getStreet2()==null ? "" : venue.getStreet2())
			.append("; ")
			.append(venue.getCity())
			.append(", ")
			.append(venue.getState())
			.append(" ")
			.append(venue.getZip())
			.append("}"));
		assertNotNull("venue ADC null", venue.getAdcPage());
		assertNotNull("venue city null", venue.getCity());
		assertNotNull("venue directions null", venue.getDirections());
		assertNotNull("venue id null", venue.getId());
		assertNotNull("venue name null", venue.getName());
		assertNotNull("venue refId null", venue.getRefid());
		assertNotNull("venue state null", venue.getState());
		assertNotNull("venue street1 null", venue.getStreet1());
		//assertNotNull("venue street2 null", venue.getStreet2());
		assertNotNull("venue zip null", venue.getZip());
	}

	void testTeam(Team team) {
		log.debug("{}", new StringBuilder()
			.append("team=")
			.append(team.getId())
			.append(", name=")
			.append(team.getName())
			.append(", refId=")
			.append(team.getRefid())
			.append(", contacts=")
			.append(team.getContactRole().size()));
		assertNotNull("team id null", team.getId());
		assertNotNull("team name null", team.getName());
		assertNotNull("team refId null", team.getRefid());
		assertNotNull("team contactRole null", team.getContactRole());
		
		for (ContactRoleType role : team.getContactRole()) {
			testContactRole(role);
		}
	}

	void testContactRole(ContactRoleType role) {
		log.debug("{}", new StringBuilder()
			.append("contactRole=")
			.append(role.getId())
			.append(", role=")
			.append(role.getRole())
			.append(", contactName=")
			.append(((Contact)role.getContact()).getName()));
		assertNotNull("contactRole id null", role.getId());
		assertNotNull("contactRole role null", role.getRole());
		assertNotNull("contactRole contact null", role.getContact());

		testContact((Contact)role.getContact());
	}
}
