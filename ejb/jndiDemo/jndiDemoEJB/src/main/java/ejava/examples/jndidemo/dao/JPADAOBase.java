package ejava.examples.jndidemo.dao;

import javax.persistence.EntityManager;

/**
 * This class represents a simple base type for a JPA DAO.
 * @param <T>
 */
public class JPADAOBase<T> {
	protected EntityManager em;

	public void create(T task) {
		em.persist(task);
	}
	public T update(T task) {
		return em.merge(task);
	}
	public void delete(T task) {
		em.remove(task);
	}
}