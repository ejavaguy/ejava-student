package myorg.relex;

import static org.junit.Assert.*;


import myorg.relex.many2many.Group;
import myorg.relex.many2many.Individual;
import myorg.relex.many2many.Node;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;

public class Many2ManyTest extends JPATestBase {
    private static Log log = LogFactory.getLog(Many2ManyTest.class);

    /**
     * This test demonstrates the capability to form and work with a many-to-many,
     * uni-directional relationship.
     */
    @Test
    public void testManyToManyUni() {
    	log.info("*** testManyToManyUni ***");
    	
    	log.debug("persisting owner");
    	Group group = new Group("board");
    	em.persist(group);
    	em.flush();
    	log.debug("persisting inverse");
    	Individual individual = new Individual("manny");
    	em.persist(individual);
    	em.flush();
    	log.debug("relating parent to inverse");
    	group.getMembers().add(individual);
    	em.flush();
    	
    	log.debug("getting new instances");
    	em.clear();
    	Group group2 = em.find(Group.class, group.getId());
    	log.debug("checking owner");
    	assertEquals("unexpected group.name", group.getName(), group2.getName());
    	log.debug("checking inverse");
    	assertEquals("unexpected size", 1, group2.getMembers().size());
    	assertEquals("unexpected member.name", individual.getName(), group2.getMembers().iterator().next().getName());
    	
    	log.debug("adding inverse members");
    	Individual individualB = new Individual("moe");    	
    	Individual individualC = new Individual("jack");    	
    	group2.getMembers().add(individualB);
    	group2.getMembers().add(individualC);
    	em.persist(individualB);
    	em.persist(individualC);
    	em.flush();
    	
    	log.debug("adding owning members");
    	Group groupB = new Group("night shift");
    	groupB.getMembers().add(individualB);
    	groupB.getMembers().add(individualC);
    	em.persist(groupB);
    	em.flush();
    	
    	log.debug("checking relations");
    	assertEquals("unexpected relations for member 1", 1, em.createQuery(
    			"select count(g) from Group g where :individual member of g.members", Number.class)
    			.setParameter("individual", individual)
    			.getSingleResult().intValue());
    	assertEquals("unexpected relations for member 2", 2, em.createQuery(
    			"select count(g) from Group g where :individual member of g.members", Number.class)
    			.setParameter("individual", individualB)
    			.getSingleResult().intValue());
    	assertEquals("unexpected relations for member 3", 2, em.createQuery(
    			"select count(g) from Group g where :individual member of g.members", Number.class)
    			.setParameter("individual", individualC)
    			.getSingleResult().intValue());
    	
    	log.debug("removing relations");
    	assertTrue(group2.getMembers().remove(individualB));
    	assertTrue(groupB.getMembers().remove(individualB));
    	log.debug("verifying relation removal");
    	assertEquals("unexpected relations for member 1", 0, em.createQuery(
    			"select count(g) from Group g, IN (g.members) m where m = :individual", Number.class)
    			.setParameter("individual", individualB)
    			.getSingleResult().intValue());

    	log.debug("verifying inverse was not removed");
    	em.flush(); em.clear();
    	assertNotNull(em.find(Individual.class, individualB.getId()));
    	log.debug("removing initial owner");
    	em.remove(em.find(Group.class, group.getId()));
    	em.flush();
    }
    
