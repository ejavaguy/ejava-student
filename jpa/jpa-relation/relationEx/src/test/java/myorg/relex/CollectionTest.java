package myorg.relex;


import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import myorg.relex.collection.Lineup;
import myorg.relex.collection.Path;
import myorg.relex.collection.Position;
import myorg.relex.collection.Segment;
import myorg.relex.collection.Ship;
import myorg.relex.collection.ShipByBusinessId;
import myorg.relex.collection.ShipByDefault;
import myorg.relex.collection.ShipByPK;
import myorg.relex.collection.ShipBySwitch;

import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;

import org.junit.*;


public class CollectionTest extends JPATestBase {
    private static Log log = LogFactory.getLog(CollectionTest.class);

    /**
     * This test will demonstrate how instances that use the default java.lang.Object
     * hashCode/equals will only be equal is when they are the same instance.
     */
    @Test
    public void testByDefault() {
    	log.info("*** testByDefault ***");
    	
    	Ship ship1 = new ShipByDefault();
    	Ship ship2 = new ShipByDefault();
    	assertFalse("unexpected hashCode", ship1.hashCode() == ship2.hashCode());
    	assertFalse("unexpected equality", ship1.equals(ship2));
    	
    	log.debug("persisting entity");
    	em.persist(ship1);
    	em.flush();
    	Ship ship3 = em.find(ShipByDefault.class, ship1.getId());
    	assertTrue("unexpected hashCode", ship1.hashCode() == ship3.hashCode());
    	assertTrue("unexpected inequality", ship1.equals(ship3));

    	log.debug("getting new instance of entity");
    	em.clear();
    	Ship ship4 = em.find(ShipByDefault.class, ship1.getId());
    	assertFalse("unexpected hashCode", ship1.hashCode() == ship4.hashCode());
    	assertFalse("unexpected equality", ship1.equals(ship4));
    }
    
    /**
     * This test will demonstrate how instances that use a databaseId assigned during 
     * persist can help determine if two separate instances are logically equal but 
     * will not provide that capability prior to being persisted.
     */
    @Test
    public void testByPK() {
    	log.info("*** testByPK ***");
    	
    	Ship ship1 = new ShipByPK();
    	Ship ship2 = new ShipByPK();
    	assertTrue("unexpected hashCode", ship1.hashCode() == ship2.hashCode());
    	assertTrue("unexpected equality", ship1.equals(ship2));

    	log.debug("persisting entity");
    	em.persist(ship1);
    	em.flush();
    	em.clear();
    	log.debug("getting new instance of entity");
    	Ship ship4 = em.find(ShipByPK.class, ship1.getId());
    	assertTrue("unexpected hashCode", ship1.hashCode() == ship4.hashCode());
    	assertTrue("unexpected equality", ship1.equals(ship4));
    	
    	log.debug("check if entity manager considers them the same");
    	assertFalse("em contained first entity", em.contains(ship1));
    	assertTrue("em did not contained second entity", em.contains(ship4));
    	
    	Set<Ship> ships = new HashSet<Ship>();
    	Ship ship5 = new ShipByPK().setName("one");
    	Ship ship6 = new ShipByPK().setName("two");
    	log.debug("add first ship to the set");
    	assertTrue("first entity not accepted into set", ships.add(ship5));
    	log.debug("add second ship to the set");
    	assertFalse("second entity accepted into set", ships.add(ship6));
    	assertEquals("unexpected set.size", 1, ships.size());
        log.debug("ships=" + ships);
    }
    
    /**
     * This method will demonstrate how instances can be identified as being equal
     * prior to and after persist() as long as they are in the same state.
     */
    @Test
    public void testBySwitch() {
    	log.info("*** testBySwitch ***");

    	Ship ship1 = new ShipBySwitch().setName("one");
    	Ship ship2 = new ShipBySwitch().setName("two");
    	assertFalse("unexpected hashCode", ship1.hashCode() == ship2.hashCode());
    	assertFalse("unexpected equality", ship1.equals(ship2));
    	
    	Set<Ship> ships = new HashSet<Ship>();
    	log.debug("add first ship to the set");
    	assertTrue("first entity not accepted into set", ships.add(ship1));
    	log.debug("add second ship to the set");
    	assertTrue("second entity not accepted into set", ships.add(ship2));
    	assertEquals("unexpected set.size", 2, ships.size());
        log.debug("ships=" + ships);
        
        em.persist(ship1);
    	em.flush();
    	em.clear();
    	log.debug("getting new instance of entity");
    	Ship ship4 = em.find(ShipBySwitch.class, ship1.getId());
    	assertTrue("unexpected hashCode", ship1.hashCode() == ship4.hashCode());
    	assertTrue("unexpected equality", ship1.equals(ship4));
    	
    	log.debug("set=" + ships);
    	log.debug("checking set for entity");
        assertFalse("set found changed entity after persist", ships.contains(ship1));
    }

