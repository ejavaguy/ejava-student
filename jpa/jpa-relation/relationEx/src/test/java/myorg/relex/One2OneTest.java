package myorg.relex;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.persistence.TemporalType;

import myorg.relex.one2one.Applicant;
import myorg.relex.one2one.Application;
import myorg.relex.one2one.Attendee;
import myorg.relex.one2one.Auto;
import myorg.relex.one2one.Auto2;
import myorg.relex.one2one.Coach;
import myorg.relex.one2one.Driver;
import myorg.relex.one2one.Driver2;
import myorg.relex.one2one.Employee;
import myorg.relex.one2one.License;
import myorg.relex.one2one.LicenseApplication;
import myorg.relex.one2one.Member;
import myorg.relex.one2one.Passenger;
import myorg.relex.one2one.Person;
import myorg.relex.one2one.Player;
import myorg.relex.one2one.Residence;
import myorg.relex.one2one.ShowEvent;
import myorg.relex.one2one.ShowEventPK;
import myorg.relex.one2one.ShowTickets;
import myorg.relex.one2one.BoxOffice;
import myorg.relex.one2one.Ticket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.*;

/**
 * Verifies several one-to-one relationships.
 */
public class One2OneTest extends JPATestBase {
    private static Logger log = LoggerFactory.getLogger(One2OneTest.class);
    
    /**
     * This test provides a demonstration of a simple one-to-one, uni-directional
     * relationship realized through a foreign key join (FK-join).
     * The ID of the parent and dependent classes are independent of one
     * another and the dependent maintains a separate FK to reference the
     * parent.
     */
    @Test
    public void testOne2OneUniFK() {
        log.info("*** testOne2OneUniFK ***");
        Person person = new Person();
        person.setName("Johnny Unitas");
        Player player = new Player();
        player.setPerson(person);
        player.setPosition(Player.Position.OFFENSE);
        em.persist(person);
        em.persist(player); //provider will propagate person.id to player.FK
        
        //clear the persistence context and get new instances
        em.flush(); em.clear();
        Player player2 = em.find(Player.class, player.getId());
        assertEquals("unexpected position", player.getPosition(), player2.getPosition());
        assertEquals("unexpected name", player.getPerson().getName(), player2.getPerson().getName());
        
        //verify the contents of the database tables, columns, and relationships
        Object[] cols = (Object[]) em.createNativeQuery(
        		"select person.id person_id, person.name, " +
        		       "player.id player_id, player.person_id player_person_id " +
        		"from RELATIONEX_PLAYER player " +
                "join RELATIONEX_PERSON person on person.id = player.person_id " +
                "where player.id = ?1")
                .setParameter(1, player.getId())
                .getSingleResult();
        log.info("row=" + Arrays.toString(cols));
        assertEquals("unexpected person_id", person.getId(), ((Number)cols[0]).intValue());
        assertEquals("unexpected person_name", person.getName(), (String)cols[1]);
        assertEquals("unexpected player_id", player.getId(), ((Number)cols[2]).intValue());
        assertEquals("unexpected player_person_id", person.getId(), ((Number)cols[3]).intValue());
        
        //remove the objects and flush commands to the database
        em.remove(player2);
        em.remove(player2.getPerson());
        em.flush();
        assertNull("person not deleted", em.find(Person.class, person.getId()));
        assertNull("player not deleted", em.find(Player.class, player.getId()));
    }

