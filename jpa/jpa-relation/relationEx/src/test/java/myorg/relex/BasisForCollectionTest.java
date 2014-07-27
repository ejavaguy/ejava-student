package myorg.relex;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import javax.persistence.*;

import myorg.relex.collection.Fleet;
import myorg.relex.collection.Ship;
import myorg.relex.collection.ShipByBusinessId;
import myorg.relex.collection.ShipByDefault;
import myorg.relex.collection.ShipByPK;
import myorg.relex.collection.ShipBySwitch;

import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;

import org.junit.*;

@Ignore
public class BasisForCollectionTest extends JPATestBase {
    private static Log log = LogFactory.getLog(BasisForCollectionTest.class);

    private static int dbid=0;
    private static void resetId() { dbid=0; }
    private static int getId() { return ++dbid; }
    
    private abstract class IdentityTest<T extends Ship> {
    	protected abstract T makeMember(String name, Date created);
    	public <C extends Collection<T>> C addItems(C collection, int expectedCount) {
    		
    		log.debug("adding member1 to " + collection.getClass());
    		T member1 = makeMember("one", new Date());
			collection.add(member1);
    		log.debug("adding member2 to " + collection.getClass());
			T member2 = makeMember("two", new Date());
			collection.add(member2);
    		log.debug("adding member3 to " + collection.getClass());
			T member3 = makeMember("three", new Date());
			collection.add(member3);
			log.debug(collection.getClass() + " has" + collection.size() + " members");
			assertEquals("unexpected count for " + collection.getClass(), expectedCount, collection.size());

    		log.debug("checking for member1");
    		assertTrue("member 1 not found", collection.contains(member1));
    		log.debug("checking for member2");
    		assertTrue("member 2 not found", collection.contains(member2));
    		log.debug("checking for member3");
    		assertTrue("member 3 not found", collection.contains(member3));
    		
    		return collection;
    	}
    }

    /**
     * This test will demonstrate capabilities and issues associated with an entity
     * using the default java.lang.Object hashCode/equals implementation.
     */
    @Test
    public void testIdentityByDefault() {
        log.info("*** testIdentifyByDefailt ***");
        
        IdentityTest<ShipByDefault> test = new IdentityTest<ShipByDefault>() {
        	protected ShipByDefault makeMember(String name, Date created) { 
        		return (ShipByDefault)new ShipByDefault().setId(getId()).setName(name).setCreated(created); }
        };

        List<? extends Ship> ships1 = test.addItems(new ArrayList<ShipByDefault>(), 3);
        Set<? extends Ship> ships2 = test.addItems(new HashSet<ShipByDefault>(), 3);

       	Ship ship = ships1.get(0);
        log.debug("verify member of first collection not member of second for " + ship.getClass());
        assertFalse("ship in two collections", ships2.contains(ship));
    }
    
    @Test
    public void testIdentifyByPK() {
        log.info("*** testIdentifyByPK ***");

        IdentityTest<ShipByPK> test = new IdentityTest<ShipByPK>() {
        	protected ShipByPK makeMember(String name, Date created) { 
        		return (ShipByPK)new ShipByPK().setId(getId()).setName(name).setCreated(created); }
        };

        resetId();
        List<? extends Ship> ships1 = test.addItems(new ArrayList<ShipByPK>(), 3);
        resetId();
        Set<? extends Ship> ships2 = test.addItems(new HashSet<ShipByPK>(), 3);
        resetId();

       	Ship ship = ships1.get(0);
       	ship.peekHashCode();
       	for (Ship s : ships2) {
           	s.peekHashCode();
       	}
        log.debug("verify member of first collection member of second for " + ship.getClass());
        assertTrue("ship not in two collections", ships2.contains(ship));
    }
    
