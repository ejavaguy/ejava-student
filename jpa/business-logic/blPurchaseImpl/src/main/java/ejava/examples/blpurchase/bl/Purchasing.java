package ejava.examples.blpurchase.bl;

import ejava.examples.blpurchase.bo.Account;

/**
 * Purchasing handles payment of purchased products.
 */
public interface Purchasing {
	/**
	 * Creates an account for the user to use in purchasing products.
	 * @param email
	 * @param firstName
	 * @param lastName
	 */
	Account createAccount(String email, String firstName, String lastName);

	/**
	 * Completes the purchase of the items in the user's shopping cart,
	 * empties the cart, and returns the total cost paid.<p/>
	 * 
	 * Note that this capability is not yet fully defined.
	 * @param email
	 * @param password
	 */
	double checkout(String email, String password);
}
