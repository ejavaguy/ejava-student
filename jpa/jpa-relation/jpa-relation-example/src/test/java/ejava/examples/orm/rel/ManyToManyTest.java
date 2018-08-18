package ejava.examples.orm.rel;

import static org.junit.Assert.*;


import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import ejava.examples.orm.rel.annotated.Author;
import ejava.examples.orm.rel.annotated.Media;
import ejava.examples.orm.rel.annotated.WantList;

/**
 * This test case provides a demo of a ManyToMany relationship. This,
 * of course, also uses a Join (or link) table.
 */
public class ManyToManyTest extends DemoBase {
    
    @Test
    public void testManyToManyUniCreate() {
        logger.info("testManyToManyUniCreate");
        Collection<WantList> wantLists = new ArrayList<WantList>(); 
        Collection<Media> media = new ArrayList<Media>(); 

        for(int i=0; i<3; i++) {
            ejava.examples.orm.rel.annotated.WantList wanted
                = new WantList();
            em.persist(wanted);
            wantLists.add(wanted);
            logger.info("created wantList:" + wanted);
        }
        for (int i=0; i<2; i++) {
            ejava.examples.orm.rel.annotated.Media mediaItem
                = new Media();
            mediaItem.setTitle("title " + i);
            em.persist(mediaItem);
            media.add(mediaItem);
            logger.info("created media:" + media);
        }

        for(WantList w: wantLists) {
            for(Media m: media) {
                //we can only navigate this in one direction
                w.getMedia().add(m);
                logger.info("added media(" + m.getId() + 
                        ") to want list (" + w.getId() +")");
            }
        }
        
        em.flush();
        em.clear();
        em.getTransaction().commit();
        
        //verify we don't have them in the cache
        for (WantList w: wantLists) {
            assertFalse("want list still managed:" + w.getId(), em.contains(w));
        }
        for (Media m: media) {
            assertFalse("media still managed: " + m.getId(), em.contains(m));
        }

        for (WantList w: wantLists) {
            WantList wanted = em.find(WantList.class, w.getId());
            assertNotNull("want list not found:" + w.getId(), wanted);
            logger.info("found want list:" + wanted);
            assertEquals(
                    "unepxected number of media:" + wanted.getMedia().size(), 
                    wanted.getMedia().size(), media.size());
        }
    }

    
    @Test
    public void testManyToManyBiCreate() {
        logger.info("testManyToManyBiCreate");
        Collection<Author> authors = new ArrayList<Author>(); 
        Collection<Media> media = new ArrayList<Media>(); 
        
        for(int i=0; i<5; i++) {
            ejava.examples.orm.rel.annotated.Author author
                = new Author();
            author.setName("author" + i);        
            em.persist(author);
            authors.add(author);
            logger.info("created author:" + author);
        }
        for (int i=0; i<3; i++) {
            ejava.examples.orm.rel.annotated.Media mediaItem
                = new Media();
            mediaItem.setTitle("title " + i);
            em.persist(mediaItem);
            media.add(mediaItem);
            logger.info("created media:" + media);
        }
        
        for(Author a: authors) {
            for(Media m: media) {
                a.getMedia().add(m);
                logger.info("added media(" + m.getId() + 
                        ") to author (" + a.getId() +")");
                m.getAuthors().add(a);
                logger.info("added author(" + a.getId() + 
                        ") to media (" + m.getId() +")");
            }
        }
        
        em.flush();
        em.clear();
        em.getTransaction().commit();
        
        //verify we don't have them in the cache
        for (Author a: authors) {
            assertFalse("author still managed:" + a.getId(), em.contains(a));
        }
        for (Media m: media) {
            assertFalse("media still managed: " + m.getId(), em.contains(m));
        }
        
        for (Author a: authors) {
            Author author = em.find(Author.class, a.getId());
            assertNotNull("author not found:" + a.getId(), author);
            logger.info("found author:" + author);
            assertEquals(
                    "unepxected number of media:" + author.getMedia().size(), 
                    author.getMedia().size(), media.size());
        }
        
        for (Media m: media) {
            Media mediaItem = em.find(Media.class, m.getId());
            assertNotNull("media not found:" + mediaItem.getId(), mediaItem);
            logger.info("found media:" + mediaItem);
            assertEquals(
                "unexpected number of authors:" + mediaItem.getAuthors().size(),
                mediaItem.getAuthors().size(), authors.size());
        }
   }
}
