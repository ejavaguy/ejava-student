package ejava.examples.asyncmarket;

import java.util.List;

import ejava.examples.asyncmarket.bo.Person;
import ejava.examples.asyncmarket.ejb.InvalidRequestException;
import ejava.examples.asyncmarket.ejb.ResourceNotFoundException;

public interface UserMgmt {
    long createUser(String userId, String name) throws InvalidRequestException;
    Person getUser(long id) throws ResourceNotFoundException;
    Person getUserByUserId(String userId) throws ResourceNotFoundException;
    void removeUser(String userId) throws ResourceNotFoundException;
    List<Person> getUsers(int index, int count);
}
