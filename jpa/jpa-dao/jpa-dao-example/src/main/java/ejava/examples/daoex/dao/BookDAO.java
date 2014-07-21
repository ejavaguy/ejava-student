package ejava.examples.daoex.dao;

import java.util.List;
import javax.persistence.PersistenceException;
import ejava.examples.daoex.bo.Book;

/**
 * This interface provides an example DAO interface for a Book. Note that
 * the technology associate with the DAO should not be exposed in this 
 * interface. 
 */
public interface BookDAO {
    /**
     * Add the book to the database.
     */
    Book create(Book book) throws PersistenceException;

    /**
     * Updates the book in the database with the values in this object.
     */
    Book update(Book book) throws PersistenceException;

    /**
     * Gets a book from the database by its ID.
     */
    Book get(long id) throws PersistenceException;

    /**
     * Removes a book from the database.
     */
    void remove(Book book) throws PersistenceException;
    
    /**
     * Returns a collection of books, starting at the index provided and
     * limiting the collection to the count value.
     */
    List<Book> findAll(int start, int count) throws PersistenceException;
}