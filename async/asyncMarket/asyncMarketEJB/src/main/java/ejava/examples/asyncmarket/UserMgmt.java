package ejava.examples.asyncmarket;

import java.util.List;

import ejava.examples.asyncmarket.bo.Person;

public interface UserMgmt {
    long createUser(String userId, String name) throws MarketException;
    Person getUser(long id) throws MarketException;
    Person getUserByUserId(String userId) throws MarketException;
    void removeUser(String userId) throws MarketException;
    List<Person> getUsers(int index, int count) throws MarketException;
}
