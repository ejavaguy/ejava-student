package ejava.examples.orm.map;

import java.util.List;

import org.junit.Test;

import ejava.examples.orm.map.annotated.OneManyChild;
import ejava.examples.orm.map.annotated.OneManyInverseParent;
import ejava.examples.orm.map.annotated.OneManyOwningChild;
import ejava.examples.orm.map.annotated.OneManyOwningParent;
import ejava.examples.orm.rel.DemoBase;


/**
 * This class demonstrates a One-to-Many relationship represented as a map
 * in the parent. There are two types of parents and children represented in 
 * these tests:
 * <nl>
 * <li>OneManyInverseParent to/from OneManyOwningChild - in this case, the 
 * foreign
 * key to the parent is supplied in the child table. The child owns the
 * relationship and the parent has a inverse reference back to the child 
 * represented as a Map.</li>
 * 
 * <li>OneManyOwningParent to OneManyChild - in this case, the parent owns 
 * the relationship and the child class knows nothing of the relationship.
 * The parent forms a link table to host the 1:1 foreign key to the child
 * and a 1:N relationship to the parent. The child object is referenced by the
 * parent through a Map.</li>
 * </nl><p/>
 * 
 * The other cases are meaningless to demonstrate because they would either
 * involve the parent not knowing about the child (thus no Map), or neither 
 * parent or child knowing about one another (totally senseless for this
 * example). 
 * <nl>
 */
public class OneManyMapTest extends DemoBase {
    