    @Test
    public void testIdentifyByBusinessId() {
        log.info("*** testIdentifyByBusinessId ***");

        IdentityTest<ShipByBusinessId> test = new IdentityTest<ShipByBusinessId>() {
        	protected ShipByBusinessId makeMember(String name, Date created) { 
        		return (ShipByBusinessId)new ShipByBusinessId().setId(getId()).setName(name).setCreated(created); }
        };

        resetId();
        List<? extends Ship> ships1 = test.addItems(new ArrayList<ShipByBusinessId>(), 3);
        resetId();
        Set<? extends Ship> ships2 = test.addItems(new HashSet<ShipByBusinessId>(), 3);

       	Ship ship = ships1.get(0);
       	ship.peekHashCode();
       	for (Ship s : ships2) {
           	s.peekHashCode();
       	}
        log.debug("verify member of first collection member of second for " + ship.getClass());
        assertTrue("ship not in two collections", ships2.contains(ship));
    }
    
    
    @Test
    public void testPersistByDefault() throws Exception {
    	log.info("*** testPersistentByDefault ***");
    	
    	//persist entity in first persistence context
    	Ship ship = new ShipByDefault().setName("one").setCreated(new Date());
    	log.debug("persisting " + ship + " to em1");
    	em.persist(ship);
    	em.getTransaction().commit();
    	
    	//create a second persistence context and find entity
    	EntityManager em2 = createEm();
    	log.debug("getting from em2 " + ship);
    	Ship ship2 = em2.find(ShipByDefault.class, ship.getId());
    	
    	//check to the result of hashCode and equals
    	log.debug("checking hashCode values");
    	assertFalse("unexpected hashCode match", ship.peekHashCode() == ship2.peekHashCode());
    	log.debug("checking equals");
    	assertFalse("unexpected equals match", ship.equals(ship2));
    	
    	//determine if second persistence context thinks it has entity
    	assertFalse("em2 contains entity from em1", em2.contains(ship));
    }
   
    @Test
    public void testPersistByPK() throws Exception {
    	log.info("*** testPersistentByPK ***");
   
    	//persist entity in first persistence context
    	Ship ship = new ShipByPK().setName("one").setCreated(new Date());
    	log.debug("persisting " + ship + " to em1");
    	em.persist(ship);
    	em.getTransaction().commit();
    	
    	//create a second persistence context and find entity
    	EntityManager em2 = createEm();
    	log.debug("getting from em2 " + ship);
    	Ship ship2 = em2.find(ShipByPK.class, ship.getId());
    	
    	//check to the result of hashCode and equals
    	log.debug("checking hashCode values");
    	assertTrue("unexpected hashCode mismatch", ship.peekHashCode() == ship2.peekHashCode());
    	log.debug("checking equals");
    	assertTrue("unexpected equals mismatch", ship.equals(ship2));
    	
    	//determine if second persistence context thinks it has entity
    	assertFalse("em2 contains entity from em1", em2.contains(ship));
    }
   
    @Test
    public void testPersistByBusinessId() throws Exception {
    	log.info("*** testPersistentByBusinessId ***");
    	
    	//persist entity in first persistence context
    	Ship ship = new ShipByBusinessId().setName("one").setCreated(new Date());
    	log.debug("persisting " + ship + " to em1");
    	em.persist(ship);
    	em.getTransaction().commit();
    	
    	//create a second persistence context and find entity
    	EntityManager em2 = createEm();
    	log.debug("getting from em2 " + ship);
    	Ship ship2 = em2.find(ShipByBusinessId.class, ship.getId());
    	
    	//check to the result of hashCode and equals
    	log.debug("checking hashCode values");
    	assertTrue("unexpected hashCode mismatch", ship.peekHashCode() == ship2.peekHashCode());
    	log.debug("checking equals");
    	assertTrue("unexpected equals mismatch", ship.equals(ship2));
    	
    	//determine if second persistence context thinks it has entity
    	assertFalse("em2 contains entity from em1", em2.contains(ship));
    }

