package ejava.examples.txhotel.jpa;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;

/** 
 * This class tests the ReservationDAO. The code contained here will be 
 * similar to the calls made by the Hotel Reservation business logic.
 *
 * @author jcstaff
 */
public class HotelRegistrationDAOTest extends DAOTestBase {
    
    /*
     * This will test the ability to create a new Reservation and Person
     */
    public void testCreateRegistration() {
        log.info("*** testCreateRegistration ***");

        Reservation reservation = new Reservation();
        reservation.setConfirmation("1234");
        reservation.setStartDate(new Date(System.currentTimeMillis()));
        reservation.setEndDate(
                new Date(System.currentTimeMillis()+(60L*60L*24L)));
        reservation.setPerson(new Person(0,0,"joe", "smith"));
                       
        reservation = reservationDAO.createReservation(reservation);
        assertTrue("reservation id not assigned", reservation.getId()!=0);
        assertTrue("person id not assigned", reservation.getPerson().getId()!=0);        
    }

    /**
     * This will test the ability to create a new Reservation for an 
     * existing person.
     *
     */
    public void testMergePeople() {
        log.info("*** testMergePeople ***");

        Person person1 = new Person(0,0,"joe", "smith");
        
        Reservation reservation1 = new Reservation();
        reservation1.setConfirmation("1234");
        reservation1.setStartDate(new Date(System.currentTimeMillis()));
        reservation1.setEndDate(
                new Date(System.currentTimeMillis()+(60L*60L*24L)));
        reservation1.setPerson(person1);
                       
        reservation1 = reservationDAO.createReservation(reservation1);
        assertTrue("person id not assigned", reservation1.getPerson().getId()!=0);        

        Reservation reservation2 = new Reservation();
        reservation2.setConfirmation("4321");
        reservation2.setStartDate(new Date(System.currentTimeMillis()));
        reservation2.setEndDate(
                new Date(System.currentTimeMillis()+(60L*60L*24L)));
        reservation2.setPerson(person1);

        reservation1 = reservationDAO.createReservation(reservation1);
        assertEquals("unexpected person", person1.getId(), 
                reservation2.getPerson().getId());        
    }
    
    public void testGetReservation() {
        log.info("*** testGetReservation ***");

        Person person1 = new Person(0,0,"joe", "smith");
        Reservation reservation1 = new Reservation();
        reservation1.setConfirmation("1234");
        reservation1.setStartDate(new Date(System.currentTimeMillis()));
        reservation1.setEndDate(
                new Date(System.currentTimeMillis()+(60L*60L*24L)));
        reservation1.setPerson(person1);
                       
        reservation1 = reservationDAO.createReservation(reservation1);
        Reservation reservation2 = reservationDAO.getReservation(
                reservation1.getId());
        assertNotNull(reservation2);
    }

    public void testRemoveReservation() {
        log.info("*** testRemoveReservation ***");

        Person person1 = new Person(0,0,"joe", "smith");
        Reservation reservation1 = new Reservation();
        reservation1.setConfirmation("1234");
        reservation1.setStartDate(new Date(System.currentTimeMillis()));
        reservation1.setEndDate(
                new Date(System.currentTimeMillis()+(60L*60L*24L)));
        reservation1.setPerson(person1);
                       
        reservation1 = reservationDAO.createReservation(reservation1);
        
        List<Reservation> reservations1 = reservationDAO.getReservations(0,100);

        reservationDAO.removeReservation(reservation1);
        
        List<Reservation> reservations2 = reservationDAO.getReservations(0,100);
        assertEquals("unexpected number of reservations:" + reservations2.size(),
                reservations1.size()-1, reservations2.size());
    }
    
    public void testQueryByName() {
        log.info("*** testQueryByName ***");

        Person person1 = new Person(0,0,"joe", "smith");
        Reservation reservation1 = new Reservation();
        reservation1.setConfirmation("1234");
        reservation1.setStartDate(new Date(System.currentTimeMillis()));
        reservation1.setEndDate(
                new Date(System.currentTimeMillis()+(60L*60L*24L)));
        reservation1.setPerson(person1);                       
        reservation1 = reservationDAO.createReservation(reservation1);
        
        reservationDAO.createReservation(new Reservation(
                0,0,"4321",person1, new Date(), new Date()));
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("firstName", person1.getFirstName());
        params.put("lastName", person1.getLastName());
        List<Reservation> reservations1 = reservationDAO.getReservations(
                "getReservationsByName", params, 0, 100);

        assertEquals("unexpected number of reservations:" + reservations1.size(),
                2, reservations1.size());
    }

    public void testQueryByConfirmation() {
        log.info("*** testByConfirmation ***");

        Person person1 = new Person(0,0,"joe", "smith");
        Reservation reservation1 = new Reservation();
        reservation1.setConfirmation("1234");
        reservation1.setStartDate(new Date(System.currentTimeMillis()));
        reservation1.setEndDate(
                new Date(System.currentTimeMillis()+(60L*60L*24L)));
        reservation1.setPerson(person1);                       
        reservation1 = reservationDAO.createReservation(reservation1);
        
        reservationDAO.createReservation(new Reservation(
                0,0,"4321",person1, new Date(), new Date()));
        
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("confirmation", reservation1.getConfirmation());
        List<Reservation> reservations1 = reservationDAO.getReservations(
                "getReservationsByConfirmation", params, 0, 100);
        assertEquals("unexpected number of reservations:" + reservations1.size(),
                1, reservations1.size());

        params.put("confirmation", "4321");
        reservations1 = reservationDAO.getReservations(
                "getReservationsByConfirmation", params, 0, 100);
        assertEquals("unexpected number of reservations:" + reservations1.size(),
                1, reservations1.size());

        params.put("confirmation", "fake");
        reservations1 = reservationDAO.getReservations(
                "getReservationsByConfirmation", params, 0, 100);
        assertEquals("unexpected number of reservations:" + reservations1.size(),
                0, reservations1.size());
    }
    
    
}
