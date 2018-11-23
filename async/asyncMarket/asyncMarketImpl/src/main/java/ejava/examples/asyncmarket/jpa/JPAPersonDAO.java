package ejava.examples.asyncmarket.jpa;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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
        List<Person> results = em.createNamedQuery(GET_PEOPLE_BY_USERID, Person.class)
                                .setParameter("userId", userId)
                                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public Person createPerson(Person person) {
        em.persist(person);
        return person;
    }

    public void removePerson(Person person) {
        em.remove(person);
    }

    public List<Person> getPeople(int index, int count) {
        return em.createNamedQuery(GET_ALL_PEOPLE, Person.class)
                    .setFirstResult(index)
                    .setMaxResults(count)
                    .getResultList();
    }

    public List<Person> getPeople(
        String queryString, Map<String, Object> params, int index, int count) {
        TypedQuery<Person> query = em.createNamedQuery(GET_PEOPLE_BY_USERID, Person.class)
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