    @Test
    public void testRelationDefault() {
    	log.info("*** testRelationDefault ***");
    	
    	int count=3;
    	Fleet fleet = new Fleet();
    	fleet.setName("sixth");
    	
    	//add entities
    	List<Ship> ships = new ArrayList<Ship>();
    	List<Integer> originalHashCode = new ArrayList<Integer>();
    	for (int i=1; i<=count; i++) {
    		Ship ship = new ShipByDefault().setName(""+i).setCreated(new Date());
    		ships.add(ship);
    		originalHashCode.add(ship.peekHashCode());
    		boolean added=false;
    		assertTrue(added=fleet.getShipsListByDefault().add((ShipByDefault)ship));
    		log.debug("entity " + ship + " added to list=" + added);
    		assertTrue(added=fleet.getShipsSetByDefault().add((ShipByDefault)ship));
    		log.debug("entity " + ship + " added to set=" + added);
    	}
    	assertEquals("unexpected list size", count, fleet.getShipsListByDefault().size());
		//since the entity is identified by instance the instances 
		//each represent an unique entry in a set
    	assertEquals("unexpected set size", count, fleet.getShipsSetByDefault().size());
    	
    	log.debug("persist the entities");
    	em.persist(fleet);     	
    	em.flush(); 
    	log.debug("check if parent collections still have original child objects"); 
    	for (int i=0; i<count; i++) {
    		Ship ship = ships.get(i);
    		boolean contains=false;
    		//basis for hashCode did not change
    		log.debug("hashCode old=" + originalHashCode.get(i) + ", new=" + ship.peekHashCode());
    		assertTrue(ship.peekHashCode() == originalHashCode.get(i).intValue());

    		//since the entity is identified by instance the instances 
    		//keep their identity during the persist to the database
    		assertTrue(contains=fleet.getShipsListByDefault().contains(ship));
    		log.debug("list contains " + ship + "=" + contains);
    		assertTrue(contains=fleet.getShipsSetByDefault().contains(ship));
    		log.debug("set contains " + ship + "=" + contains);
    		assertTrue(contains=em.contains(ship));
    		log.debug("em contains " + ship + "=" + contains);
    	}
    	
    	log.debug("verify all instances pulled back correctly");
    	em.clear();
    	Fleet fleet2 = em.find(Fleet.class, fleet.getId());
    	assertEquals("unexpected list size", fleet.getShipsListByDefault().size(), 
    			fleet2.getShipsListByDefault().size());
    	assertEquals("unexpected unmanaged set size", fleet.getShipsSetByDefault().size(), 
    			fleet2.getShipsSetByDefault().size());

    	log.debug("check if second parent has original child objects"); 
    	for (int i=0; i<count; i++) {
    		Ship ship = ships.get(i);
    		boolean contains=false;
    		assertTrue(contains=fleet.getShipsListByDefault().contains(ship));
    		log.debug("original list contains " + ship + "=" + contains);
    		//basis of hashCode has changed
    		Ship ship2 = em.find(ShipByDefault.class, ship.getId());
    		log.debug("hashCode old=" + originalHashCode.get(i) + ", new=" + ship2.peekHashCode());
    		assertFalse(originalHashCode.get(i) == ship2.peekHashCode());
    		
    		//since the entity is identified by instance the new instances 
    		//created during the find are not a match 
    		assertFalse(contains=fleet2.getShipsListByDefault().contains(ship));
    		log.debug("list contains " + ship + "=" + contains);
    		assertFalse(contains=fleet2.getShipsSetByDefault().contains(ship));
    		log.debug("set contains " + ship + "=" + contains);
    		assertFalse(contains=em.contains(ship));
    		log.debug("em contains " + ship + "=" + contains);
    	}
    }
    

    @Test
    public void testRelationPK() {
    	log.info("*** testRelationPK ***");
    	
    	int count=3;
    	Fleet fleet = new Fleet();
    	fleet.setName("sixth");

    	//add entities
    	List<Ship> ships = new ArrayList<Ship>();
    	List<Integer> originalHashCode = new ArrayList<Integer>();
    	for (int i=1; i<=count; i++) {
    		Ship ship = new ShipByPK().setName(""+i).setCreated(new Date());
    		ships.add(ship);
    		originalHashCode.add(ship.peekHashCode());
    		boolean added=false;
    		assertTrue(added=fleet.getShipsListByPK().add((ShipByPK)ship));
    		log.debug("entity " + ship + " added to list=" + added);
    		//since the entity is identified by an unassigned PK 
    		//the subsequent entities overlap with the first entity and are discarded
    		added=fleet.getShipsSetByPK().add((ShipByPK)ship);
    		log.debug("entity " + ship + " added to set=" + added);
    	}
    	assertEquals("unexpected list size", count, fleet.getShipsListByPK().size());
		//since the entity is identified by an unassigned PK 
		//the subsequent entities overlap with the first entity and are discarded
    	assertEquals("unexpected set size", 1, fleet.getShipsSetByPK().size());
    	
    	log.debug("persist the entities");
    	em.persist(fleet);     	
    	em.flush(); 
    	log.debug("check if parent collections still have original child objects");
    	for (int i=0; i<count; i++) {
    		Ship ship = ships.get(i);
    		//basis of hashCode has changed
    		log.debug("hashCode old=" + originalHashCode.get(i) + ", new=" + ship.peekHashCode());
    		assertFalse(ship.peekHashCode() == originalHashCode.get(i));
    		boolean contains=false;
    		//since the entity is identified by a PK value that is shared across multiple instances
    		//representing the same row -- we will get matches between previous instances referencing
    		//the row and some collections in what we have pulled back.
    		assertTrue(contains=fleet.getShipsListByPK().contains(ship));
    		log.debug("list contains " + ship + "=" + contains);
    		if (i<=0) { //sets only have one entry above because of common hashCode during add() 
    			//regular sets don't seem to want to match our value
	    		assertFalse(contains=fleet.getShipsSetByPK().contains(ship));
	    		log.debug("set contains " + ship + "=" + contains);
    		}
    		assertTrue(contains=em.contains(ship));
    		log.debug("em contains " + ship + "=" + contains);
    	}
    	
    	log.debug("verify all instances pulled back correctly");
    	em.clear();
    	Fleet fleet2 = em.find(Fleet.class, fleet.getId());
    	assertEquals("unexpected list size", fleet.getShipsListByPK().size(), 
    			fleet2.getShipsListByPK().size());
    	assertEquals("unexpected unmanaged set size", fleet.getShipsSetByPK().size(), 
    			fleet2.getShipsSetByPK().size());

    	log.debug("check if second parent has original child objects"); 
    	for (int i=0; i<count; i++) {
    		Ship ship = fleet.getShipsListByPK().get(i);
    		//basis of hashCode has changed since the original object created
    		//but has not changed since the persist()
    		Ship ship2 = em.find(ShipByPK.class, ship.getId());
    		log.debug("hashCode old=" + originalHashCode.get(i) + ", new=" + ship2.peekHashCode());
    		assertFalse(originalHashCode.get(i) == ship2.peekHashCode());

    		boolean contains=false;
    		assertTrue(contains=fleet.getShipsListByPK().contains(ship));
    		log.debug("original list contains " + ship + "=" + contains);
    		assertTrue(contains=fleet2.getShipsListByPK().contains(ship));
    		log.debug("list contains " + ship + "=" + contains);
    		if (i<=0) { //sets only have one element
	    		assertTrue(contains=fleet2.getShipsSetByPK().contains(ship));
	    		log.debug("set contains " + ship + "=" + contains);
    		}
    		assertFalse(contains=em.contains(ship));
    		log.debug("em contains " + ship + "=" + contains);
    	}
    }