    /**
     * This test provides a demonstration of a simple one-to-one, uni-directional
     * relationship realized through a separate join table. The ID of the parent and 
     * dependent are independent of one another and these values are kept in a separate
     * table to realize the relationship.
     */
    @Test
    public void testOne2OneUniJoinTable() {
        log.info("*** testOne2OneUniJoinTable ***");
        Person person = new Person();
        person.setName("Joe Smith");
        Member member = new Member(person);
        member.setRole(Member.Role.SECONDARY);
        em.persist(person);
        em.persist(member); //provider will propagate person.id to player.FK
        
        //clear the persistence context and get new instances
        em.flush(); em.clear();
        Member member2 = em.find(Member.class, member.getId());
        assertEquals("unexpected role", member.getRole(), member2.getRole());
        assertEquals("unexpected name", member.getPerson().getName(), member2.getPerson().getName());
        
        //verify the contents of the database tables, columns, and relationships
        Object[] cols = (Object[]) em.createNativeQuery(
        		"select person.id person_id, person.name, " +
        		       "member.id member_id, member.role member_role, " +
        		       "link.member_id link_member, link.person_id link_person " +
        		"from RELATIONEX_MEMBER member " +
        		"join RELATIONEX_MEMBER_PERSON link on link.member_id = member.id " +
                "join RELATIONEX_PERSON person      on link.person_id = person.id " +
                "where member.id = ?1")
                .setParameter(1, member.getId())
                .getSingleResult();
        log.info("row=" + Arrays.toString(cols));
        assertEquals("unexpected person_id", person.getId(), ((Number)cols[0]).intValue());
        assertEquals("unexpected person_name", person.getName(), (String)cols[1]);
        assertEquals("unexpected member_id", member.getId(), ((Number)cols[2]).intValue());
        assertEquals("unexpected member_role", member.getRole().name(), (String)cols[3]);
        assertEquals("unexpected link_member_id", member.getId(), ((Number)cols[4]).intValue());
        assertEquals("unexpected link_person_id", person.getId(), ((Number)cols[5]).intValue());
        
        //remove the objects and flush commands to the database
        em.remove(member2);
        em.remove(member2.getPerson());
        em.flush();
        assertNull("person not deleted", em.find(Person.class, person.getId()));
        assertNull("member not deleted", em.find(Member.class, member.getId()));
    }
    
    
    
    /**
     * This test provides a demonstration of a one-to-one primary key join.
     * In this case, the child and parent tables using the same primary key value
     * and the child re-uses its PK-column as the FK-column. No separate FK-column
     * is created in the child -- unlike the one-to-one FK-join case above. However,
     * the provider does not automatically propagate the parent.PK to the dependent.PK
     * during the persist().  
     */
    @Test
    public void testOne2OneUniPKJ() {
        log.info("*** testOne2OneUniPKJ ***");
        Person person = new Person();
        person.setName("Ozzie Newsome");
        em.persist(person);
        em.flush(); //generate the PK for the person
        
        Employee employee = new Employee(person);//set PK/FK -- provider will not auto propagate
    	employee.setHireDate(new GregorianCalendar(1996, Calendar.JANUARY, 1).getTime());
        //em.persist(person);
        em.persist(employee);
        
        //clear the persistence context and get new instances
        em.flush(); em.clear();
        Employee employee2 = em.find(Employee.class, employee.getPerson().getId());
        log.info("calling person...");
        assertEquals("unexpected name", employee.getPerson().getName(), employee2.getPerson().getName());
        
        //verify the contents of the database tables, columns, and relationships
        Object[] cols = (Object[]) em.createNativeQuery(
        		"select person.id person_id, person.name, " +
        		       "employee.id employee_id " +
        		"from RELATIONEX_EMPLOYEE employee " +
                "join RELATIONEX_PERSON person on person.id = employee.id " +
                "where employee.id = ?1")
                .setParameter(1, employee.getId())
                .getSingleResult();
        log.info("row=" + Arrays.toString(cols));
        assertEquals("unexpected person_id", person.getId(), ((Number)cols[0]).intValue());
        assertEquals("unexpected person_name", person.getName(), (String)cols[1]);
        assertEquals("unexpected employee_id", employee.getId(), ((Number)cols[2]).intValue());
        
        //remove the objects and flush commands to the database
        em.remove(employee2);
        em.remove(employee2.getPerson());
        em.flush();
        assertNull("person not deleted", em.find(Person.class, person.getId()));
        assertNull("employee not deleted", em.find(Employee.class, employee.getId()));
    }
    
