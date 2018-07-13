package ejava.examples.blpurchase.blimpl;

import java.util.Random;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.blpurchase.bl.Purchasing;
import ejava.examples.blpurchase.bo.Account;
import ejava.examples.blpurchase.bo.Cart;
import ejava.examples.blpurchase.bo.Product;

/**
 * This class implements basic logic used to implement the purchasing
 * aspects of the application.
 */
public class PurchasingImpl implements Purchasing {
	private static final Logger log = LoggerFactory.getLogger(PurchasingImpl.class);
	private EntityManager em;
	private Random random=new Random();
	
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public Account createAccount(String email, String firstName, String lastName) {
		int count=em.createNamedQuery(Account.FIND_BY_EMAIL)
				.setParameter("email", email)
				.getResultList().size();
		if (count==0) {
			Account account = new Account(email, firstName, lastName);
			account.setPassword(generatePassword());
			em.persist(account);
			log.debug("created account:" + account);
			return account;
		}

		return null; //don't return legacy accounts -- they have the password
	}

	@Override
	public double checkout(String email, String password) {
		Cart cart = em.find(Cart.class, email);
		if (cart == null) {
			log.warn("cart not found");
			return 0;
		}
		
		Account account = cart.getAccount();
		if (!account.getPassword().equals(password)) {
			log.warn("wrong password");
			return 0;
		}
		
		double total = 0;
		for (Product product : cart.getProducts()) {
			total += product.getPrice();
		}
		log.debug(String.format("checked out %d products for %s",cart.getProducts().size(), email));
		cart.getProducts().clear();
		
		return total;
	}

	private String generatePassword() {
		byte[] password = new byte[8];
		for (int i=0; i<password.length; i++) {
			password[i] = (byte)('a' + random.nextInt(25));
		}
		return new String(password);
	}
}
