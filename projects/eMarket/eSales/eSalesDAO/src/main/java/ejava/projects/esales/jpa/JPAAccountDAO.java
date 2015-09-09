package ejava.projects.esales.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import ejava.projects.esales.bo.Account;
import ejava.projects.esales.dao.AccountDAO;

/**
 * This class provides a sparse example of a JPA DAO for the class project.
 * It is put in place here to demonstrate some of the end-to-end use cases,
 * 
 * @author jcstaff
 *
 */
public class JPAAccountDAO implements AccountDAO {
	private EntityManager em;
	
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public void createAccount(Account account) {
        em.persist(account);
	}

	@SuppressWarnings("unchecked")
	public List<Account> getAccounts(int index, int count) {
	    return (List<Account>)em.createQuery("select a from Account a")
	                             .setFirstResult(index)
	                             .setMaxResults(count)
	                             .getResultList();
	}
}
