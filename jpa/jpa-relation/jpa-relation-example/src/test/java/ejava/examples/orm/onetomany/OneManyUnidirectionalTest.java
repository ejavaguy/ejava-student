package ejava.examples.orm.onetomany;

import java.util.List;

import org.junit.Test;

import ejava.examples.orm.onetomany.annotated.OneManyChild;
import ejava.examples.orm.onetomany.annotated.OneManyOwningParent;
import ejava.examples.orm.rel.DemoBase;


/**
 * 
 */
public class OneManyUnidirectionalTest extends DemoBase {
    
    protected void precleanup() throws Exception { cleanup(); }
    //protected void postcleanup() throws Exception { cleanup(); }
    
    /**
     * This method will cleanup the database of our OneToMany entities. We
     * start with the owning entities, since they own the foreign keys or
     * the link/join tables with the foreign keys. Once the associations 
     * have been deleted, all inverse and oblivious entities can be safely 
     * removed.
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected void cleanup() throws Exception {
        try {
            //remove the link table before removing the owning parent
            List<OneManyOwningParent> owningParents = em.createQuery(
                    "select owningParent from O2MOwningParent owningParent")
                    .getResultList();
            log.debug("removing " + owningParents.size() + " owningParents");
            for (OneManyOwningParent parent : owningParents) {
                parent.getChildren().clear();
                log.debug("removing:" + parent);
                em.remove(parent);
            }
                
            //no in-coming foreign keys to child should be left; delete
            List<OneManyChild> children = em.createQuery(
                    "select child from O2MChild child")
                    .getResultList();
            log.debug("removing " + children.size() + " children");
            for (OneManyChild child : children) {
                log.debug("removing:" + child);
                em.remove(child);
            }
            em.getTransaction().begin();
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            log.error("error removing objects", ex);
            throw ex;
        }
    }
    
    

    /**
     * This method tests the case where the parent owns the relationship. 
     * Since children in a one-to-many relationship are required by JPA
     * to own the relationship, we can only test this with children that
     * know nothing of the parent. In this case, there is only an owning 
     * side. 
     */
    @Test
    public void testOneToManyOwningParent() {
        log.info("*** testOneToManyOwningParent ***");
        
        OneManyOwningParent oneManyOwningParent = 
                new OneManyOwningParent("tom");
        
        OneManyChild oneManyChild1 = new OneManyChild("david");
        oneManyOwningParent.getChildren().add(oneManyChild1);
        
        OneManyChild oneManyChild2 = new OneManyChild("tommy");
        oneManyOwningParent.getChildren().add(oneManyChild2);		
        
        em.persist(oneManyOwningParent);
        em.persist(oneManyChild1);
        em.persist(oneManyChild2);
            
        em.flush();
        em.getTransaction().commit();
        log.info("persisted oneManyOwningParent=" + oneManyOwningParent);
        log.info("persisted oneManyChild1=" + oneManyChild1);
        log.info("persisted oneManyChild2=" + oneManyChild2);
        
        em.clear();

        OneManyOwningParent oneManyOwningParentA =
                em.find(OneManyOwningParent.class,oneManyOwningParent.getId());
        OneManyChild oneManyChild1a = 
                em.find(OneManyChild.class, oneManyChild1.getId());
        OneManyChild oneManyChild2a = 
                em.find(OneManyChild.class, oneManyChild2.getId());
        
        log.info("found oneManyOwningParentA=" + oneManyOwningParentA);
        log.info("found oneManyChild1a=" + oneManyChild1a);
        log.info("found oneManyChild2a=" + oneManyChild2a);
    }
}
