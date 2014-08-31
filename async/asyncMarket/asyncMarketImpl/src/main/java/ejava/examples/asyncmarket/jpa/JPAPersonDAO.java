package ejava.examples.asyncmarket.jpa;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import ejava.examples.asyncmarket.bo.Person;
import ejava.examples.asyncmarket.dao.PersonDAO;

public class JPAPersonDAO implements PersonDAO {
    private static final String GET_ALL_PEOPLE = "AsyncMarket_getAllPeople";    
    private static final String GET_PEOPLE_BY_USERID = "AsyncMarket_getPersonByUserId";    
    private EntityManager em;
    
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public Person getPerson(long personId) {
        return em.find(Person.class, personId);
    }

    public Person getPersonByUserId(String userId) {
        Query query = em.createNamedQuery(GET_PEOPLE_BY_USERID)
                        .setParameter("userId", userId);
        return (Person)query.getSingleResult();
    }

    public Person createPerson(Person person) {
        em.persist(person);
        return person;
    }

    public void removePerson(Person person) {
        em.remove(person);
    }

    @SuppressWarnings("unchecked")
    public List<Person> getPeople(int index, int count) {
        Query query = em.createNamedQuery(GET_ALL_PEOPLE)
                        .setFirstResult(index)
                        .setMaxResults(count);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Person> getPeople(
        String queryString, Map<String, Object> params, int index, int count) {
        Query query = em.createNamedQuery(GET_PEOPLE_BY_USERID)
                        .setFirstResult(index)
                        .setMaxResults(count);
        if (params != null && params.size() != 0) {
            for (String name: params.keySet()) {
                query.setParameter(name, params.get(name));
            }
        }
        return query.getResultList();
    }

}
