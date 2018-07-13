package myorg.relex;

import static org.junit.Assert.*;
import java.util.Date;
import java.util.List;

import myorg.relex.many2one.House;
import myorg.relex.many2one.HousePK;
import myorg.relex.many2one.Item;
import myorg.relex.many2one.ItemPK;
import myorg.relex.many2one.ItemType;
import myorg.relex.many2one.Occupant;
import myorg.relex.many2one.State;
import myorg.relex.many2one.StateResident;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.*;

public class Many2OneTest extends JPATestBase {
    private static Logger log = LoggerFactory.getLogger(Many2OneTest.class);

    /**
     * This test demonstrates the ability to form a many to one, unit directional relationship
     * between entities.
     */
    @Test
    public void testManyToOneUniFK() {
        log.info("*** testManyToOneUniFK ***");
        
        State state = new State("MD", "Maryland");
        StateResident res = new StateResident(state);
        res.setName("joe");
        log.debug("persisting parent");
        em.persist(state);
        log.debug("persisting child");
        em.persist(res);
        em.flush();
        
        log.debug("getting new instances");
        em.clear();
        StateResident res2 = em.find(StateResident.class, res.getId());
        log.debug("checking child");
        assertEquals("unexpected child data", res.getName(), res2.getName());
        log.debug("checking parent");
        assertEquals("unexpected parent data", state.getName(), res2.getState().getName());
        
        log.debug("add more child entities");
        StateResident resB = new StateResident(res2.getState());
        em.persist(resB);
        em.flush();

        log.debug("getting new instances of children");
        em.clear();
        List<StateResident> residents = em.createQuery(
	        		"select r from StateResident r " +
	        		"where r.state.id=:stateId", 
	        		StateResident.class)
        		.setParameter("stateId", res.getState().getId())
        		.getResultList();
        assertEquals("unexpected number of children", 2, residents.size());

        log.debug("changing state/data of common parent");
        residents.get(0).getState().setName("Home State");
        assertEquals("unexpected difference in parent data", 
        		residents.get(0).getState().getName(),
        		residents.get(1).getState().getName());
    }
    
    /**
     * This test will demonstrate using a compound foreign key in a many-to-one,
     * uni-directional relationship.    
     */
    @Test
    public void testManyToOneUniCompoundFK() {
        log.info("*** testManyToOneUniCompoundFK ***");
        
        House house = new House(new HousePK(1600,"PA Ave"),"White House");
        Occupant occupant = new Occupant("bo", house);
        log.debug("persisting parent");
        em.persist(house);
        log.debug("persisting child");
        em.persist(occupant);
        em.flush();

        log.debug("getting new instances");
        em.clear();
        Occupant occupant2 = em.find(Occupant.class, occupant.getId());
        log.debug("checking child");
        assertEquals("unexpected child data", occupant.getName(), occupant2.getName());
        log.debug("checking parent");
        assertEquals("unexpected parent data", house.getName(), occupant2.getResidence().getName());
        
        log.debug("add more child entities");
        Occupant occupantB = new Occupant("miss beazily", occupant2.getResidence());
        em.persist(occupantB);
        em.flush();

        log.debug("getting new instances of children");
        em.clear();
        List<Occupant> occupants = em.createQuery(
	        		"select o from Occupant o " +
	        		"where o.residence.id=:houseId", 
	        		Occupant.class)
        		.setParameter("houseId", occupant.getResidence().getId())
        		.getResultList();
        assertEquals("unexpected number of children", 2, occupants.size());
    }
    
    /**
     * This method demonstrates how the foreign key can be mapped to a compound primary
     * key property of the child class. This means that one of the primary key columns of 
     * the child is *also* used as the foreign key to the parent.
     */
    @Test
    public void testManyToOneUniMapsIdEmbedded() {
        log.info("*** testManyToOneUniMapsIdEmbedded ***");
        
        ItemType type = new ItemType("snowblower");
        log.debug("persisting parent:" + type);
        em.persist(type);
        em.flush();
        log.debug("persisted parent:" + type);
        
        Item item = new Item(type,1);
        item.setCreated(new Date());
        log.debug("persisting child:" + item);
        em.persist(item);
        em.flush();
        log.debug("persisted child:" + item);
        //check PK assigned
        ItemPK pk = new ItemPK().setTypeId(type.getId()).setNumber(1);
        assertTrue(String.format("expected PK %s not match actual %s", pk, 
        		item.getId()), pk.equals(item.getId()));
        
        log.debug("getting new instances");
        em.clear();
        Item item2 = em.find(Item.class, pk);
        log.debug("checking child");
        assertNotNull("child not found by primary key:" + pk, item2);
        assertTrue("unexpected child data", item.getCreated().equals(item2.getCreated()));
        log.debug("checking parent");
        assertEquals("unexpected parent data", type.getName(), item2.getItemType().getName());
        
        Item itemB = new Item(item2.getItemType(),2);
        log.debug("add more child entities:" + itemB);
        itemB.setCreated(new Date());
        em.persist(itemB);
        em.flush();
        log.debug("new child entities added:" + itemB);

        log.debug("getting new instances of children");
        em.clear();
        List<Item> items = em.createQuery(
	        		"select i from Item i " +
	        		//"where i.itemType.id=:typeId", 
	        		"where i.id.typeId=:typeId", 
	        		Item.class)
        		.setParameter("typeId", item.getItemType().getId())
        		.getResultList();
        assertEquals("unexpected number of children", 2, items.size());
    }
}