    /**
     * This test provides a demonstration of a one-to-on primary key join. 
     * In this case, the entity is configured to use the foreign key mapping as the
     * primary key using @MapsId. This works much easier than the case where the PK is used
     * as the FK.  The provider automatically propagates the parent.PK to the dependent.FK,
     * which is re-used to be the dependent.PK.
     */
    @Test
    public void testOne2OneUniMapsId() {
        log.info("*** testOne2OneUniMapsId ***");
        Person person = new Person();
        person.setName("John Harbaugh");
        Coach coach = new Coach(person);
        coach.setType(Coach.Type.HEAD);
        em.persist(person);
        em.persist(coach); //provider auto propagates person.id to coach.FK mapped to coach.PK 
        
        //flush commands to database, clear cache, and pull back new instance
        em.flush(); em.clear();
        Coach coach2 = em.find(Coach.class, coach.getId());
        log.info("calling person...");
        assertEquals("unexpected name", coach.getPerson().getName(), coach2.getPerson().getName());

        //verify the contents of the database tables, columns, and relationships
        Object[] cols = (Object[]) em.createNativeQuery(
        		"select person.id person_id, person.name, " +
        		       "coach.person_id coach_id " +
        		"from RELATIONEX_COACH coach " +
                "join RELATIONEX_PERSON person on person.id = coach.person_id " +
                "where coach.person_id = ?1")
                .setParameter(1, coach.getId())
                .getSingleResult();
        log.info("row=" + Arrays.toString(cols));
        assertEquals("unexpected person_id", person.getId(), ((Number)cols[0]).intValue());
        assertEquals("unexpected person_name", person.getName(), (String)cols[1]);
        assertEquals("unexpected coach_id", coach.getId(), ((Number)cols[2]).intValue());
        
        //remove the objects and flush commands to the database
        em.remove(coach2);
        em.remove(coach2.getPerson());
        em.flush();
        assertNull("person not deleted", em.find(Person.class, person.getId()));
        assertNull("coach not deleted", em.find(Coach.class, coach.getId()));
    }
    
