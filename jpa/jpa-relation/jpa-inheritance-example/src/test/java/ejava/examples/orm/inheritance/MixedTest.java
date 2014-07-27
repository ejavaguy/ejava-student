package ejava.examples.orm.inheritance;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ejava.examples.orm.inheritance.annotated.BaseObject;
import ejava.examples.orm.inheritance.annotated.Circle;
import ejava.examples.orm.inheritance.annotated.Cube;
import ejava.examples.orm.inheritance.annotated.Rectangle;
import ejava.examples.orm.inheritance.annotated.Shape;

/**
 * This class provides an example of using an inheritance hierachy that 
 * attempts to mix strategies.
 */
public class MixedTest extends DemoBase {

	@Before
    public void setUp() throws Exception {
        @SuppressWarnings("unchecked")
        List<Shape> shapes = 
            em.createQuery("select s from Shape s").getResultList();
        for(Shape s : shapes) {
            em.remove(s);
        }
        em.flush();
        em.getTransaction().commit();
        em.getTransaction().begin();
    }

    @Test
    public void testNonEntityBaseAndJoinCreate() {
        log.info("testNonEntityBaseAndJoinCreate");
        
        ejava.examples.orm.inheritance.annotated.Rectangle rectangle = 
            new Rectangle();
        rectangle.setHeight(1);
        rectangle.setWidth(2);
        rectangle.setPosx(25);
        rectangle.setPosy(50);
        em.persist(rectangle);
        
        ejava.examples.orm.inheritance.annotated.Circle circle = 
            new Circle();
        circle.setPosx(40);
        circle.setPosy(20);
        circle.setRadius(4);
        em.persist(circle);
        
        em.flush();
        em.clear();
        assertFalse("rectangle still managed", em.contains(rectangle));
        assertFalse("circle still managed", em.contains(circle));
        
        @SuppressWarnings("unchecked")
        List<BaseObject> objects = 
            em.createQuery("select s from Shape s").getResultList();
        
        assertTrue("unexpected number of objects:" + objects.size(),
                objects.size() == 2);
        for(BaseObject o: objects) {
            log.info("object found:" + o);
        }        
        
        //query specific tables for columns
        int rows = em.createNativeQuery(
                "select ID, VERSION, POSX, POSY " +
                " from ORMINH_SHAPE")
                .getResultList().size();
        assertEquals("unexpected number of shape rows:" + rows, 2, rows);
        rows = em.createNativeQuery(
                "select ID, HEIGHT, WIDTH " +
                " from ORMINH_RECTANGLE")
                .getResultList().size();
        assertEquals("unexpected number of rectangle rows:" + rows, 1, rows);
        rows = em.createNativeQuery(
                "select ID, RADIUS " +
                " from ORMINH_CIRCLE")
                .getResultList().size();
        assertEquals("unexpected number of circle rows:" + rows, 1, rows);
    }
    
    @Test
    public void testNonEntityBaseAndTablePerClassCreate() {
        log.info("testNonEntityBaseAndTablePerClassCreate");
        
        ejava.examples.orm.inheritance.annotated.Cube cube = new Cube();
        cube.setDepth(2);
        cube.setHeight(3);
        cube.setWidth(5);
        cube.setPosx(12);
        cube.setPosy(15);
        em.persist(cube);
        
        em.flush();
        em.clear();
        assertFalse("cube still managed", em.contains(cube));
        
        @SuppressWarnings("unchecked")
		List<BaseObject> objects = 
            em.createQuery("select c from Cube c").getResultList();
        assertTrue("unexpected number of objects:" + objects.size(),
                objects.size() == 1);
        for(BaseObject o : objects) {
            log.info("object found:" + o);
        }
        
        //query specific tables for columns
        int rows = em.createNativeQuery(
                "select ID, VERSION, POSX, POSY " +
                " from ORMINH_SHAPE")
                .getResultList().size();
        assertEquals("unexpected number of shape rows:" + rows, 1, rows);
        rows = em.createNativeQuery(
                "select ID, HEIGHT, WIDTH " +
                " from ORMINH_RECTANGLE")
                .getResultList().size();
        assertEquals("unexpected number of rectangle rows:" + rows, 1, rows);
        rows = em.createNativeQuery(
                "select ID, DEPTH " +
                " from ORMINH_CUBE")
                .getResultList().size();
        assertEquals("unexpected number of rectangle rows:" + rows, 1, rows);
    }
}
