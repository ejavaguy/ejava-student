package ejava.projects.edmv.dao;

import java.util.List;

import ejava.projects.edmv.bo.DMVPerson;

/**
 * This interface provides core functionality needed by the DataGen to 
 * access people.
 *
 */
public interface DMVPersonDAO {
    DMVPerson getPerson(long id);
    List<DMVPerson> getPeople(int index, int count);
}
