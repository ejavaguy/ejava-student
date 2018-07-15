package ejava.projects.eleague.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

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

import junit.framework.TestCase;

/**
 * This provides a basic test of a constructed League DTO graph to be 
 * successfully marshalled and de-marshalled to/from an XML steam. 
 * @author jcstaff
 *
 */
public class ELeagueBindingTest extends TestCase {
    private Logger log = LoggerFactory.getLogger(ELeagueBindingTest.class);
    private Marshaller m;
    
    public void setUp() throws Exception {
        JAXBContext jaxbc = JAXBContext.newInstance(ELeague.class);
        m = jaxbc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    }
    

    public void testCalendar() throws Exception {
        log.info("*** testCalendar ***");
        DatatypeFactory dataFactory = DatatypeFactory.newInstance();
        log.info("DataTypeFactory=" + dataFactory);
        XMLGregorianCalendar cal = dataFactory.newXMLGregorianCalendar();
        log.info("XMLGregorianCalendar=" + cal.getClass());
        cal.setMonth(GregorianCalendar.MARCH);
        String xml = cal.toXMLFormat();
        log.debug("cal=" + xml);
        dataFactory.newXMLGregorianCalendar(xml);

        cal.setTimezone(0);

        Calendar jCal = Calendar.getInstance();
        jCal.clear();
        jCal.set(Calendar.MONTH, Calendar.MARCH);
        DateFormat df = DateFormat.getDateInstance();
        String dfString = df.format(jCal.getTime());
        log.debug("calendar=" + dfString);

        String format = "--01";
        try {
                XMLGregorianCalendar xCal = dataFactory.newXMLGregorianCalendar(format);
                log.info("successfully parsed:" + format + ", xCal=" + xCal.toXMLFormat());
                format = "--01--";
                xCal = dataFactory.newXMLGregorianCalendar(format);
                log.info("successfully parsed:" + format + ", xCal=" + xCal.toXMLFormat());
        }
        catch (Exception ex) {
                log.error("failed to parse:" + format);
                fail("failed to parse:" + format);
        }
    }


    public void testMarshallDemarshall() throws Exception {
        log.info("*** testMarshallDemarshall ***");
        ELeague league = new SampleGen().createLeague();
        
        File xmlFile = new File("target/test-classes/league.xml");
        FileOutputStream fos = new FileOutputStream(xmlFile);
        m.marshal(league, fos);
        fos.close();

        FileInputStream fis = new FileInputStream(xmlFile);
        ELeagueParser parser = new ELeagueParser(ELeague.class, fis);
        Object object=null;
        while ((object = 
            parser.getObject(
                    "contact", "league-metadata", "club", "season")) != null) {
            log.debug(object);
            if (object instanceof Contact) {
                Contact contact = (Contact) object;
                Contact expected = 
                    (Contact)getById(contact.getId(), league.getContact());
                assertNotNull("unexpected contact id:" + contact.getId(),
                        expected);
            }
            if (object instanceof LeagueMetadata) {
                compare(league.getLeagueMetadata(), (LeagueMetadata)object);
            }
            else if (object instanceof Club) {
                Club club = (Club)object;
                Club expected = (Club)getById(club.getId(), league.getClub());
                assertNotNull("unexpected club id:" + club.getId(), expected);
                compare(expected, club);
            }
            else if (object instanceof Season) {
                Season season = (Season) object;
                Season expected = 
                    (Season)getById(season.getId(), league.getSeason());
                assertNotNull("unexpected season id:" + season.getId(), 
                        expected);
                compare(expected, season);
            }
        }        
    }
    
    private Object getById(long id, List<?> objects) {        
        Object theObject=null;
        for (Object object : objects) {
            Method getId;
            Long theId=0L;
            try {
                getId = object.getClass().getMethod("getId", new Class[]{});
                theId = (Long)getId.invoke(object, new Object[]{});
            } catch (Exception e) {
                fail(e.toString());
            }
            if (id == theId) {
                theObject = object;
                break;
            }
        }
        return theObject;
    }
    
    @SuppressWarnings("unused")
	private void compare(ELeague expected, ELeague actual) throws Exception {
        compare(expected.getLeagueMetadata(), actual.getLeagueMetadata());
        assertEquals("unexpected number of clubs",
                expected.getClub().size(), actual.getClub().size());
        for(Club actualClub : actual.getClub()) {
            long id = actualClub.getId();
            Club expectedClub = (Club)getById(id, expected.getClub());
            compare(expectedClub,actualClub);
        }
        for(Season actualSeason : actual.getSeason()) {
            Season expectedSeason = (Season)
                getById(actualSeason.getId(), expected.getSeason());
            compare(expectedSeason, actualSeason);
        }
     }
    
    private void compare(LeagueMetadata expected, LeagueMetadata actual) {
        assertEquals("unexpected name", 
                expected.getName(), actual.getName());
        compare((Contact)expected.getContact(), (Contact)actual.getContact());
    }
    
    private void compare (Contact expected, Contact actual) {
        assertEquals("unexpected id", expected.getId(), actual.getId());
        assertEquals("unexpected e-mail",expected.getEMail(), actual.getEMail());
        assertEquals("unexpected login", expected.getLogin(), actual.getLogin());
        assertEquals("unexpected name", expected.getName(), actual.getName());
    }
    
