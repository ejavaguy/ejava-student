package ejava.examples.asyncmarket.dao;

import java.util.List;
import java.util.Map;

import ejava.examples.asyncmarket.bo.Person;

public interface PersonDAO {
    Person getPerson(long personId);
    Person getPersonByUserId(String userId);
    Person createPerson(Person person);
    void removePerson(Person person);
    List<Person> getPeople(int index, int count);
    List<Person> getPeople(
        String queryString, Map<String, Object> params, int index, int count);
}