    protected void precleanup() throws Exception { cleanup(); }
    protected void postcleanup() throws Exception { cleanup(); }
    
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
                    "select owningParent from OneManyOwningParent owningParent")
                    .getResultList();
            log.debug("removing " + owningParents.size() + " owningParents");
            for (OneManyOwningParent parent : owningParents) {
                parent.getOwnedChildren().clear();
                log.debug("removing:" + parent);
                em.remove(parent);
            }
            //em.getTransaction().begin();
            //em.getTransaction().commit();
    
            //foreign key will be removed when owning child removed
            List<OneManyOwningChild> owningChildren = em.createQuery(
                    "select owningChild from OneManyOwningChild owningChild")
                    .getResultList();
            log.debug("removing " + owningChildren.size() + " owningChildren");
            for (OneManyOwningChild child : owningChildren) {
                child.setOneInverseParent(null);
                log.debug("removing:" + child);
                em.remove(child);
            }
            //em.getTransaction().begin();
            //em.getTransaction().commit();
    
            //no in-coming foreign keys to inverse parent should be left; delete
            List<OneManyInverseParent> inverseParents = em.createQuery(
                    "select inverseParent from OneManyInverseParent inverseParent")
                    .getResultList();
            log.debug("removing " + inverseParents.size() + " inverseParents");
            for (OneManyInverseParent parent : inverseParents) {
                log.debug("removing:" + parent);
                em.remove(parent);
            }
            //em.getTransaction().begin();
            //em.getTransaction().commit();
            
            //no in-coming foreign keys to child should be left; delete
            List<OneManyChild> children = em.createQuery(
                    "select child from OneManyChild child")
                    .getResultList();
            log.debug("removing " + children.size() + " children");
            for (OneManyChild child : children) {
                log.debug("removing:" + child);
                em.remove(child);
            }
            //em.getTransaction().begin();
            //em.getTransaction().commit();
        }
        catch (Exception ex) {
            log.error("error removing objects", ex);
            throw ex;
        }
    }
    
    

    /**
	 * This method tests the case where the child owns the relationship
	 * and the parent is the inverse side. If we model the Java form of 
	 * the relationship without defining a @MapKey, then the following 
	 * schema gets created,<p/>
     * <pre>
    create table ORMMAP_ONEMANY_INVPARENT (
        name varchar(255) not null,
        primary key (name)
    );

    create table ORMMAP_ONEMANY_OWNCHILD (
        name varchar(255) not null,
        oneInverseParent_name varchar(255),
        mapkey varchar(255),
        primary key (name)
    );
	 </pre><p/>
     * But runtime reports<p/>
     * <pre>
     null index column for collection: 
     ejava.examples.orm.map.annotated.OneManyInverseParent.ownedByChildren
     </pre><p/>
     * 
     * When @MapKey(name="name") is added to the parent definition...<p/>
     * <pre>
    @OneToMany(mappedBy="oneInverseParent")
    @MapKey(name="name")
    public Map<String, OneManyOwningChild> getOwnedByChildren() {
        return ownedByChildren;
    }
     </pre><p/>
     * The schema changes to the following...<p/>
     * <pre>
    create table ORMMAP_ONEMANY_INVPARENT (
        name varchar(255) not null,
        primary key (name)
    );

    create table ORMMAP_ONEMANY_OWNCHILD (
        name varchar(255) not null,
        oneInverseParent_name varchar(255),
        primary key (name)
    );
     </pre><p/>
	 */
	@Test
	public void testOneToManyInverseParentMap() {
		log.info("*** testOneToManyInverseParentMap ***");
		
		OneManyInverseParent oneManyInverseParent = 
			new OneManyInverseParent("ozzie");
		
		OneManyOwningChild oneManyOwnChild1 = new OneManyOwningChild("david");
		oneManyOwnChild1.setOneInverseParent(oneManyInverseParent);
		oneManyInverseParent.getOwnedByChildren().put(
				oneManyOwnChild1.getName(), oneManyOwnChild1);
		
		OneManyOwningChild oneManyOwnChild2 = new OneManyOwningChild("ricky");
		oneManyOwnChild2.setOneInverseParent(oneManyInverseParent);
		oneManyInverseParent.getOwnedByChildren().put(
				oneManyOwnChild2.getName(), oneManyOwnChild2);		
		
		em.persist(oneManyInverseParent);
		em.persist(oneManyOwnChild1);
		em.persist(oneManyOwnChild2);
		
		em.flush();
		em.getTransaction().commit();
		log.info("persisted oneManyInverseParent=" + oneManyInverseParent);
		log.info("persisted oneManyOwnChild1=" + oneManyOwnChild1);
		log.info("persisted oneManyOwnChild2=" + oneManyOwnChild2);
		
		em.clear();

		OneManyInverseParent oneManyInverseParentA =
			em.find(OneManyInverseParent.class,oneManyInverseParent.getName());
		OneManyOwningChild oneManyOwnChild1a = 
			em.find(OneManyOwningChild.class, oneManyOwnChild1.getName());
		OneManyOwningChild oneManyOwnChild2a = 
			em.find(OneManyOwningChild.class, oneManyOwnChild2.getName());
		
		log.info("found oneManyInverseParentA=" + oneManyInverseParentA);
		log.info("found oneManyOwnChild1a=" + oneManyOwnChild1a);
		log.info("found oneManyOwnChild2a=" + oneManyOwnChild2a);
	}
	
    /**
     * This method tests the case where the parent owns the relationship. 
     * Since children in a one-to-many relationship are required by JPA
     * to own the relationship, we can only test this with children that
     * know nothing of the parent. In this case, there is only an owning 
     * side. If we model the Java form of 
     * the relationship without defining a @MapKey, then the following 
     * schema gets created (note that in this class, the name property has 
     * been mapped to the @Column(name="ID") within each Entity class).<p/>
     * <pre>
    create table ORMMAP_ONEMANY_CHILD (
        ID varchar(255) not null,
        primary key (ID)
    );

    create table ORMMAP_ONEMANY_OWNPARENT (
        ID varchar(255) not null,
        primary key (ID)
    );

    create table ORMMAP_ONEMANY_OWNPARENT_ORMMAP_ONEMANY_CHILD (
        ORMMAP_ONEMANY_OWNPARENT_ID varchar(255) not null,
        ownedChildren_ID varchar(255) not null,
        mapkey varchar(255),
        primary key (ORMMAP_ONEMANY_OWNPARENT_ID, mapkey)
    );
     </pre><p/>
     * At runtime, we get the following data in the link table.<p/>
     * <pre>
ORMMAP_ONEMANY_OWNPARENT_ID OWNEDCHILDREN_ID MAPKEY
--------------------------- ---------------- ------
fred                        chip             chip
fred                        ernie            ernie
     </pre><p/>
     * 
     * When @MapKey(name="name") is added to the parent definition...<p/>
     * <pre>
    @ManyToMany
    @MapKey(name="name")
    public Map<String, OneManyChild> getOwnedChildren() {
        return ownedChildren;
    }
     </pre><p/>
     * The link table schema changes to the following...<p/>
     * <pre>
    create table ORMMAP_ONEMANY_OWNPARENT_ORMMAP_ONEMANY_CHILD (
        ORMMAP_ONEMANY_OWNPARENT_ID varchar(255) not null,
        ownedChildren_ID varchar(255) not null,
        primary key (ORMMAP_ONEMANY_OWNPARENT_ID, ownedChildren_ID)
    );
     </pre><p/>
     * At runtime, we get the following data in the link table.<p/>
     * <pre>
ORMMAP_ONEMANY_OWNPARENT_ID OWNEDCHILDREN_ID
--------------------------- ----------------
fred                        chip            
fred                        ernie           
     </pre><p/>
     *
     * By supplying a @JoinTable defintion, we can also provide control 
     * over the link table and column naming.<p/>
     * <pre>
    @ManyToMany
    @MapKey(name="name")
    @JoinTable(name="ORMMAP_OWNPARENT_INVCHILD_LINK",
            joinColumns=@JoinColumn(name="PARENT_ID"),
            inverseJoinColumns=@JoinColumn(name="CHILD_ID"))
    public Map<String, OneManyChild> getOwnedChildren() {
        return ownedChildren;
    }
     * </pre><p/>
     * 
     * The link table schema changes to the following...<p/>
     * <pre>
    create table ORMMAP_OWNPARENT_INVCHILD_LINK (
        PARENT_ID varchar(255) not null,
        CHILD_ID varchar(255) not null,
        primary key (PARENT_ID, CHILD_ID)
    );
     </pre><p/>
     */
    @Test
    public void testOneToManyOwningParentMap() {
            log.info("*** testOneToManyOwningParentMap ***");
            
            OneManyOwningParent oneManyOwningParent = 
                    new OneManyOwningParent("fred");
            
            OneManyChild oneManyChild1 = new OneManyChild("chip");
            oneManyOwningParent.getOwnedChildren().put(
                            oneManyChild1.getName(), oneManyChild1);
            
            OneManyChild oneManyChild2 = new OneManyChild("ernie");
            oneManyOwningParent.getOwnedChildren().put(
                            oneManyChild2.getName(), oneManyChild2);		
            
            em.persist(oneManyOwningParent);
            em.persist(oneManyChild1);
            em.persist(oneManyChild2);
		
            em.flush();
            em.getTransaction().commit();
            log.info("persisted neManyOwningParent=" + oneManyOwningParent);
            log.info("persisted oneManyChild1=" + oneManyChild1);
            log.info("persisted oneManyChild2=" + oneManyChild2);
            
            em.clear();

            OneManyOwningParent oneManyOwningParentA =
                    em.find(OneManyOwningParent.class,oneManyOwningParent.getName());
            OneManyChild oneManyChild1a = 
                    em.find(OneManyChild.class, oneManyChild1.getName());
            OneManyChild oneManyChild2a = 
                    em.find(OneManyChild.class, oneManyChild2.getName());
            
            log.info("found oneManyOwningParentA=" + oneManyOwningParentA);
            log.info("found oneManyChild1a=" + oneManyChild1a);
            log.info("found oneManyChild2a=" + oneManyChild2a);
	}
}
