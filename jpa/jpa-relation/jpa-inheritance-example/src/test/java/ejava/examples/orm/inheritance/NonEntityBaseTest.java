package ejava.examples.orm.inheritance;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import ejava.examples.orm.inheritance.annotated.Album;
import ejava.examples.orm.inheritance.annotated.BaseObject;
import ejava.examples.orm.inheritance.annotated.ToothPaste;

/**
 * This class provides a demonstration of a class hierachy that inherits
 * from a non-entity class. 
 */
public class NonEntityBaseTest extends DemoBase {

	@SuppressWarnings("unchecked")
	@Test
	public void testNonEntityBaseCreate() {
        log.info("testNonEntityBaseCreate");
        
        ejava.examples.orm.inheritance.annotated.Album album = new Album();
        album.setArtist("Lynyrd Skynyrd");
        album.setTitle("One More for the Road");
        em.persist(album);
        
        ejava.examples.orm.inheritance.annotated.ToothPaste toothpaste = new ToothPaste();
        toothpaste.setSize(10);
        em.persist(toothpaste);
        
        em.flush();
        em.clear();
        assertFalse("album still managed", em.contains(album));
        assertFalse("toothpaste still managed", em.contains(toothpaste));
        
        List<BaseObject> objects = 
            em.createQuery("select a from Album a").getResultList();
        objects.addAll( 
            em.createQuery("select tp from ToothPaste tp").getResultList());
        
        assertTrue("unexpected number of objects:" + objects.size(),
                objects.size() == 2);
        for(BaseObject o: objects) {
            log.info("object found:" + o);
        }        

        //query specific tables for columns
        int rows = em.createNativeQuery(
                "select ALBUM_ID, ALBUM_VERSION, ARTIST, TITLE " +
                " from ORMINH_ALBUM")
                .getResultList().size();
        assertEquals("unexpected number of album rows:" + rows, 1, rows);
        rows = em.createNativeQuery(
                "select ID, VERSION, SIZE " +
                " from ORMINH_TOOTHPASTE")
                .getResultList().size();
        assertEquals("unexpected number of toothpaste rows:" + rows, 1, rows);
    }
}
