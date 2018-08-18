package ejava.examples.orm.rel;

import static org.junit.Assert.*;


import org.junit.Test;

import ejava.examples.orm.rel.annotated.Media;
import ejava.examples.orm.rel.annotated.MediaCopy;
import ejava.examples.orm.rel.annotated.MediaCopy2;

/**
 * This test case provides a demonstration of a amny-to-one, uni-directional
 * mapping.
 */
public class ManyToOneUnidirectionalTest extends DemoBase {
    
    /**
     */
    public void setUpBase() throws Exception {
        super.setUpBase();
    }

    /**
     * This version of MediaCopy uses a READ-ONLY foreign key mapping
     * based on JPA 1.0
     */
    @Test
    public void testCreateManyToOne() {
        logger.info("testCreateManyToOne");
        ejava.examples.orm.rel.annotated.Media media = new Media();
        media.setTitle("EJB3");
        
        //add media to DB
        assertTrue(media.getId() == 0);
        em.persist(media);
        logger.info("created media:" + media);
        assertTrue(media.getId() != 0);
        
        //create some copies 
        for(int i=0; i<5; i++) {
            ejava.examples.orm.rel.annotated.MediaCopy mc = 
                new MediaCopy(media, i);
            assertNotNull(mc.getMedia());
            assertEquals(i, mc.getCopyNo());
            em.persist(mc);
            logger.info("created copy:" + mc);
        }
        
        em.flush();
        em.clear();
        
        for(int i=0; i<5; i++) {
            ejava.examples.orm.rel.MediaCopyPK pk =
                new MediaCopyPK(media.getId(), i);
            MediaCopy mc = em.find(MediaCopy.class, pk);
            logger.info("found media copy:" + mc);
            assertNotNull("media copy not found:" + pk, mc);
            assertEquals("unexpected mediaId:" + mc.getMediaId(),
                    mc.getMediaId(), media.getId());
            assertEquals("unexpected copyNo:" + mc.getCopyNo(), 
                    mc.getCopyNo(), i);
            assertNotNull("no media resolved", mc.getMedia());
        }
   }    

    /**
     * This version of the MediaCopy uses a JPA 2.0 @MapsId annotation to link
     * the foreign and primary key properties.
     */
    @Test
    public void testCreateManyToOneMapsId() {
        logger.info("testCreateManyToOneMapsId");
        ejava.examples.orm.rel.annotated.Media media = new Media();
        media.setTitle("EJB31");
        
        //add media to DB
        assertTrue(media.getId() == 0);
        em.persist(media);
        logger.info("created media:" + media);
        assertTrue(media.getId() != 0);
        
        //create some copies 
        for(int i=0; i<5; i++) {
            ejava.examples.orm.rel.annotated.MediaCopy2 mc = 
                new MediaCopy2(media, i);
            assertNotNull(mc.getMedia());
            assertEquals(i, mc.getCopyNo());
            em.persist(mc);
            logger.info("created copy:" + mc);
        }
        
        em.flush();
        em.clear();
        
        for(int i=0; i<5; i++) {
            ejava.examples.orm.rel.MediaCopyPK2 pk =
                new MediaCopyPK2(media.getId(), i);
            MediaCopy2 mc = em.find(MediaCopy2.class, pk);
            logger.info("found media copy:" + mc);
            assertNotNull("media copy not found:" + pk, mc);
            assertEquals("unexpected mediaId:" + mc.getMediaId(),
                    mc.getMediaId(), media.getId());
            assertEquals("unexpected copyNo:" + mc.getCopyNo(), 
                    mc.getCopyNo(), i);
            assertNotNull("no media resolved", mc.getMedia());
        }
   }    
}
