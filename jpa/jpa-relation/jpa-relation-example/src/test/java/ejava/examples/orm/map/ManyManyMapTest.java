package ejava.examples.orm.map;

import java.util.List;

import org.junit.Test;

import ejava.examples.orm.map.annotated.ManyManyEntity;
import ejava.examples.orm.map.annotated.ManyManyInverseEntity;
import ejava.examples.orm.map.annotated.ManyManyOwningEntity;
import ejava.examples.orm.rel.DemoBase;


/**
 * This class demonstrates a Many-to-Many relationship represented as a Map
 * in the owning entity. There are two types of entities represented in 
 * these tests. In each case, the foreign key is placed in a link table.
 * <nl>
 * <li>ManyManyInverseEntity to/from OneManyOwningEntity - One end owns the
 * relationship and the other end is the inverse. The inverse side has a 
 * reference to the owning side, but the application code (DAO or business
 * logic) is responsible for maintaining the inverse side's reference.</li> 
 * 
 * <li>ManyManyOwningEntity to ManyManyEntity - since all foreign key 
 * information about the relationship is stored in a link/join table, this
 * is almost identical to the case described above except that the non-owning
 * side has no knowledge of the relationship. All this means is that the 
 * application code has no responsibility to update the inverse side.</li>
 * </nl><p/>
 */
public class ManyManyMapTest extends DemoBase {
    
    protected void precleanup() throws Exception { cleanup(); }
    protected void postcleanup() throws Exception { cleanup(); }

