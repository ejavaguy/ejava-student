package ejava.projects.edmv.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import ejava.projects.edmv.bo.DMVPerson;
import ejava.projects.edmv.dao.DMVPersonDAO;

/**
 * This class provides a JPA DAO implementation for people.
 *
 */
public class JPADMVPersonDAO implements DMVPersonDAO {
    protected EntityManager em;
    
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public DMVPerson getPerson(long id) {
        return em.find(DMVPerson.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<DMVPerson> getPeople(int index, int count) {
        return (List<DMVPerson>) 
              em.createQuery("select p from DMVPerson p")
                  .setFirstResult(index)
                  .setMaxResults(count)
                  .getResultList();
    }
}