    private Calendar toCalendar(Date date) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal;
    }
    
    /**
     * This test provides a demonstration of creating a one-to-one, uni-directional
     * relationship to a parent class that uses a composite primary key mapped thru an @IdClass
     */
    @Test
    public void testOne2OneUniIdClass() {
        log.info("*** testOne2OneUniIdClass ***");
        Date showDate = new GregorianCalendar(1975+new Random().nextInt(100),
        		Calendar.JANUARY, 1).getTime();
        Date showTime = new GregorianCalendar(0, 0, 0, 0, 0, 0).getTime();
        ShowEvent show = new ShowEvent(showDate, showTime);
        show.setName("Rocky Horror");
        ShowTickets tickets = new ShowTickets(show); //parent already has natural PK by this point
        tickets.setTicketsLeft(300);
        em.persist(show);
        em.persist(tickets);  
        
        //flush commands to database, clear cache, and pull back new instance
        em.flush(); em.clear();
        ShowTickets tickets2 = em.find(ShowTickets.class, new ShowEventPK(tickets.getDate(), tickets.getTime()));
        log.info("calling parent...");
        assertEquals("unexpected name", tickets.getShow().getName(), tickets2.getShow().getName());

        //verify the contents of the database tables, columns, and relationships
        Object[] cols = (Object[]) em.createNativeQuery(
        		"select show.date show_date, show.time show_time, " +
        		       "tickets.ticket_date ticket_date, tickets.ticket_time ticket_time, tickets.tickets " +
        		"from RELATIONEX_SHOWEVENT show " +
            "join RELATIONEX_SHOWTICKETS tickets on show.date = tickets.ticket_date and show.time = tickets.ticket_time " +
            "where tickets.ticket_date = ?1 and tickets.ticket_time = ?2")
                .setParameter(1, tickets.getShow().getDate(), TemporalType.DATE)
//                HHH-11875 - Incorrect parameter binding for java.util.Date and TemporalType
//                .setParameter(2, tickets.getShow().getTime(), TemporalType.TIME)
                .setParameter(2, toCalendar(tickets.getTime()), TemporalType.TIME) //bug prevents TemporalType.TIME from working with Java.util.Date 
                .getSingleResult();
        log.info("row=" + Arrays.toString(cols));
        assertEquals("unexpected show_date", tickets2.getShow().getDate(), (Date)cols[0]);
        assertEquals("unexpected show_time", tickets2.getShow().getTime(), (Date)cols[1]);
        assertEquals("unexpected ticket_date", tickets2.getDate(), (Date)cols[2]);
        assertEquals("unexpected ticket_time", tickets2.getTime(), (Date)cols[3]);
        assertEquals("unexpected ticketsLeft", tickets2.getTicketsLeft(), ((Number)cols[4]).intValue());
        
        
        //remove the objects and flush commands to the database
        em.remove(tickets2);
        em.remove(tickets2.getShow());
        em.flush();
        assertNull("tickets not deleted", em.find(ShowEvent.class, 
        		new ShowEventPK(show.getDate(), show.getTime())));
        assertNull("show not deleted", em.find(ShowTickets.class, 
        		new ShowEventPK(tickets.getDate(), tickets.getTime())));
    }
    

    /**
     * This test provides a demonstration of creating a one-to-one, uni-directional
     * relationship to a parent class that uses a composite primary key mapped thru and 
     * @EmbeddedId
     */
    @Test
    public void testOne2OneUniEmbeddedId() {
        log.info("*** testOne2OneUniEmbedded ***");
        Date showDate = new GregorianCalendar(1975+new Random().nextInt(100),
        		Calendar.JANUARY, 1).getTime();
        Date showTime = new GregorianCalendar(0, 0, 0, 0, 0, 0).getTime();
        ShowEvent show = new ShowEvent(showDate, showTime);
        show.setName("Rocky Horror");
        BoxOffice boxOffice = new BoxOffice(show);
        boxOffice.setTicketsLeft(500);
        em.persist(show);
        em.persist(boxOffice); //provider auto propagates parent.cid to dependent.FK mapped to dependent.cid 
        
        //flush commands to database, clear cache, and pull back new instance
        em.flush(); em.clear();
        BoxOffice boxOffice2 = em.find(BoxOffice.class, new ShowEventPK(boxOffice.getDate(), boxOffice.getTime()));
        log.info("calling parent...");
        assertEquals("unexpected name", boxOffice.getShow().getName(), boxOffice2.getShow().getName());

        //verify the contents of the database tables, columns, and relationships
        Object[] cols = (Object[]) em.createNativeQuery(
        		"select show.date show_date, show.time show_time, " +
        		       "tickets.show_date ticket_date, tickets.show_time ticket_time, tickets.tickets " +
        		"from RELATIONEX_SHOWEVENT show " +
                "join RELATIONEX_BOXOFFICE tickets on show.date = tickets.show_date and show.time = tickets.show_time " +
                "where tickets.show_date = ?1 and tickets.show_time = ?2")
                .setParameter(1, boxOffice.getShow().getDate(), TemporalType.DATE)
//              HHH-11875 - Incorrect parameter binding for java.util.Date and TemporalType
//              .setParameter(2, boxOffice.getShow().getTime(), TemporalType.TIME)
               .setParameter(2, toCalendar(boxOffice.getShow().getTime()), TemporalType.TIME) //bug prevents TemporalType.TIME from working with Java.util.Date 
                .getSingleResult();
        log.info("row=" + Arrays.toString(cols));
        assertEquals("unexpected show_date", boxOffice2.getShow().getDate(), (Date)cols[0]);
        assertEquals("unexpected show_time", boxOffice2.getShow().getTime(), (Date)cols[1]);
        assertEquals("unexpected ticket_date", boxOffice2.getDate(), (Date)cols[2]);
        assertEquals("unexpected ticket_time", boxOffice2.getTime(), (Date)cols[3]);
        assertEquals("unexpected ticketsLeft", boxOffice2.getTicketsLeft(), ((Number)cols[4]).intValue());        
        
        //remove the objects and flush commands to the database
        em.remove(boxOffice2);
        em.remove(boxOffice2.getShow());
        em.flush();
        assertNull("tickets not deleted", em.find(ShowEvent.class, 
        		new ShowEventPK(show.getDate(), show.getTime())));
        assertNull("show not deleted", em.find(BoxOffice.class, 
        		new ShowEventPK(boxOffice.getDate(), boxOffice.getTime())));
    }
    
    
    /**
     * This test provides a demonstration of creating a one-to-one
     * bi-directional relationship using entities that will share
     * a generated primary key value and be joined by their common
     * primary key value.
     */
    @Test
    public void testOne2OneBiPKJ() {
        log.info("*** testOne2OneBiPKJ() ***");
        Applicant applicant = new Applicant();
        applicant.setName("Jason Garret");
        Application application = new Application(applicant);
        application.setDesiredStartDate(new GregorianCalendar(2008, Calendar.JANUARY, 1).getTime());
        em.persist(applicant);   //provider will generate a PK
        em.persist(application); //provider will propogate parent.PK to dependent.FK/PK
        
        //clear the persistence context and get new instances from the owning side
        em.flush(); em.clear();
        log.info("finding dependent...");
        Application application2 = em.find(Application.class, application.getId());
        log.info("found dependent...");
        assertTrue("unexpected startDate", 
        		application.getDesiredStartDate().equals(
        		application2.getDesiredStartDate()));
        log.info("calling parent...");
        assertEquals("unexpected name", application.getApplicant().getName(), application2.getApplicant().getName());
        
        //clear the persistence context and get new instances from the inverse side
        em.flush(); em.clear();
        log.info("finding parent...");
        Applicant applicant2 = em.find(Applicant.class, applicant.getId());
        log.info("found parent...");
        assertEquals("unexpected name", applicant.getName(), applicant2.getName());
        log.info("calling dependent...");
        assertTrue("unexpected startDate", 
        		applicant.getApplication().getDesiredStartDate().equals(
        		applicant2.getApplication().getDesiredStartDate()));
        
        //remove the objects and flush commands to the database
        em.remove(applicant2.getApplication());
        em.remove(applicant2);
        em.flush();
        assertNull("applicant not deleted", em.find(Applicant.class, applicant2.getId()));
        assertNull("application not deleted", em.find(Application.class, applicant2.getApplication().getId()));
    }
    
    /**
     * This test provides a demonstration of creating a one-to-one bi-directional
     * relationship where the inverse/parent side of the relation has a 0..1 with 
     * the owning/dependent side.
     */
    @Test
    public void testOne2OneBiOwningOptional() {
        log.info("*** testOne2OneBiOwningOptional() ***");
        Auto auto = new Auto();           //auto is inverse/parent side
        auto.setType(Auto.Type.CAR);
        Driver driver = new Driver(auto); //driver is owning/dependent side
        driver.setName("Danica Patrick");
        auto.setDriver(driver); //application must maintain inverse side
        em.persist(auto);
        em.persist(driver);

        //clear the persistence context and get new instances from the owning side
        em.flush(); em.clear();
        log.info("finding dependent...");
        Driver driver2 = em.find(Driver.class, driver.getId());
        log.info("found dependent...");
        assertEquals("unexpected name", driver.getName(), driver2.getName());
        log.info("calling parent...");
        assertEquals("unexpected name", driver.getAuto().getType(), driver2.getAuto().getType());

        //clear the persistence context and get new instances from the inverse side
        em.flush(); em.clear();
        log.info("finding parent...");
        Auto auto2 = em.find(Auto.class, auto.getId());
        log.info("found parent...");
        assertEquals("unexpected type", auto.getType(), auto.getType());
        log.info("calling dependent...");
        assertEquals("unexpected name", auto.getDriver().getName(), auto2.getDriver().getName());
        
        //create a new auto for the current driver to be switched to
        Auto truck = new Auto();
        truck.setType(Auto.Type.TRUCK);
        em.persist(truck);
        driver = em.find(Driver.class, driver.getId()); //get the managed instance
        driver.setAuto(truck);
        truck.setDriver(driver);
        
        em.flush(); em.clear();
        Auto auto3 = em.find(Auto.class, auto.getId());
        Driver driver3 = em.find(Driver.class, driver.getId());
        Auto truck3 = em.find(Auto.class, truck.getId());
        assertNull("driver not removed from auto", auto3.getDriver());
        assertEquals("driver not assigned to truck", truck.getId(), driver3.getAuto().getId());
        assertEquals("truck not assigned to driver", driver.getId(), truck3.getDriver().getId());
        
        //remove remaining object(s) from database
        em.remove(truck3.getDriver());
        em.remove(truck3);
        em.remove(auto3);
        em.flush();
        assertNull("driver not deleted", em.find(Driver.class, truck3.getDriver().getId()));
        assertNull("auto not deleted", em.find(Auto.class, auto.getId()));
        assertNull("truck not deleted", em.find(Auto.class, truck.getId()));
    }

    /**
     * This test provides a demonstration of making the inverse/parent side of 
     * a relationship optional and the owning/dependent side required.
     */
    @Test
    public void testOne2OneBiInverseOptional() {
        log.info("*** testOne2OneBiInverseOptional() ***");
        Auto2 auto = new Auto2();           //auto is owning/dependent side
        auto.setType(Auto2.Type.CAR);
        Driver2 driver = new Driver2(auto); //driver is inverse/parent side
        driver.setName("Danica Patrick");
        auto.setDriver(driver);           //owning side must be set
        em.persist(driver);
        em.persist(auto);
        em.flush();

        //clear the persistence context and get new instances from the inverse side
        em.flush(); em.clear();
        log.info("finding parent...");
        Driver2 driver2 = em.find(Driver2.class, driver.getId());
        log.info("found parent...");
        assertEquals("unexpected name", driver.getName(), driver2.getName());
        log.info("calling dependent...");
        assertEquals("unexpected name", driver.getAuto().getType(), driver2.getAuto().getType());

        //clear the persistence context and get new instances from the owning side
        em.flush(); em.clear();
        log.info("finding dependent...");
        Auto2 auto2 = em.find(Auto2.class, auto.getId());
        log.info("found dependent...");
        assertEquals("unexpected type", auto.getType(), auto.getType());
        log.info("calling parent...");
        assertEquals("unexpected name", auto.getDriver().getName(), auto2.getDriver().getName());

        //create a new auto for the current driver to be switched to
        Auto2 truck = new Auto2();
        truck.setType(Auto2.Type.TRUCK);
        driver = em.find(Driver2.class, driver.getId()); //get the managed instance
        driver.setAuto(truck);
        auto2.setDriver(null);  //must remove reference to former driver
        truck.setDriver(driver);//prior to assigning to new driver for 1:1
        em.persist(truck);
        
        em.flush(); em.clear();
        Auto2 auto3 = em.find(Auto2.class, auto.getId());
        Driver2 driver3 = em.find(Driver2.class, driver.getId());
        Auto2 truck3 = em.find(Auto2.class, truck.getId());
        assertNull("driver not removed from auto", auto3.getDriver());
        assertEquals("driver not assigned to truck", truck.getId(), driver3.getAuto().getId());
        assertEquals("truck not assigned to driver", driver.getId(), truck3.getDriver().getId());
        
        //remove remaining object(s) from database
        em.remove(truck3);
        em.remove(auto3);
        em.remove(truck3.getDriver());
        em.flush();
        assertNull("driver not deleted", em.find(Driver.class, truck3.getDriver().getId()));
        assertNull("auto not deleted", em.find(Auto.class, auto.getId()));
        assertNull("truck not deleted", em.find(Auto.class, truck.getId()));
    }
    
    @Test @Ignore
    public void testOne2OneOrphan() {
        log.info("*** testOne2OneOrphan ***");
    }
    
    /**
     * This test demonstrates use of cascades in a one-to-one 
     * uni-directional relationship or one where all cascades come
     * from the owning/dependent side.
     */
    @Test
    public void testOne2OneCascadeFromOwner() {
        log.info("*** testOne2OneCascadeFromOwner ***");
        License license = new License();
        license.setRenewal(new GregorianCalendar(2012,1,1).getTime());
        LicenseApplication licapp = new LicenseApplication(license);
        licapp.setUpdated(new Date());
        em.persist(licapp);
        em.flush();
        
        //detach the current instances and obtain new instances
        assertTrue("licapp was not managed???", em.contains(licapp));
        assertTrue("license was not managed???", em.contains(license));
        em.detach(licapp);
        assertFalse("licapp still managed", em.contains(licapp));
        assertFalse("license still managed", em.contains(license));
        licapp = em.find(LicenseApplication.class, licapp.getId());
        license = licapp.getLicense();
        
        //perform a bulk update and refresh on the entities to synchronize state
        Date newDate = new GregorianCalendar(2014, 1, 1).getTime();
        Date newUpdate = new Date(licapp.getUpdated().getTime()+1);
        assertEquals("unexpected update count", 1, 
          em.createQuery("update License lic set lic.renewal=:renewal where lic.id=:id")
        	.setParameter("renewal", newDate, TemporalType.DATE)
        	.setParameter("id", license.getId())
        	.executeUpdate());
        assertEquals("unexpected update count", 1, 
                em.createQuery("update LicenseApplication licapp set licapp.updated=:updated where licapp.id=:id")
              	.setParameter("updated", newUpdate, TemporalType.TIMESTAMP)
              	.setParameter("id", licapp.getId())
              	.executeUpdate());
        assertFalse("unexpected updated value prior to refresh", 
        		licapp.getUpdated().getTime() == newUpdate.getTime());
        assertFalse("unexpected renewal value prior to refresh", 
        		license.getRenewal().getTime() == newDate.getTime());
        log.info("database updated");
        em.refresh(licapp);
        log.info("entities refreshed");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        assertTrue(String.format("licapp not refreshed, exp=%s, act=%s", df.format(newUpdate), df.format(licapp.getUpdated())), 
        		licapp.getUpdated().getTime() == newUpdate.getTime());
        assertTrue(String.format("license not refreshed, exp=%s, act=%s", df.format(newDate), df.format(license.getRenewal())), 
        		license.getRenewal().getTime() == newDate.getTime());
        
        //detach, change, and merge changes from the detached entities
        em.detach(licapp);
        newDate = new GregorianCalendar(2016, 1, 1).getTime();
        newUpdate = new Date(licapp.getUpdated().getTime()+1);
        assertFalse("licapp still managed", em.contains(licapp));
        assertFalse("license still managed", em.contains(licapp.getLicense()));
        licapp.setUpdated(newUpdate);
        licapp.getLicense().setRenewal(newDate);
        log.info("merging changes to detached entities");
        licapp=em.merge(licapp);
        em.flush();
        log.info("merging complete");
        assertTrue("merged licapp not managed", em.contains(licapp));
        assertTrue("merged licapp.license not managed", em.contains(licapp.getLicense()));
        assertTrue(String.format("licapp not merged, exp=%s, act=%s", df.format(newUpdate), df.format(licapp.getUpdated())), 
        		licapp.getUpdated().getTime() == newUpdate.getTime());
        assertTrue(String.format("license not merged, exp=%s, act=%s", df.format(newDate), df.format(license.getRenewal())), 
        		licapp.getLicense().getRenewal().getTime() == newDate.getTime());
        
        //delete remaining objects
        em.remove(licapp);
        em.flush();
        assertNull("licapp not deleted", em.find(LicenseApplication.class, licapp.getId()));
        assertNull("licapp.license not deleted", em.find(License.class, licapp.getLicense().getId()));
    }
    
    /**
     * This test demonstrates use of cascades in a one-to-one 
     * bi-directional relationship where all cascades come
     * from the inverse/parent side of the relationship.
     */
    @Test
    public void testOne2OneCascadeFromInverse() {
        log.info("*** testOne2OneCascadeFromInverse ***");
        Ticket ticket = new Ticket();
        ticket.setDate(new GregorianCalendar(2013, Calendar.MARCH, 16).getTime());
        Passenger passenger = new Passenger(ticket, "Fred"); //set inverse side
        ticket.setPassenger(passenger);                //set the owning side
        em.persist(ticket);                            //persist from inverse side
        em.flush();
        assertTrue("ticket not managed", em.contains(ticket));
        assertTrue("passenger not managed", em.contains(passenger));
        
        log.debug("detach both instances from the persistence context");
        em.detach(ticket);
        assertFalse("ticket managed", em.contains(ticket));
        assertFalse("passenger managed", em.contains(passenger));
        
        log.debug("perform a bulk update to both objects");
        ticket = em.find(Ticket.class, ticket.getId());
        Date newDate=new GregorianCalendar(2013, Calendar.APRIL, 1).getTime();
        String newName = "Frederick";
        em.createQuery("update Ticket t set t.date=:date")
          .setParameter("date", newDate,TemporalType.DATE)
          .executeUpdate();
        em.createQuery("update Passenger p set p.name=:name where p.name='Fred'")
           .setParameter("name", newName)
           .executeUpdate();
        assertFalse("unexpected date", newDate.equals(ticket.getDate()));
        assertFalse("unexpected name", newName.equals(ticket.getPassenger().getName()));
        em.refresh(ticket);
        assertTrue("date not refreshed", newDate.equals(ticket.getDate()));
        assertTrue("name not refreshed", newName.equals(ticket.getPassenger().getName()));
        
        log.debug("merging changes from inverse side");
        Ticket ticket2 = new Ticket(ticket.getId());
        ticket2.setDate(new GregorianCalendar(2014, Calendar.APRIL, 1).getTime());
        Passenger passenger2 = new Passenger(passenger.getId());
        passenger2.setName("Rick");
        ticket2.setPassenger(passenger2);
        passenger2.setTicket(ticket2);
        assertFalse("unexpected date", ticket2.getDate().equals(ticket.getDate()));
        assertFalse("unexpected name", ticket2.getPassenger().getName().equals(ticket.getPassenger().getName()));
        ticket=em.merge(ticket2);
        em.flush();
        assertTrue("date not merged", ticket2.getDate().equals(ticket.getDate()));
        assertTrue("name not merged", ticket2.getPassenger().getName().equals(ticket.getPassenger().getName()));
        
        log.debug("delete the entities from the inverse side");
        assertNotNull("ticket not found", em.find(Ticket.class, ticket.getId()));
        assertNotNull("passenger not found", em.find(Passenger.class, ticket.getPassenger().getId()));
        em.remove(ticket);
        em.flush();
        assertNull("ticket not removed", em.find(Ticket.class, ticket.getId()));
        assertNull("passenger not removed", em.find(Passenger.class, ticket.getPassenger().getId()));
    }
    
    
    /**
     * This test demonstrates the capability to have the provider automatically
     * delete a parent class when it becomes dereferenced from its dependent in 
     * a relationship.
     */
    @Test
    public void testOrphanRemoval() {
    	log.info("*** testOrphanRemoval ***");
    	
    	log.debug("start by verifying the state of the database");
    	int startCount = em.createQuery("select count(r) from Residence r", Number.class)
    			              .getSingleResult().intValue();
    	log.debug("create a new attendee and residence");
    	Attendee attendee = new Attendee();
    	attendee.setName("jones");
    	attendee.setResidence(new Residence("Columbia", "MD"));
    	em.persist(attendee);
    	em.flush();
    	
    	log.debug("verify we have a new residence in the database");
    	assertEquals("unexpected number of residences", startCount+1,
    			em.createQuery("select count(r) from Residence r", Number.class)
	              .getSingleResult().intValue());
    	log.debug("verify we can find our new instance");
    	int originalId=attendee.getResidence().getId();
    	assertNotNull("could not find residence", em.find(Residence.class, originalId));
    	
    	log.debug("have attendee change residence");
    	//ISSUE: https://hibernate.atlassian.net/browse/HHH-6484
    	//MORE: https://hibernate.atlassian.net/browse/HHH-5559
    	attendee.setResidence(null);
    	em.flush();
    	attendee.setResidence(new Residence("Baltimore", "MD"));
    	em.flush();
    	
    	log.debug("verify we have the same number of residences");
    	assertEquals("unexpected number of residences", startCount+1,
    			em.createQuery("select count(r) from Residence r", Number.class)
	              .getSingleResult().intValue());
    	
    	log.debug("verify the new instance replaced the original instance");
    	assertNull("found original residence", em.find(Residence.class, originalId));
    	assertNotNull("could not find new residence", em.find(Residence.class, attendee.getResidence().getId()));
    	
    	log.debug("remove reference to the current residence");
    	attendee.setResidence(null);
    	//em.flush(); -- note flush is done during follow-on query
    	
    	log.debug("verify all residences created during this test have been deleted");
    	assertEquals("unexpected number of residences", startCount,
    			em.createQuery("select count(r) from Residence r", Number.class)
	              .getSingleResult().intValue());
    }
}