    /**
     * This method will demonstrate how instances basing their hashCode and equals on
     * a consistent business identity through their lifetime offer the best solution
     * of them all -- assuming it it possible or needed. 
     */
    @Test
    public void testByBusinessId() {
    	log.info("*** testByBusinessId ***");

    	Ship ship1 = new ShipByBusinessId().setName("one").setCreated(new Date());
    	try { Thread.sleep(1);} catch (InterruptedException e) {}
    	Ship ship2 = new ShipByBusinessId().setName("two").setCreated(new Date());
    	assertFalse("unexpected hashCode", ship1.hashCode() == ship2.hashCode());
    	assertFalse("unexpected equality", ship1.equals(ship2));

    	Set<Ship> ships = new HashSet<Ship>();
    	log.debug("add first ship to the set");
    	assertTrue("first entity not accepted into set", ships.add(ship1));
    	log.debug("add second ship to the set");
    	assertTrue("second entity not accepted into set", ships.add(ship2));
    	assertEquals("unexpected set.size", 2, ships.size());
        log.debug("ships=" + ships);

        em.persist(ship1);
    	em.flush();
    	em.clear();
    	log.debug("getting new instance of entity");
    	Ship ship4 = em.find(ShipByBusinessId.class, ship1.getId());
    	ship1.setId(0); //making sure that databaseId not used in hashCode/equals
    	assertTrue("unexpected hashCode", ship1.hashCode() == ship4.hashCode());
    	assertTrue("unexpected equality", ship1.equals(ship4));
    	
    	log.debug("set=" + ships);
    	log.debug("checking set for entity");
        assertTrue("entity not found after persist", ships.contains(ship1));
    }
    
    /**
     * This method will demonstrate the ability to order entities by their business
     * values when stored within JPA collections. 
     */
    @Test
    public void testOrderBy() {
    	log.info("*** testOrderBy ***");
    	
    	Segment s1 = new Segment().setNumber(1).setFrom("A").setTo("B");
    	Segment s2 = new Segment().setNumber(2).setFrom("B").setTo("C");
    	Segment s3 = new Segment().setNumber(3).setFrom("C").setTo("D");
    	Path path = new Path();
    	path.addSegment(s2).addSegment(s3).addSegment(s1);
    	log.debug("path.segments=" + path.getSegments());
    	Iterator<Segment> itr = path.getSegments().iterator();
    	assertEquals(1, itr.next().getNumber());
    	assertEquals(2, itr.next().getNumber());
    	assertEquals(3, itr.next().getNumber());
    	
    	log.debug("getting new path instance from database");
    	em.persist(path);
    	em.flush(); em.clear();
    	Path path2 = em.find(Path.class, path.getId());
    	itr = path2.getSegments().iterator();
    	log.debug("path2.segments=" + path2.getSegments());
    	assertEquals(1, itr.next().getNumber());
    	assertEquals(2, itr.next().getNumber());
    	assertEquals(3, itr.next().getNumber());
    }
    
    /**
     * This test will demonstrate the ability to map child elements through Map interface.
     */
    @Test
    public void testMap() {
    	log.info("*** testMap ***");

    	Position players[] = new Position[] {
    			new Position("1st", "who"),
    			new Position("2nd", "what"),
    			new Position("3rd", "idontknow"),
    			new Position("1st", "whom"),
    			new Position("1st", "whoever")
    	};
    	log.debug("persisting players");
    	for (Position p: players) {
    		em.persist(p);
    	}

    	Lineup lineup = new Lineup();
    	lineup.setTeam("today");
    	lineup.addPosition(players[0]);
    	lineup.addPosition(players[1]);
    	lineup.addPosition(players[2]);
    	log.debug("persisting lineup");
    	em.persist(lineup);
    	
    	log.debug("getting new lineup instance");
    	em.flush(); em.clear();
    	Lineup lineup2 = em.find(Lineup.class, lineup.getId());
    	assertEquals("unexpected size", lineup.getPositions().size(), lineup2.getPositions().size());
    	for (int i=0; i<lineup.getPositions().size(); i++) {
    		assertNotNull(players[i].getPlayer() + " not found", lineup2.getPositions().get(players[i].getPosition()));
    	}
    	
    	log.debug("adding new player for position");
    	lineup2.addPosition(players[3]);
    	assertEquals("number of positions changed", lineup.getPositions().size(), lineup2.getPositions().size());
    	em.flush();
    	
    	log.debug("checking positions");
    	@SuppressWarnings("unchecked")
		List<Object[]> rows = em.createNativeQuery("select ID, LINEUP_ID from RELATIONEX_POSITION").getResultList();
		for (Object[] val : rows) {
			int id = (Integer)val[0];
			Integer lineupId = (Integer)val[1];
			if (id==players[1].getId() || id==players[2].getId() || id==players[3].getId()) {
				assertNotNull("unexpected lineupId", lineupId);
			} else {
				assertNull("lineupId was assigned for " + id, lineupId);
			}
		}
    }
}
