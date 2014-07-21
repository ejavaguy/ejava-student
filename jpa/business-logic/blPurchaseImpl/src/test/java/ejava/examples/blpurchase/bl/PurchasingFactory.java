package ejava.examples.blpurchase.bl;

import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import ejava.examples.blpurchase.blimpl.CatalogImpl;
import ejava.examples.blpurchase.blimpl.PurchasingImpl;
import ejava.examples.blpurchase.bo.Account;
import ejava.examples.blpurchase.bo.Product;

/**
 * This class provides a factory for commonly used objects in the application.
 */
public class PurchasingFactory {

	private static EntityManagerFactory emf;
	private EntityManager em;
	
	public void init() {
		if (em == null) {
			if (emf==null) {
				emf = Persistence.createEntityManagerFactory("purchasing");
			}
			em = emf.createEntityManager();
		}
	}
	
	public void close() {
		if (em.getTransaction().isActive()) {
			if (em.getTransaction().getRollbackOnly()) {
				em.getTransaction().rollback();
			}
			else {
				em.getTransaction().commit();
			}
		}
		em.close();
		em=null;
	}
	
	public EntityManager getEntityManager() {
		return em;
	}
	
	public Catalog getCatalog() {
		Catalog catalog = new CatalogImpl();
		((CatalogImpl)catalog).setEntityManager(getEntityManager());
		return catalog;
	}
	
	public Purchasing getPurchasing() {
		Purchasing purchasing = new PurchasingImpl();
		((PurchasingImpl)purchasing).setEntityManager(getEntityManager());
		return purchasing;
	}
	
	public void createProducts() {
		Random random = new Random();
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		for (int i=0; i<100; i++) {
			Product product = new Product("A" + i, random.nextInt(1000), random.nextInt(100));
			em.persist(product);
		}
		em.getTransaction().commit();
	}
	
	public Account createAccount() {
		String email="steveb@ravens.com";
		em.getTransaction().begin();
		Account account = getPurchasing().createAccount(
				email, "steve", "bisciotti");
		em.getTransaction().commit();

			//got a null back -- account must exist -- get it
		if (account == null) {
			account = em.createNamedQuery(Account.FIND_BY_EMAIL, Account.class)
						.setParameter("email", email)
						.getSingleResult();
		}
		return account;
	}
}
