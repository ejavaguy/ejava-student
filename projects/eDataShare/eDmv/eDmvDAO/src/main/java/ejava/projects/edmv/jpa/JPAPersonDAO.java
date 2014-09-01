package ejava.projects.edmv.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import ejava.projects.edmv.bo.Person;
import ejava.projects.edmv.dao.PersonDAO;

/**
 * This class provides a _sparse_ example of a JPA DAO for the class project.
 * It is put in place here to demonstrate some of the end-to-end use cases,
 * 
 * @author jcstaff
 *
 */
public class JPAPersonDAO implements PersonDAO {
	private EntityManager em;
	
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public void createPerson(Person person) {
        em.persist(person);
	}
	
	public Person getPerson(long id) {
	    return em.find(Person.class, id);
	}

	@SuppressWarnings("unchecked")
    public List<Person> getPeople(int index, int count)  {
	    return (List<Person>)em.createQuery("select p from Person p")
	                             .setFirstResult(index)
	                             .setMaxResults(count)
	                             .getResultList();
	}
}
