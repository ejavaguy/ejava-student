package ejava.examples.orm.rel;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;

import ejava.examples.orm.rel.annotated.Inventory;
import ejava.examples.orm.rel.annotated.Media;

/**
 * This test case provides a demo of a OneToMany relationship using a Join
 * (or Link) table. In this relationship, the foreign keys are in a table
 * separate from the two related items.
 */
public class OneToManyJoinTableTest extends DemoBase {
    
    @Test
    public void testOneToManyJoinCreate() {
        log.info("testLinkCreate");
        ejava.examples.orm.rel.annotated.Inventory inventory
            = new Inventory();
        inventory.setName("testLinkCreate");
        
        em.persist(inventory);
        
        for(int i=0; i<5; i++) {
            ejava.examples.orm.rel.annotated.Media media = new Media();
            em.persist(media);
            log.info("created media:" + media);
            inventory.getMedia().add(media);
        }
        log.info("created inventory:" + inventory);
    }
    
    @Test
    public void testOneToManyJoinFind() {
        log.info("testOneToManyJoinFind()");
        
        ejava.examples.orm.rel.annotated.Inventory inventory1
            = new Inventory();
        inventory1.setName("testLinkFind");
        
        em.persist(inventory1);
        
        for(int i=0; i<5; i++) {
            ejava.examples.orm.rel.annotated.Media media = new Media();
            //em.persist(media);
            log.info("created media:" + media);
            inventory1.getMedia().add(media);
        }
        log.info("creating inventory:" + inventory1);

        em.flush();        
        em.clear();
        em.getTransaction().commit();
        em.getTransaction().begin();

        assertFalse("inventory still managed", em.contains(inventory1));
        
        Inventory inventory2 = em.find(Inventory.class, inventory1.getId());
        log.info("found inventory:" + inventory2);
        
        assertNotNull("inventory not found", inventory2);
        assertNotSame(inventory1, inventory2);
        assertTrue("inventory name unexpected:" + inventory2.getName(), 
                inventory1.getName().equals(inventory2.getName()));
        assertNotNull("media null", inventory2.getMedia());
        assertEquals("unexpected media count:" + inventory2.getMedia().size(), 
                inventory1.getMedia().size(), inventory2.getMedia().size());
        findMedia(inventory2.getMedia(), true);
    }

    @Test
    public void testOneToManyJoinRemove() {
        log.info("testOneToManyJoinRemove");
        ejava.examples.orm.rel.annotated.Inventory inventory1
            = new Inventory();
        inventory1.setName("testRemove");
        
        em.persist(inventory1);
        
        for(int i=0; i<5; i++) {
            ejava.examples.orm.rel.annotated.Media media = new Media();
            em.persist(media);
            log.info("created media:" + media);
            inventory1.getMedia().add(media);
        }
        log.info("creating inventory:" + inventory1);
    
        em.flush();        
        em.clear();
        em.getTransaction().commit();
        em.getTransaction().begin();
        
        assertFalse("inventory still managed", em.contains(inventory1));
        Inventory inventory2 = em.find(Inventory.class, inventory1.getId()); 
        assertNotNull("inventory not found", inventory2);
        findMedia(inventory2.getMedia(), true);
        em.remove(inventory2);
        log.info("removed inventory:" + inventory2);
        assertTrue("inventory not removed",
            em.createQuery("select object(i) from Inventory as i where i.id=" + 
                    inventory2.getId()).getResultList().size() == 0);
        //the media will also be deleted because of cascade=All
        findMedia(inventory2.getMedia(), false);
        
   }
    
    private void findMedia(Collection<Media> media, boolean exist) {
        log.info("looking for media objects:" + media.size());
        for(Media m : media) {
            if (exist) {
                assertTrue("media not found:" + m.getId(), 
                    em.find(Media.class, m.getId()) != null);
                log.info("found media:" + m.getId());
            }
            else {
                assertFalse("media not found:" + m.getId(), 
                        em.find(Media.class, m.getId()) != null);
                    log.info("media not found:" + m.getId());            
            }
        }
    }
}