    /**
     * This method will cleanup the database of our ManyToMany entities. We
     * start with the owning entities, since they own the link/join tables
     * with the foreign keys. Once the associations have been deleted, all
     * inverse and oblivious entities can be safely removed.
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected void cleanup() throws Exception {
        try {
            List<ManyManyOwningEntity> owningEntities =
                em.createQuery("select entity from ManyManyOwningEntity entity")
                  .getResultList();
            log.debug("removing " + owningEntities.size() + " owningEntities");
            for (ManyManyOwningEntity owningEntity : owningEntities) {
                for (String key : 
                    owningEntity.getOwnedInverseEntities().keySet()) {
                    ManyManyInverseEntity inverseEntity =
                        owningEntity.getOwnedInverseEntities().get(key);
                    log.debug("removing inverse relationship:" + 
                            inverseEntity.getName() + " to " +
                            owningEntity.getName());
                    inverseEntity.getOwnedByEntities()
                                 .remove(owningEntity);
                    log.debug("removing owning relationship:" + 
                            owningEntity.getName() + " to " +
                            inverseEntity.getName());
                    owningEntity.getOwnedInverseEntities()
                                .remove(inverseEntity);
                }
                log.debug("removing:" + owningEntity);
                em.remove(owningEntity);
            }
            
            List<ManyManyInverseEntity> inverseEntities =
                em.createQuery("select entity from ManyManyInverseEntity entity")
                  .getResultList();
            log.debug("removing " + owningEntities.size() + " inverseEntities");
            for (ManyManyInverseEntity entity : inverseEntities) {
                log.debug("removing:" + entity);
                em.remove(entity);
            }

            List<ManyManyEntity> entities =
                em.createQuery("select entity from ManyManyEntity entity")
                  .getResultList();
            log.debug("removing " + entities.size() + " entities");
            for (ManyManyEntity entity : entities) {
                log.debug("removing:" + entity);
                em.remove(entity);
            }
        }
        catch (Exception ex) {
            
        }
    }

	
    /**
     * This method tests the case where one entity owns the many-to-many,
     * bi-directional relationship and the other side forms the inverse side. 
     * If we model the Java form of the relationship without defining a 
     * @MapKey, then the following schema gets created,<p/>
     * <pre>
    create table ORMMAP_MANYMANY_INVENTITY (
        name varchar(255) not null,
        primary key (name)
    );

    create table ORMMAP_MANYMANY_OWNENTITY (
        name varchar(255) not null,
        primary key (name)
    );

    create table ORMMAP_MANYMANY_OWNENTITY_ORMMAP_MANYMANY_INVENTITY (
        ownedByEntities_name varchar(255) not null,
        ownedInverseEntities_name varchar(255) not null,
        mapkey varchar(255),
        primary key (ownedByEntities_name, ownedInverseEntities_name)
    );
`	 </pre><p/>
     * But runtime reports an error when manipulating the inverse side
     * map.<p/>
     * 
     * When @MapKey(name="name") is added to the inverse definition...<p/>
     * <pre>
    @ManyToMany(mappedBy="ownedInverseEntities")
    @MapKey(name="name")
    public Map<String, ManyManyOwningEntity> getOwnedByEntities() {
        return ownedByEntities;
    }
     </pre><p/>
     * The schema stays the same, but the test code can works<p/>
     * <pre>
    create table ORMMAP_MANYMANY_INVENTITY (
        name varchar(255) not null,
        primary key (name)
    );

    create table ORMMAP_MANYMANY_OWNENTITY (
        name varchar(255) not null,
        primary key (name)
    );

    create table ORMMAP_MANYMANY_OWNENTITY_ORMMAP_MANYMANY_INVENTITY (
        ownedByEntities_name varchar(255) not null,
        ownedInverseEntities_name varchar(255) not null,
        primary key (ownedByEntities_name, ownedInverseEntities_name)
    );
    </pre><p/>
     * 
     * When @MapKey(name="name") is added to the owning definition...<p/>
     * <pre>
    @ManyToMany
    @MapKey(name="name")
    public Map<String, ManyManyEntity> getOwnedEntities() {
        return ownedEntities;
    }
     </pre><p/>
     * The schema gets updated to remove the mapKey column from the 
     * link table and we reference into both entity tables for the 
     * mapKeys<p/>
     * <pre>
    create table ORMMAP_MANYMANY_INVENTITY (
        name varchar(255) not null,
        primary key (name)
    );

    create table ORMMAP_MANYMANY_OWNENTITY (
        name varchar(255) not null,
        primary key (name)
    );

    create table ORMMAP_MANYMANY_OWNENTITY_ORMMAP_MANYMANY_INVENTITY (
        ownedByEntities_name varchar(255) not null,
        ownedInverseEntities_name varchar(255) not null,
        primary key (ownedByEntities_name, ownedInverseEntities_name)
    );
    </pre><p/>

	 */
    @Test
    public void testManyToManyInverseMap() {
        log.info("*** testManyToManyInverseMap ***");
        
        ManyManyInverseEntity manyManyInverseEntity1 = 
                new ManyManyInverseEntity("chandler");
        ManyManyInverseEntity manyManyInverseEntity2 = 
            new ManyManyInverseEntity("ross");
		
		ManyManyOwningEntity manyManyOwningEntity1 = 
            new ManyManyOwningEntity("rachel");
		manyManyOwningEntity1.getOwnedInverseEntities().put(
                manyManyInverseEntity1.getName(),
                manyManyInverseEntity1);
        manyManyInverseEntity1.getOwnedByEntities().put(
                manyManyOwningEntity1.getName(),
                manyManyOwningEntity1);
        manyManyOwningEntity1.getOwnedInverseEntities().put(
                manyManyInverseEntity2.getName(),
                manyManyInverseEntity2);
        manyManyInverseEntity2.getOwnedByEntities().put(
                manyManyOwningEntity1.getName(),
                manyManyOwningEntity1);
        
        
        ManyManyOwningEntity manyManyOwningEntity2 = 
            new ManyManyOwningEntity("phebe");
        manyManyOwningEntity2.getOwnedInverseEntities().put(
                manyManyInverseEntity1.getName(),
                manyManyInverseEntity1
                );
        manyManyInverseEntity1.getOwnedByEntities().put(
                manyManyOwningEntity2.getName(),
                manyManyOwningEntity2);
		
        em.persist(manyManyInverseEntity1);
        em.persist(manyManyInverseEntity2);
        em.persist(manyManyOwningEntity1);
        em.persist(manyManyOwningEntity2);		
		em.flush();
        
        em.getTransaction().commit();
        log.info("persisted manyManyInverseEntity1=" + manyManyInverseEntity1);
        log.info("persisted manyManyInverseEntity2=" + manyManyInverseEntity2);
        log.info("persisted manyManyOwningEntity1=" + manyManyOwningEntity1);
        log.info("persisted manyManyOwningEntity2=" + manyManyOwningEntity2);

        em.clear();

        ManyManyInverseEntity manyManyInverseEntity1a =
                em.find(ManyManyInverseEntity.class,
                    manyManyInverseEntity1.getName());
        ManyManyInverseEntity manyManyInverseEntity2a =
            em.find(ManyManyInverseEntity.class,
                    manyManyInverseEntity2.getName());
        ManyManyOwningEntity manyManyOwningEntity1a =
            em.find(ManyManyOwningEntity.class,
                    manyManyOwningEntity1.getName());
        ManyManyOwningEntity manyManyOwningEntity2a =
            em.find(ManyManyOwningEntity.class,
                    manyManyOwningEntity2.getName());
		
        log.info("found manyManyInverseEntity1=" + manyManyInverseEntity1a);
        log.info("found manyManyInverseEntity2=" + manyManyInverseEntity2a);
        log.info("found manyManyOwningEntity1=" + manyManyOwningEntity1a);
        log.info("found manyManyOwningEntity2=" + manyManyOwningEntity2a);
    }
    
