package ejava.projects.edmv.dao;

import java.util.List;

import ejava.projects.edmv.bo.Person;

/**
 * This interface provides a _sparse_ example of the methods offered by a 
 * DAO supplying O/R mapping to the DB.
 * 
 */
public interface PersonDAO {
	void createPerson(Person person) 
		throws DAOException;
	Person getPerson(long id) 
	    throws DAOException;
	List<Person> getPeople(int index, int count)
	    throws DAOException;
}