    /**
     * This test provides an example of a many-to-many, bi-directional relationship that just happens
     * to use a single, recursive entity on both sides of the relationsip.
     */
    @Test
    public void testManyToManyBi() {
    	log.info("*** testManyToManyBi ***");
    	
    	log.debug("create instances");
    	Node one = new Node("one");
    	Node two = new Node(one,"two");
    	em.persist(one);
    	em.flush();
    	
    	log.debug("getting new instances from owning side");
    	em.clear();
    	Node one2 = em.find(Node.class, one.getId());
    	assertNotNull("owning side not found", one2);
    	log.debug("checking owning side");
    	assertEquals("unexpected owning.name", one.getName(), one2.getName());
    	log.debug("checking parents");
    	assertEquals("unexpected parents.size", 0, one2.getParents().size());
    	log.debug("checking children");
    	assertEquals("unexpected children.size", 1, one2.getChildren().size());
    	assertEquals("unexpected child.name", two.getName(), one2.getChildren().iterator().next().getName());
    	
    	log.debug("adding more inverse instances");
    	Node twoB = new Node(one2, "twoB");
    	Node twoC = new Node(one2, "twoC");
    	em.persist(one2);
    	em.flush();
    	
    	log.debug("getting new instances from inverse side");
    	em.clear();
    	Node two2 = em.find(Node.class, two.getId());
    	assertNotNull("inverse node not found", two2);
    	log.debug("checking inverse side");
    	assertEquals("unexpected name", two.getName(), two2.getName());
    	log.debug("checking parents");
    	assertEquals("unexpected parents.size", 1, two2.getParents().size());
    	log.debug("checking children");
    	assertEquals("unexpected children.size", 0, two2.getChildren().size());
    	
    	log.debug("adding owning entity");
    	Node oneB = new Node("oneB");
    	oneB.getChildren().add(two2);
    	two2.getParents().add(oneB);
    	em.persist(oneB);
    	em.flush();
    	
    	log.debug("checking relationships");
    	assertEquals("unexpected parents", 0,
    			em.createQuery("select count(p) from Node n, IN (n.parents) p where n=:node", Number.class)
		    		.setParameter("node", one)
		    		.getSingleResult().intValue());
    	assertEquals("unexpected parents", 2,
    			em.createQuery("select count(p) from Node n, IN (n.parents) p where n=:node", Number.class)
		    		.setParameter("node", two)
		    		.getSingleResult().intValue());
    	assertEquals("unexpected parents", 1,
    			em.createQuery("select count(p) from Node n, IN (n.parents) p where n=:node", Number.class)
		    		.setParameter("node", twoB)
		    		.getSingleResult().intValue());
    	assertEquals("unexpected parents", 1,
    			em.createQuery("select count(p) from Node n, IN (n.parents) p where n=:node", Number.class)
		    		.setParameter("node", twoC)
		    		.getSingleResult().intValue());    	
    	assertEquals("unexpected children", 3,
    			em.createQuery("select count(c) from Node n, IN (n.children) c where n=:node", Number.class)
		    		.setParameter("node", one)
		    		.getSingleResult().intValue());
    	assertEquals("unexpected children", 0,
    			em.createQuery("select count(c) from Node n, IN (n.children) c where n=:node", Number.class)
		    		.setParameter("node", two)
		    		.getSingleResult().intValue());    	
    	assertEquals("unexpected children", 1,
    			em.createQuery("select count(c) from Node n, IN (n.children) c where n=:node", Number.class)
		    		.setParameter("node", oneB)
		    		.getSingleResult().intValue());
    	
    	log.debug("getting managed owning side");
    	assertNotNull(one = em.find(Node.class, one.getId()));
    	log.debug("removing relationship");
    	one.getChildren().remove(two);
    	two.getParents().remove(one);
    	em.flush();
    	assertEquals("unexpected children", 2,
    			em.createQuery("select count(c) from Node n, IN (n.children) c where n=:node", Number.class)
		    		.setParameter("node", one)
		    		.getSingleResult().intValue());
    	assertEquals("unexpected parents", 1,
    			em.createQuery("select count(p) from Node n, IN (n.parents) p where n=:node", Number.class)
		    		.setParameter("node", two)
		    		.getSingleResult().intValue());
    	
    	log.debug("deleting owner");
    	em.remove(oneB);
    	em.flush();
    	assertEquals("unexpected parents", 0,
    			em.createQuery("select count(p) from Node n, IN (n.parents) p where n=:node", Number.class)
		    		.setParameter("node", two)
		    		.getSingleResult().intValue());

    	log.debug("deleting inverse");
    	assertNotNull(twoB = em.find(Node.class, twoB.getId()));
    	em.remove(twoB);
    	em.flush();
//    	assertNull("inverse not deleted", em.find(Node.class, twoB.getId()));
    	one.getChildren().remove(twoB);
    	em.flush();
//    	assertNull("inverse not deleted", em.find(Node.class, twoB.getId()));
    	em.remove(twoB);
    	em.flush();
    	assertNull("inverse not deleted", em.find(Node.class, twoB.getId()));
    }
}