    /**
     * This method tests the a simpler version of the owning/inverse
     * case. This case only has an owning side. The final schema ends up
     * looking like the following...  
     * <pre>
    create table ORMMAP_MANYMANY_ENTITY (
        name varchar(255) not null,
        primary key (name)
    );

    create table ORMMAP_MANYMANY_OWNENTITY (
        name varchar(255) not null,
        primary key (name)
    );

    create table ORMMAP_MANYMANY_OWNENTITY_ORMMAP_MANYMANY_ENTITY (
        ORMMAP_MANYMANY_OWNENTITY_name varchar(255) not null,
        ownedEntities_name varchar(255) not null,
        primary key (ORMMAP_MANYMANY_OWNENTITY_name, ownedEntities_name)
    );
     </pre><p/>
     */
	@Test
	public void XtestManyToManyMap() {
        log.info("*** testManyToManyMap ***");
        
        ManyManyEntity manyManyEntity1 = 
            new ManyManyEntity("wojo");
        ManyManyEntity manyManyEntity2 = 
            new ManyManyEntity("detrick");
        
        ManyManyOwningEntity manyManyOwningEntity1 = 
            new ManyManyOwningEntity("barney");
        manyManyOwningEntity1.getOwnedEntities().put(
                manyManyEntity1.getName(),
                manyManyEntity1);
        manyManyOwningEntity1.getOwnedEntities().put(
                manyManyEntity2.getName(),
                manyManyEntity2);
        
        
        ManyManyOwningEntity manyManyOwningEntity2 = 
            new ManyManyOwningEntity("luger");
        manyManyOwningEntity2.getOwnedEntities().put(
                manyManyEntity1.getName(),
                manyManyEntity1
                );
        
        em.persist(manyManyEntity1);
        em.persist(manyManyEntity2);
        em.persist(manyManyOwningEntity1);
        em.persist(manyManyOwningEntity2);      
        em.flush();
        
        em.getTransaction().commit();
        log.info("persisted manyManyInverseEntity1=" + manyManyEntity1);
        log.info("persisted manyManyInverseEntity2=" + manyManyEntity2);
        log.info("persisted manyManyOwningEntity1=" + manyManyOwningEntity1);
        log.info("persisted manyManyOwningEntity2=" + manyManyOwningEntity2);

        em.clear();

        ManyManyEntity manyManyEntity1a =
            em.find(ManyManyEntity.class,
                    manyManyEntity1.getName());
        ManyManyEntity manyManyEntity2a =
            em.find(ManyManyEntity.class,
                    manyManyEntity2.getName());
        ManyManyOwningEntity manyManyOwningEntity1a =
            em.find(ManyManyOwningEntity.class,
                    manyManyOwningEntity1.getName());
        ManyManyOwningEntity manyManyOwningEntity2a =
            em.find(ManyManyOwningEntity.class,
                    manyManyOwningEntity2.getName());
        
        log.info("found manyManyEntity1=" + manyManyEntity1a);
        log.info("found manyManyEntity2=" + manyManyEntity2a);
        log.info("found manyManyOwningEntity1=" + manyManyOwningEntity1a);
        log.info("found manyManyOwningEntity2=" + manyManyOwningEntity2a);
    }
}
