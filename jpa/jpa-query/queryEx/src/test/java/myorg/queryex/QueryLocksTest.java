package myorg.queryex;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.Test;

public class QueryLocksTest extends QueryBase {
    private static final Logger log = LoggerFactory.getLogger(QueryLocksTest.class);
	public static enum Action { INSERT, UPDATE, FAIL };
	
	@Before
	public void setUpLocksTest() {
		em.getTransaction().commit();
		cleanup(em);
		populate(em);
	}

	/**
	 * This class is used to perform write actions to an object within the database
	 * within a Thread.
	 */
    private class Writer extends Thread {
		private String context;
    	private Actor actor;
    	private LockModeType lockMode;
		private EntityManager em_;
		private Action action;
		private int sleepTime=100;
		private String errorText;
    	public Writer(String context, Actor actor, LockModeType lockMode) {
    		this.context = context;
    		this.actor = actor;
    		this.lockMode = lockMode;
    		em_ = emf.createEntityManager();
			em_.getTransaction().begin();
			log.debug(context + " transaction started");
    	}
    	public boolean isDone() { return action != null && em_==null; }
    	public String getContext() { return context; }
    	public Action getAction() { return action; }
    	public String getErrorText() { return errorText; }
    	public void run() {
    		try {
            log.debug(context + " selecting with lockMode=" + lockMode);
            //h2 won't let us lock on a JOIN -- use subquery
            List<Actor> actors = em_.createQuery(
                    "select a from Actor a "
                    + "where a.person in ("
                    + "select p from Person p "
                    + "where p.firstName=:firstName and p.lastName=:lastName "
                    + "or p.firstName='" + context + "')", Actor.class)
                    .setLockMode(lockMode)
                    .setParameter("firstName", actor.getFirstName())
                    .setParameter("lastName", actor.getLastName())
                    .setMaxResults(1)
                    .getResultList();
            /*
            List<Actor> actorsx = em_.createQuery(
                            "select a from Actor a JOIN a.person as p " +
                            "where p.firstName=:firstName and p.lastName=:lastName " +
                            "or p.firstName='" + context + "'", Actor.class)
                            .setLockMode(lockMode)
                            .setParameter("firstName", actor.getFirstName())
                            .setParameter("lastName", actor.getLastName())
                            .setMaxResults(1)
                            .getResultList();*/
            try { 
                log.debug(context + " sleeping " + sleepTime + " msecs"); 
                Thread.sleep(sleepTime); 
            } catch (Exception ex){}
            if (actors.size()==0) {
                    log.debug(context + " creating entity");
                    if (!em_.contains(actor.getPerson())) {
                        em_.persist(actor.getPerson());
                    }
                    em_.persist(actor);
                    action=Action.INSERT;
            } else {
                    log.debug(context + " updating entity");
                    actors.get(0).setBirthDate(actor.getBirthDate());
                    action=Action.UPDATE;
            }
            em_.flush();
            log.debug(context + " committing transaction version=" + actor.getVersion());
                    em_.getTransaction().commit();
            log.debug(context + " committed transaction version=" + actor.getVersion());
    		} catch (PersistenceException ex) {
    			log.debug(context + " failed " + ex);
    			em_.getTransaction().rollback();
    			action = Action.FAIL; errorText = ex.toString();
    		} finally {
    			em_.close(); em_=null;
    		}
    	}
    }
    
    /**
     * This method is used to setup all locking tests based on a provided locking mode.
     * @param lockMode
     * @return
     */
    protected int testUpsert(LockModeType lockMode, int count) {
    	List<Writer> writers = new ArrayList<QueryLocksTest.Writer>();
    	//create writer instances within their own thread
    	for (int i=0; i<count; i++) {
    		Date birthDate = new GregorianCalendar(1969+i, Calendar.MAY, 25).getTime();
        	Actor actor = new Actor(new Person("test-actor" + i)
        		.setFirstName("Anne")
        		.setLastName("Heche")
        		.setBirthDate(birthDate));
    		writers.add(new Writer("writer" + i, actor, lockMode));
    	}
    	
    	//start each of the threads
    	List<Writer> working = new ArrayList<Writer>();
    	for (Writer writer : writers) {
    		working.add(writer); writer.start();
    	}

    	//run until all writers complete
    	while (!working.isEmpty()) {
    		try { Thread.sleep(100); } catch (Exception ex) {}
    		Iterator<Writer> itr = working.iterator();
    		while (itr.hasNext()) {
    			if (itr.next().isDone()) { itr.remove(); }
    		}
    	}
    	
    	//get the resultant entries in database
		List<Actor> actors = em.createQuery(
			"select a from Actor a JOIN FETCH a.person as p " +
			"where p.firstName=:firstName and p.lastName=:lastName", Actor.class)
			.setParameter("firstName", "Anne")
			.setParameter("lastName", "Heche")
			.getResultList();
		log.debug("actors=" + actors);
		for (Writer w : writers) {
			log.debug(String.format("%s => %s %s", w.getContext(), w.getAction(), w.getErrorText()==null?"":w.getErrorText()));
		}
		return actors.size();
    }
    
    @Test
    public void testSimple() {
    	log.info("*** testPersistentSimple ***");
        assertEquals("unexpected number of actors", 1, testUpsert(LockModeType.NONE, 1));
    }

    @Test
    public void testNONE() {
    	log.info("*** testNONE ***");
        int count=testUpsert(LockModeType.NONE, 5);
        for (int i=0; i<10 && count<=1; i++) {
            //can't always trigger race condition -- so retry
            cleanup(em);
            populate(em);
            count=testUpsert(LockModeType.NONE, 5);
        }
        assertTrue("unexpected number of actors", count > 1);
    }

    @Test
    public void testPessimisticWrite1() {
    	log.info("*** testPersistentWrite1 ***");
        assertEquals("unexpected number of actors", 1, testUpsert(LockModeType.PESSIMISTIC_WRITE, 1));
    }

    @Test @org.junit.Ignore //TODO: figure out why this does not work with server instance
    public void testPessimisticWrite() {
    	log.info("*** testPersistentWrite ***");
        assertEquals("unexpected number of actors", 1, testUpsert(LockModeType.PESSIMISTIC_WRITE, 5));
    }

    @Test @org.junit.Ignore //TODO: figure out why this does not work with server instance
    public void testPessimisticForceIncrement() {
    	log.info("*** testPersistentForceIncrement ***");
        assertEquals("unexpected number of actors", 1, testUpsert(LockModeType.PESSIMISTIC_FORCE_INCREMENT, 5));
    }
}
