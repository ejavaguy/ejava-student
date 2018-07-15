package ejava.projects.eleague.blimpl;

import java.io.PrintStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.projects.eleague.bl.ClubMgmt;
import ejava.projects.eleague.bo.Venue;
import ejava.projects.eleague.jpa.JPAClubDAO;

public class ClubMgmtCommand extends ELeagueIngestCommand {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(ClubMgmtCommand.class);
	private static String command = System.getProperty("command");
	private static String indexStr = System.getProperty("index","0");
	private static String countStr = System.getProperty("count","1");
	private static PrintStream out = System.out;
	
	private static EntityManagerFactory getEMF() {
		return Persistence.createEntityManagerFactory("eLeagueBO-test");
	}
	
	private static void invoke(ClubMgmt clubMgmt) throws Exception {
		if (command == null) {
			throw new Exception("command not supplied");
		}
		else if ("listVenues".equals(command)) {
			listVenues(clubMgmt);
		}
		else {
			throw new Exception("unknown command:" + command);
		}
	}
	
	private static void listVenues(ClubMgmt clubMgmt) 
	    throws Exception {
		
		int index = Integer.parseInt(indexStr);
		int count = Integer.parseInt(countStr);
		
		for (Venue venue : clubMgmt.getVenues(index, count)) {
	        out.println("venue:" + venue);	    	
		}
	}
	
	public static void main(String args[]) {		
		try {
			EntityManagerFactory emf = null;
			EntityManager em = null;
			try {
				emf = getEMF();
				em = emf.createEntityManager();
				JPAClubDAO clubDAO = new JPAClubDAO();
				clubDAO.setEntityManager(em);
				
			    ClubMgmtImpl clubMgmt = new ClubMgmtImpl();
			    clubMgmt.setClubDAO(clubDAO);
			    em.getTransaction().begin();
			    
			    invoke(clubMgmt);
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
