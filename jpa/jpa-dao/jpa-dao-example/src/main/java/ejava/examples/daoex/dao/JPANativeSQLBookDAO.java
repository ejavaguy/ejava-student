package ejava.examples.daoex.dao;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import ejava.examples.daoex.bo.Book;

public class JPANativeSQLBookDAO implements BookDAO {
	private EntityManager em;
	
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	@Override
	public Book create(Book book) throws PersistenceException {
		em.createNativeQuery(
			"insert into JPADAO_BOOK (ID, DESCRIPTION, PAGES, TITLE) " +
			"values (null, ?1, ?2, ?3)")
				.setParameter(1, book.getDescription())
				.setParameter(2, book.getPages())
				.setParameter(3, book.getTitle())
				.executeUpdate();
		
		int idVal = ((Number)em.createNativeQuery("call identity()")
			.getSingleResult()).intValue();
		try {
	        Field id = Book.class.getDeclaredField("id");
	        id.setAccessible(true);
	        id.set(book, idVal);
		} catch (Exception ex) {
			throw new RuntimeException("Error setting id", ex);
		} 
		
		return book;
	}

	@Override
	public Book update(Book book) throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Book get(long id) throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(Book book) throws PersistenceException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Book> findAll(int start, int count) throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

}
