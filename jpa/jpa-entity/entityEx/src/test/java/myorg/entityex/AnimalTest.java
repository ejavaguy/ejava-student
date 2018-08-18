package myorg.entityex;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import myorg.entityex.annotated.Address;
import myorg.entityex.annotated.Bear;
import myorg.entityex.annotated.Bear2;
import myorg.entityex.annotated.Bunny;
import myorg.entityex.annotated.Cow;
import myorg.entityex.annotated.Cow2;
import myorg.entityex.annotated.CowPK;
import myorg.entityex.annotated.Dog;
import myorg.entityex.annotated.Horse;
import myorg.entityex.annotated.Name;
import myorg.entityex.annotated.Shark;
import myorg.entityex.annotated.Street;
import myorg.entityex.mapped.Animal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AnimalTest {
    private static final Logger logger = LoggerFactory.getLogger(Auto.class);
    private static final String PERSISTENCE_UNIT = "entityEx-test";
    private static EntityManagerFactory emf;
    private EntityManager em;    

    @BeforeClass
    public static void setUpClass() {
        logger.debug("creating entity manager factory");
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }
    
    @Before
    public void setUp() throws Exception {
        logger.debug("creating entity manager");
        em = emf.createEntityManager();
        cleanup();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
            logger.debug("tearDown() started, em= {}",em);
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                em.getTransaction().commit();            
            } else if (!em.getTransaction().getRollbackOnly()) {
                em.getTransaction().commit();                        	
            } else {
            	em.getTransaction().rollback();
            }
            em.close();
            logger.debug("tearDown() complete, em= {}",em);
     }
    
    @AfterClass
    public static void tearDownClass() {
        logger.debug("closing entity manager factory");
        if (emf!=null) { emf.close(); }
    }
    
    public void cleanup() {
        em.getTransaction().begin();
        //delete what we need to here
        em.createQuery("delete from Animal").executeUpdate();
        em.createQuery("delete from Animal2").executeUpdate();
        em.createQuery("delete from Cow").executeUpdate();
        em.createQuery("delete from Cow2").executeUpdate();
        em.getTransaction().commit();
    }

    @Test
    public void testCreateAnimal() {
        logger.info("testCreateAnimal");
        logger.info("{}", em.createQuery("select a from Animal a").getResultList());
        
    	    Animal animal = new Animal("bessie", 
    			new GregorianCalendar(1960, 1, 1).getTime(), 1400.2);
        em.persist(animal);        
        
        assertNotNull("animal not found", em.find(Animal.class,animal.getId()));
        
        em.flush(); //make sure all writes were issued to DB
        em.clear(); //purge the local entity manager entity cache to cause new instance
        assertNotNull("animal not found", em.find(Animal.class,animal.getId()));
    }

    @Test
    public void testCreateAnimalAnnotated() {
        logger.info("testCreateAnimalAnnotated");
    	    myorg.entityex.annotated.Animal2 animal = new myorg.entityex.annotated.Animal2("bessie", 
    			new GregorianCalendar(1960, 1, 1).getTime(), 1400.2);
        em.persist(animal);        
        
        assertNotNull("animal not found", em.find(myorg.entityex.annotated.Animal2.class,animal.getId()));
        
        em.flush(); //make sure all writes were issued to DB
        em.clear(); //purge the local entity manager entity cache to cause new instance
        assertNotNull("animal not found", em.find(myorg.entityex.annotated.Animal2.class,animal.getId()));
    }
    
    @Test
    public void testCreateCatMapped() {
    	logger.info("testCreateCatMapped");
    	myorg.entityex.mapped.Cat cat = new myorg.entityex.mapped.Cat("fluffy", null, 99.9);
    	em.persist(cat);                                             //get provider to call getters
    	em.flush(); em.detach(cat);
    	cat = em.find(myorg.entityex.mapped.Cat.class, cat.getId()); //get provider to call setters
    }

    @Test
    public void testCreateCatAnnotated() {
    	logger.info("testCreateCatAnnotated");
    	myorg.entityex.annotated.Cat2 cat = new myorg.entityex.annotated.Cat2("fluffy", null, 99.9);
    	em.persist(cat);                                                 //get provider to call getters
    	em.flush(); em.detach(cat);
    	cat = em.find(myorg.entityex.annotated.Cat2.class, cat.getId()); //get provider to call setters
    }

    @Test
    public void testTemporal() {
    	logger.info("testTemporal");
    	Shark shark = new Shark()
    		.setDate(new GregorianCalendar(1776, Calendar.JULY, 4))
    		.setTime(new Date())
    		.setTimestamp(new Date());
    	em.persist(shark);
    	logger.info("initial object= {}",shark);
    	
    	//flush commands to DB and get new instance
    	em.flush(); em.detach(shark);
    	Shark shark2 = em.find(Shark.class, shark.getId());
    	logger.info("object from DB= {}",shark2);
    }
    
    @Test
    public void testEnums() {
    	logger.info("testEnums");
    	Dog dog = new Dog()
    		.setGender(Dog.Sex.FEMALE)
    		.setColor(Dog.Color.MIX)
    		.setBreed(Dog.Breed.SAINT_BERNARD);
    	em.persist(dog);
    	em.flush();
    	
    	//check the raw value stored in the database
    	Object[] o = (Object[])em.createNativeQuery("select GENDER, COLOR, BREED from ENTITYEX_DOG where id=?")
    	        .setParameter(1, dog.getId())
    			.getSingleResult();
    	logger.debug("cols= {}",Arrays.toString(o));
    	assertEquals("unexpected gender", Dog.Sex.FEMALE.ordinal(), ((Number)o[0]).intValue());
    	assertEquals("unexpected color", Dog.Color.MIX.name(), ((String)o[1]));
    	assertEquals("unexpected breed", Dog.Breed.SAINT_BERNARD.prettyName, ((String)o[2]));
    	
    	//get a new instance
    	em.detach(dog);
    	Dog dog2 = em.find(Dog.class, dog.getId());
    	assertEquals("unexpected dog gender", dog.getGender(), dog2.getGender());
    	assertEquals("unexpected dog color", dog.getColor(), dog2.getColor());
    	assertEquals("unexpected dog breed", dog.getBreed(), dog2.getBreed());
    }
    
    @Test
    public void testPKGen() {
    	logger.info("testPKGen");
    	Bunny bunny = new Bunny();
    	bunny.setName("fuzzy");
    	assertTrue("primary key unexpectedly assigned", bunny.getId()==0);
    	em.persist(bunny);
    	em.flush();
    	logger.info("bunny.getId()= {}",bunny.getId());
    	assertFalse("primary key not assigned", bunny.getId()==0);
    	
    	Set<Integer> ids = new HashSet<Integer>();
    	ids.add(bunny.getId());
    	for (String name: new String[]{"peter", "march hare", "pat"}) {
        	Bunny b = new Bunny();
        	b.setName(name);
        	em.persist(b);
            em.flush();
        	assertTrue("id not unique:" + b.getId(), ids.add(b.getId()));
    	}
    	logger.debug("ids= {}",ids);
    }
    
    @Test 
    public void testLob() {
    	logger.info("testLob");
    	//create our host object with Lob objects
    	Horse horse = new Horse();
    	horse.setName("Mr. Ed");
    	horse.setDescription("There once was a horse of course and his name was Mr. Ed...");
    	horse.setHistory("Mister Ed is a fictional talking horse residing in Mount Kisco, New York,...".toCharArray());
	    	byte[] picture = new byte[10*1000];
	    	new Random().nextBytes(picture);
    	horse.setPhoto(picture);
	    	Horse.Jockey jockey = new Horse.Jockey();
	    	jockey.setName("Wilbur Post");
    	horse.setJockey(jockey);
    	em.persist(horse);
    	
    	//flush to DB and get a new instance
    	em.flush(); em.detach(horse);
    	Horse horse2 = em.find(Horse.class, horse.getId());
    	assertEquals("unexpected description", horse.getDescription(), horse2.getDescription());
    	assertTrue("unexpected history", Arrays.equals(horse.getHistory(), horse2.getHistory()));
    	assertTrue("unexpected photo", Arrays.equals(horse.getPhoto(), horse2.getPhoto()));
    	assertEquals("unexpected jockey", horse.getJockey().getName(), horse2.getJockey().getName());
    }
    
    @Test
    public void testEmbeddedId() {
    	logger.info("testEmbedded");
    	Cow cow = new Cow(new CowPK("Ponderosa", "Bessie"));
    	cow.setWeight(900);
    	em.persist(cow);
    	
    	//flush to DB and get a new instance
    	em.flush(); em.detach(cow);
    	Cow cow2 = em.find(Cow.class, new CowPK("Ponderosa", "Bessie"));
    	assertNotNull("cow not found", cow2);
    	assertEquals("unexpected herd", cow.getPk().getHerd(), cow2.getPk().getHerd());
    	assertEquals("unexpected name", cow.getPk().getName(), cow2.getPk().getName());
    	assertEquals("unexpected weight", cow.getWeight(), cow2.getWeight());    	
    }
    
    @Test
    public void testIdClass() {
    	logger.info("testIdClass");
    	Cow2 cow = new Cow2("Ponderosa", "Bessie");
    	cow.setWeight(900);
    	em.persist(cow);
    	
    	//flush to DB and get a new instance
    	em.flush(); em.detach(cow);
    	Cow2 cow2 = em.find(Cow2.class, new CowPK("Ponderosa", "Bessie"));
    	assertNotNull("cow not found", cow2);
    	assertEquals("unexpected herd", cow.getHerd(), cow2.getHerd());
    	assertEquals("unexpected name", cow.getName(), cow2.getName());
    	assertEquals("unexpected weight", cow.getWeight(), cow2.getWeight());    	
    }
    
    @Test
    public void testEmbeddedObject() {
    	logger.info("testEmbeddedObject");
    	Bear bear = new Bear();
    	bear.setName(new Name().setFirstName("Yogi").setLastName("Bear"));
    	bear.setAddress(new Address()
    		.setCity("Jellystone Park")
    		.setState("???")
    	    .setStreet(new Street().setNumber(1).setName("Picnic")));
    	em.persist(bear);
    	
    	//flush to DB and get a new instance
    	em.flush(); em.detach(bear);
    	Bear bear2 = em.find(Bear.class, bear.getId());
    	assertEquals("unexpected firstName", bear.getName().getFirstName(), bear2.getName().getFirstName());
    	assertEquals("unexpected lastName", bear.getName().getLastName(), bear2.getName().getLastName());
    	assertEquals("unexpected street number", 
    			bear.getAddress().getStreet().getNumber(), bear2.getAddress().getStreet().getNumber());
    	assertEquals("unexpected street name", 
    			bear.getAddress().getStreet().getName(), bear2.getAddress().getStreet().getName());
    	assertEquals("unexpected city", 
    			bear.getAddress().getCity(), bear2.getAddress().getCity());
    	assertEquals("unexpected state", 
    			bear.getAddress().getState(), bear2.getAddress().getState());
    }
    
    @Test
    public void testMultiTableMapping() {
    	logger.info("testMultiTableMapping");
    	Bear2 bear = new Bear2()
    		.setFirstName("Yogi")
    		.setLastName("Bear")
    		.setStreetNumber(1)
    		.setStreetName("Picnic")
    		.setCity("Jellystone Park")
    		.setState("???");
    	em.persist(bear);
    	
    	//flush to DB and get a new instance
    	em.flush(); em.detach(bear);
    	Bear2 bear2 = em.find(Bear2.class, bear.getId());
    	assertEquals("unexpected firstName", bear.getFirstName(), bear2.getFirstName());
    	assertEquals("unexpected lastName", bear.getLastName(), bear2.getLastName());
    	assertEquals("unexpected street number", 
    			bear.getStreetNumber(), bear2.getStreetNumber());
    	assertEquals("unexpected street name", 
    			bear.getStreetName(), bear2.getStreetName());
    	assertEquals("unexpected city", 
    			bear.getCity(), bear2.getCity());
    	assertEquals("unexpected state", 
    			bear.getState(), bear2.getState());
    }
    
}