    @Test
    public void testRelationSwitch() {
    	log.info("*** testRelationSwitch ***");
    	
    	int count=3;
    	Fleet fleet = new Fleet();
    	fleet.setName("sixth");
    	
    	//add entities
    	List<Ship> ships = new ArrayList<Ship>();
    	List<Integer> originalHashCode = new ArrayList<Integer>();
    	for (int i=1; i<=count; i++) {
    		Ship ship = new ShipBySwitch().setName(""+i).setCreated(new Date());
    		ships.add(ship);
    		originalHashCode.add(ship.peekHashCode());
    		boolean added=false;
    		assertTrue(added=fleet.getShipsListBySwitch().add((ShipBySwitch)ship));
    		log.debug("entity " + ship + " added to list=" + added);
    		//since entities are starting out instance-based -- all elements are accepted by sets
    		assertTrue(added=fleet.getShipsSetBySwitch().add((ShipBySwitch)ship));
    		log.debug("entity " + ship + " added to set=" + added);
    	}
    	assertEquals("unexpected list size", count, fleet.getShipsListBySwitch().size());
    	assertEquals("unexpected set size", count, fleet.getShipsSetBySwitch().size());
    	
    	log.debug("persist the entities");
    	em.persist(fleet);     	
    	em.flush(); 
    	log.debug("check if parent collections still have original child objects"); 
    	for (int i=0; i<count; i++) {
    		Ship ship = ships.get(i);
    		//basis of hashCode has changed
    		log.debug("hashCode old=" + originalHashCode.get(i) + ", new=" + ship.peekHashCode());
    		assertFalse(ship.peekHashCode() == originalHashCode.get(i).intValue());
    		boolean contains=false;
    		assertTrue(contains=fleet.getShipsListBySwitch().contains(ship));
    		log.debug("list contains " + ship + "=" + contains);
    		//new hashCode/equals results in mis-match of values even with same PK
    		assertFalse(contains=fleet.getShipsSetBySwitch().contains(ship));
    		log.debug("set contains " + ship + "=" + contains);
    		//entity manager answers contains=true for instances it manages
    		assertTrue(contains=em.contains(ship));
    		log.debug("em contains " + ship + "=" + contains);
    	}
    	
    	log.debug("verify all instances pulled back correctly");
    	em.clear();
    	Fleet fleet2 = em.find(Fleet.class, fleet.getId());
    	assertEquals("unexpected list size", fleet.getShipsListBySwitch().size(), 
    			fleet2.getShipsListBySwitch().size());
    	assertEquals("unexpected unmanaged set size", fleet.getShipsSetBySwitch().size(), 
    			fleet2.getShipsSetBySwitch().size());

    	log.debug("check if second parent has original child objects"); 
    	for (int i=0; i<count; i++) {
    		Ship ship = fleet.getShipsListBySwitch().get(i);
    		//basis of hashCode has changed
    		Ship ship2 = em.find(ShipByPK.class, ship.getId());
    		log.debug("hashCode old=" + originalHashCode.get(i) + ", new=" + ship2.peekHashCode());
    		assertFalse(originalHashCode.get(i) == ship2.peekHashCode());
    		boolean contains=false;
    		//since the entities are now being identified by PK value -- the previous
    		//values will match the entities in the retrieved collections
    		assertTrue(contains=fleet.getShipsListBySwitch().contains(ship));
    		log.debug("original list contains " + ship + "=" + contains);
    		assertTrue(contains=fleet2.getShipsListBySwitch().contains(ship));
    		log.debug("list contains " + ship + "=" + contains);
    		assertTrue(contains=fleet2.getShipsSetBySwitch().contains(ship));
    		log.debug("set contains " + ship + "=" + contains);
    		//entity manager only answers contains=true for instances it manages
    		assertFalse(contains=em.contains(ship));
    		log.debug("em contains " + ship + "=" + contains);
    	}
    }
    
