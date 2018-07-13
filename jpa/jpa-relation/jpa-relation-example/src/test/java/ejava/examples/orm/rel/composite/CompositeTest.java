package ejava.examples.orm.rel.composite;

import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.orm.rel.DemoBase;

public class CompositeTest extends DemoBase {
	private static final Logger log = LoggerFactory.getLogger(CompositeTest.class);

	@Test
	public void testManyToOneIdClassPKReuse() {
		log.info("*** testManyToOneIdClassPKReuse ***");
		
		House house = new House();
		em.persist(house); //generate a PK for parent
		house.getRooms().add(new Room(house,0));
		house.getRooms().add(new Room(house,1));
		house.getRooms().add(new Room(house,2));
		em.persist(house); //cascade persists to children
		
		//get a new copy of house
		em.flush(); em.clear();
		House house2 = em.find(House.class, house.getId());
		assertEquals("unexpected number of rooms", house.getRooms().size(), house2.getRooms().size());
		for (Room room: house2.getRooms()) {
			assertNotNull("null house", room.getHouse());
			assertEquals("unexpected room.house.id", house.getId(), room.getHouse().getId());
		}
	}

	@Test
	public void testManyToOneEmbeddedIdPKReuse() {
		log.info("*** testManyToOneEmbeddedIdPKReuse ***");
		
		House house = new House();
		em.persist(house); //generate a PK for parent
		house.getDoors().add(new Door(house,0));
		house.getDoors().add(new Door(house,1));
		house.getDoors().add(new Door(house,2));
		em.persist(house); //cascade persists to children
		
		//get a new copy of house
		em.flush(); em.clear();
		House house2 = em.find(House.class, house.getId());
		assertEquals("unexpected number of doors", house.getDoors().size(), house2.getDoors().size());
		for (Door door: house2.getDoors()) {
			assertNotNull("null house", door.getHouse());
			assertEquals("unexpected door.house.id", house.getId(), door.getHouse().getId());
		}
	}

	@Test
	public void testManyToOneIdClassPK() {
		log.info("*** testManyToOneIdClassPK ***");
		
		House house = new House();
		em.persist(house); //generate a PK for parent
		house.getResidents().add(new Resident(house,0));
		house.getResidents().add(new Resident(house,1));
		house.getResidents().add(new Resident(house,2));
		em.persist(house); //cascade persists to children
		
		//get a new copy of house
		em.flush(); em.clear();
		House house2 = em.find(House.class, house.getId());
		assertEquals("unexpected number of doors", house.getResidents().size(), house2.getResidents().size());
		for (Resident resident: house2.getResidents()) {
			assertNotNull("null house", resident.getHouse());
			assertEquals("unexpected resident.house.id", house.getId(), resident.getHouse().getId());
		}
	}

	@Test
	public void testManyToOneEmbeddedIdMapsId() {
		log.info("*** testManyToOneEmbeddedIdMapsId ***");
		
		House house = new House();
		em.persist(house); //generate a PK for parent
		house.getMortgages().add(new Mortgage(house,0));
		house.getMortgages().add(new Mortgage(house,1));
		house.getMortgages().add(new Mortgage(house,2));
		em.persist(house); //cascade persists to children
		
		//get a new copy of house
		em.flush(); em.clear();
		House house2 = em.find(House.class, house.getId());
		assertEquals("unexpected number of mortgages", house.getMortgages().size(), house2.getMortgages().size());
		for (Mortgage mortgage: house2.getMortgages()) {
			assertNotNull("null house", mortgage.getHouse());
			assertEquals("unexpected mortgage.house.id", house.getId(), mortgage.getHouse().getId());
		}
	}
}
