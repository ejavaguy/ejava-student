package ejava.examples.daoex.dao;

import java.util.List;

import javax.persistence.EntityManager;

import ejava.examples.daoex.bo.Book;

/**
 * This class provides a simple DAO implementation based on JPA
 */
public class JPABookDAOImpl implements BookDAO {
	private EntityManager em;
	
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public Book create(Book book) {
		em.persist(book);
		return book;
	}

	@Override
	public Book update(Book book) {
		return em.merge(book);
	}

	@Override
	public Book get(long id) {
		return em.find(Book.class, id);
	}

	@Override
	public void remove(Book book) {
		em.remove(book);
	}

	@Override
	public List<Book> findAll(int offset, int limit) {
		return em.createQuery("select b from Book b", Book.class)
				.setFirstResult(offset)
				.setMaxResults(limit)
				.getResultList();
	}

}
