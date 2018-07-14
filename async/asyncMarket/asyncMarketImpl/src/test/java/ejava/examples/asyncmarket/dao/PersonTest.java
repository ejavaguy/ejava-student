package ejava.examples.asyncmarket.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.asyncmarket.MarketTestBase;
import ejava.examples.asyncmarket.bo.Person;

public class PersonTest extends MarketTestBase {
    Logger log = LoggerFactory.getLogger(PersonTest.class);
    
    @Test
    public void testPerson() {
        log.info("*** testCreatePerson ***");
        
        Person person = new Person();
        person.setName("joe smith");
        person.setUserId("jsmith");
        
        personDAO.createPerson(person);
        
        log.debug("created person:" + person);        
        assertTrue("personId not assigned", person.getId() > 0);
    }
    
    @Test
    public void testGetPerson() {
        log.info("*** testGetPerson ***");
                
        Person p = personDAO.getPerson(10101L);
        assertNull("unexpected person found", p);
        
        Person person = new Person();
        person.setName("joe smith");
        person.setUserId("jsmith");        
        personDAO.createPerson(person);        
        log.debug("created person:" + person);        
        assertTrue("personId not assigned", person.getId() > 0);
        
        Person person2 = personDAO.getPerson(person.getId());
        assertNotNull("person not found", person2);
        
        Person person3 = personDAO.getPersonByUserId(person.getUserId());
        assertNotNull("person not found", person3);        
    }

    @Test
    public void testGetPeople() {
        log.info("*** testGetPeople ***");
        
        for (int i=0; i<10; i++) {
            Person person = new Person();
            person.setName("joe smith");
            person.setUserId("jsmith" + i);        
            personDAO.createPerson(person);        
        }
        
        List<Person> people = personDAO.getPeople(0, 100);
        assertEquals("unexpected number of people found:" + people.size(),
                10, people.size());
    }    
}