    @Test
    public void testRelationBusinessId() {
    	log.info("*** testRelationBusinessId ***");
    	
    	int count=3;
    	Fleet fleet = new Fleet();
    	fleet.setName("sixth");
    	
    	//add entities
    	List<Ship> ships = new ArrayList<Ship>();
    	List<Integer> originalHashCode = new ArrayList<Integer>();
    	for (int i=1; i<=count; i++) {
    		Ship ship = new ShipByBusinessId().setName(""+i).setCreated(new Date());
    		ships.add(ship);
    		originalHashCode.add(ship.peekHashCode());
    		boolean added=false;
    		assertTrue(added=fleet.getShipsListByBusinessId().add((ShipByBusinessId)ship));
    		log.debug("entity " + ship + " added to list=" + added);
    		added=fleet.getShipsSetByBusinessId().add((ShipByBusinessId)ship);
    		log.debug("entity " + ship + " added to set=" + added);
    	}
    	assertEquals("unexpected list size", count, fleet.getShipsListByBusinessId().size());
    	assertEquals("unexpected set size", count, fleet.getShipsSetByBusinessId().size());
    	
    	log.debug("persist the entities");
    	em.persist(fleet);     	
    	em.flush(); 
    	log.debug("check if parent collections still have original child objects"); 
    	for (int i=0; i<count; i++) {
    		Ship ship = ships.get(i);
    		//basis of hashCode has not changed
    		log.debug("hashCode old=" + originalHashCode.get(i) + ", new=" + ship.peekHashCode());
    		assertTrue(ship.peekHashCode() == originalHashCode.get(i).intValue());
    		boolean contains=false;
    		assertTrue(contains=fleet.getShipsListByBusinessId().contains(ship));
    		log.debug("list contains " + ship + "=" + contains);
    		assertTrue(contains=fleet.getShipsSetByBusinessId().contains(ship));
    		log.debug("set contains " + ship + "=" + contains);
    		assertTrue(contains=em.contains(ship));
    		log.debug("em contains " + ship + "=" + contains);
    	}
    	
    	log.debug("verify all instances pulled back correctly");
    	em.clear();
    	Fleet fleet2 = em.find(Fleet.class, fleet.getId());
    	assertEquals("unexpected list size", fleet.getShipsListByBusinessId().size(), 
    			fleet2.getShipsListByBusinessId().size());
    	assertEquals("unexpected unmanaged set size", fleet.getShipsSetByBusinessId().size(), 
    			fleet2.getShipsSetByBusinessId().size());

    	log.debug("check if second parent has original child objects"); 
    	for (int i=0; i<count; i++) {
    		Ship ship = fleet.getShipsListByBusinessId().get(i);
    		//basis of hashCode has not changed
    		Ship ship2 = em.find(ShipByBusinessId.class, ship.getId());
    		log.debug("hashCode old=" + originalHashCode.get(i) + ", new=" + ship2.peekHashCode());
    		assertTrue(originalHashCode.get(i) == ship2.peekHashCode());
    		boolean contains=false;
    		assertTrue(contains=fleet.getShipsListByBusinessId().contains(ship));
    		log.debug("original list contains " + ship + "=" + contains);
    		assertTrue(contains=fleet2.getShipsListByBusinessId().contains(ship));
    		log.debug("list contains " + ship + "=" + contains);
    		assertTrue(contains=fleet2.getShipsSetByBusinessId().contains(ship));
    		log.debug("set contains " + ship + "=" + contains);
    		assertFalse(contains=em.contains(ship));
    		log.debug("em contains " + ship + "=" + contains);
    	}
    }
}
