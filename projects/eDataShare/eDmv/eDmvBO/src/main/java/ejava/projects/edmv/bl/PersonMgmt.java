package ejava.projects.edmv.bl;

import java.util.List;

import ejava.projects.edmv.bo.Person;

/** 
 * This interface provides a _sparse_ example for business logic related
 * to managing people within the DMV.
 * 
 * @author jcstaff
 *
 */
public interface PersonMgmt {
    List<Person> getPeople(int index, int count) throws EDmvException;
}
