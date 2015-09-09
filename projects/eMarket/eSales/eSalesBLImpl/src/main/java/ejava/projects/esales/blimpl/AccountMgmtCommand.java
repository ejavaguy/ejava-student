package ejava.projects.esales.blimpl;

import java.io.PrintStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.projects.esales.bl.AccountMgmt;
import ejava.projects.esales.bo.Account;
import ejava.projects.esales.jpa.JPAAccountDAO;

public class AccountMgmtCommand extends ESalesIngestCommand {
	@SuppressWarnings("unused")
	private static final Log log = 
		LogFactory.getLog(AccountMgmtCommand.class);
	private static String command = System.getProperty("command");
	private static String indexStr = System.getProperty("index","0");
	private static String countStr = System.getProperty("count","1");
	private static PrintStream out = System.out;
	
	private static EntityManagerFactory getEMF() {
		return Persistence.createEntityManagerFactory("eSalesBO");
	}
	
	private static void invoke(AccountMgmt accountMgmt) throws Exception {
		if (command == null) {
			throw new Exception("command not supplied");
		}
		else if ("listAccounts".equals(command)) {
			listAccounts(accountMgmt);
		}
		else {
			throw new Exception("unknown command:" + command);
		}
	}
	
	private static void listAccounts(AccountMgmt accountMgmt) 
	    throws Exception {
		
		int index = Integer.parseInt(indexStr);
		int count = Integer.parseInt(countStr);
		
		for (Account account : accountMgmt.getAccounts(index, count)) {
	        out.println("account:" + account);	    	
		}
	}
	
	public static void main(String args[]) {		
		try {
			EntityManagerFactory emf = null;
			EntityManager em = null;
			try {
				emf = getEMF();
				em = emf.createEntityManager();
				JPAAccountDAO accountDAO = new JPAAccountDAO();
				accountDAO.setEntityManager(em);
				
			    AccountMgmtImpl accountMgmt = new AccountMgmtImpl();
			    accountMgmt.setAccountDAO(accountDAO);
			    em.getTransaction().begin();
			    
			    invoke(accountMgmt);
			}
			finally {
				if (em != null) {
					EntityTransaction tx = em.getTransaction();
					if (tx.getRollbackOnly()) { tx.rollback(); }
					else                      { tx.commit(); }
 				}
				if (emf != null) { emf.close(); }				
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}
}