    private void compare(ContactRoleType expected, ContactRoleType actual) {
        assertEquals("unexpected id", expected.getId(), actual.getId());
        assertEquals("unexpected role", expected.getRole(), actual.getRole());
        compare((Contact)expected.getContact(), 
                (Contact)actual.getContact());
    }
    
    private void compare(Club expected, Club actual) {
        assertEquals("unexpected id", expected.getId(), actual.getId());
        assertEquals("unxpected name", expected.getName(), actual.getName());
        compare((Contact)expected.getContact(), (Contact)actual.getContact());
        assertEquals("unexpected number of venues",
                expected.getTeam().size(), actual.getTeam().size());
        for (Venue actualVenue : actual.getVenue()) {
            Venue expectedVenue = (Venue)getById(actualVenue.getId(),
                    expected.getVenue());
            compare(expectedVenue, actualVenue);
        }
        assertEquals("unexpected number of teams",
                expected.getTeam().size(), actual.getTeam().size());
        for (Team actualTeam : actual.getTeam()) {
            Team expectedTeam = (Team)getById(actualTeam.getId(),
                    expected.getTeam());
            assertNotNull("unexpected team id:" + actualTeam.getId(), 
                    expectedTeam);
            compare(expectedTeam, actualTeam);
        }
    }
    
    private void compare(Venue expected, Venue actual) {
        assertEquals("unexpected id", expected.getId(), actual.getId());
        assertEquals("unexpected ADC page", 
                expected.getAdcPage(), actual.getAdcPage());
        assertEquals("unexpected directions", 
                expected.getDirections(), actual.getDirections());
        assertEquals("unexpected name", 
                expected.getName(), actual.getName());
    }
    
    private void compare(Team expected, Team actual) {
        assertEquals("unexpected id", expected.getId(), actual.getId());
        assertEquals("unexpected name", expected.getName(), actual.getName());
        assertEquals("unexpected number of contacts",
                expected.getContactRole().size(), 
                actual.getContactRole().size());
        for(ContactRoleType actualContact : actual.getContactRole()) {
            ContactRoleType expectedContact = (ContactRoleType)
                getById(actualContact.getId(), expected.getContactRole());
            compare(expectedContact, actualContact);
        }
    }
    
    private void compare(Season expected, Season actual) {
        assertEquals("unexpected id", expected.getId(), actual.getId());
        assertEquals("unexpected starts date", 
                expected.getStarts().getTime(), actual.getStarts().getTime());
        assertEquals("unexpected ends date", 
                expected.getEnds().getTime(), actual.getEnds().getTime());
        assertEquals("unexpected name",
                expected.getName(), actual.getName());
        assertEquals("unexpected number of divisions", 
                expected.getDivision().size(), actual.getDivision().size());
        for (Division actualDivision : actual.getDivision()) {
            Division expectedDivision = (Division)
                getById(actualDivision.getId(), expected.getDivision());
            compare(expectedDivision, actualDivision);
        }
        assertEquals("unexpected number of contests", 
                expected.getContest().size(), actual.getContest().size());
        for (Contest actualContest : actual.getContest()) {
            Contest expectedContest = (Contest)
                getById(actualContest.getId(), expected.getContest());
            compare(expectedContest, actualContest);
        }
    }
    
    private void compare(Division expected, Division actual) {
        assertEquals("unexpected id", expected.getId(), actual.getId());
        assertEquals("unexpected group", 
                expected.getGroup(), actual.getGroup());
        assertEquals("unexpected level", 
                expected.getLevel(), actual.getLevel());
        assertEquals("unexpected ranking", 
                expected.getRanking(), actual.getRanking());
        assertEquals("unexpected number of division teams", 
                expected.getTeamSeason().size(), actual.getTeamSeason().size());
        compare(expected.getContact(), actual.getContact());
        for(TeamSeason actualTeamSeason : actual.getTeamSeason()) {
            TeamSeason expectedTeamSeason = (TeamSeason)
                getById(actualTeamSeason.getId(), expected.getTeamSeason());
            compare(expectedTeamSeason, actualTeamSeason);
        }
    }
    
    private void compare(TeamSeason expected, TeamSeason actual) {
        assertEquals("unexpected id", expected.getId(), actual.getId());
        compare((Team)expected.getTeam(), (Team)actual.getTeam());
    }
    
    private void compare(Contest expected, Contest actual) {
        assertEquals("unexpected id", expected.getId(), actual.getId());
        compare((Venue)expected.getLocation(), (Venue)actual.getLocation());
        assertEquals("unexpected start time; expected=" + expected.getStarts() +
                ", actual=" + actual.getStarts(), 
                expected.getStarts().getTime(), actual.getStarts().getTime());
        assertEquals("unexpected duration", 
                expected.getDuration(), actual.getDuration());
        compare(
                (TeamSeason)expected.getHomeTeam(), 
                (TeamSeason)actual.getHomeTeam());
        compare(
                (TeamSeason)expected.getAwayTeam(), 
                (TeamSeason)actual.getAwayTeam());
        assertEquals("unexpected home score",
                expected.getHomeScore(), actual.getHomeScore());
        assertEquals("unexpected away score",
                expected.getAwayScore(), actual.getAwayScore());
    }
}
