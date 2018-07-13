package ejava.jpa.examples.query;

import static org.junit.Assert.*;


import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

/**
 * This test case provides an example of each of the different query types.
 */
public class QueryTypesTest extends QueryBase {
	private static final Logger log = LoggerFactory.getLogger(QueryTypesTest.class);

	@Test
	public void exampleJPAQL() {
		log.info("*** exampleJPAQL");
		
		String jpaqlString =
			//c is part of the root query and 
			//represents rows from the Customer entity table(s)  
			"select c from Customer c " +
			//c.lastName is a path off the root term that expressed
			//property is evaluated against a parameter
			"where c.firstName = :firstName " +
			//we order by property located through a path based from root
			"order by c.lastName ASC"; 
		
		//use query string to build query
		TypedQuery<Customer> query = em
				.createQuery(jpaqlString,Customer.class);

		//at this point we are query-type agnostic
		List<Customer> customers = query
				.setParameter("firstName", "thing")
				.getResultList();
		log.info("result=" + customers);
		assertEquals("unexpected number of results", 2, customers.size());
	}

	@Test
	public void exampleJPAQLOptimized() {
		log.info("*** exampleJPAQLOptimized");
		
		List<Customer> customers = em.createQuery(
			"select c from Customer c " +
			"where c.firstName = :firstName " +
			"order by c.lastName ASC",
			Customer.class)
				.setParameter("firstName", "thing")
				.getResultList();
		log.info("result=" + customers);
		assertEquals("unexpected number of results", 2, customers.size());
	}
	
	@Test
	public void exampleSQL() {
		log.info("*** exampleSQL ***");
		
		Table table = Customer.class.getAnnotation(Table.class);

		String sqlString =
			//return several columns from the table represented by c
			"select c.CUSTOMER_ID, c.FIRST_NAME, c.LAST_NAME " +						
			//base the query on the entity class table
			String.format("from %s c ", table.name()) +
			"where c.FIRST_NAME = ? " +
			//we order by column in entity class table
			"order by c.LAST_NAME ASC";
			
		//use query string to build query
		Query query = em.createNativeQuery(sqlString,Customer.class);

		//at this point we are query-type agnostic (mostly)
		//native queries do not have an option for TypedQuery
		@SuppressWarnings("unchecked")
		List<Customer> customers = query
				//name substitution is specified as non-portable in JPA spec
				.setParameter(1, "thing")
				.getResultList();
		log.info("result=" + customers);
		assertEquals("unexpected number of results", 2, customers.size());
	}
	
	@Test
	public void exampleSQLOptimized() {
		log.info("*** exampleSQLOptimized ***");
		Table table = Customer.class.getAnnotation(Table.class);
		
		@SuppressWarnings("unchecked")
		List<Customer> customers = em.createNativeQuery(
			"select c.CUSTOMER_ID, c.FIRST_NAME, c.LAST_NAME " +						
			String.format("from %s c ", table.name()) +
			"where c.FIRST_NAME = ? " +
			"order by c.LAST_NAME ASC",
			Customer.class)
				.setParameter(1, "thing")
				.getResultList();
		log.info("result=" + customers);
		assertEquals("unexpected number of results", 2, customers.size());
	}
	
	/**
	 * This test shows more features of the SqlResultSet -- where we mix entity
	 * and value return values.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void exampleSQLResultSet() {
		List<Object[]> results = em.createNativeQuery(
				"select clerk.CLERK_ID, "
				+ "clerk.FIRST_NAME, "
				+ "clerk.LAST_NAME, "
				+ "clerk.HIRE_DATE, "
				+ "clerk.TERM_DATE, "
				+ "sum(sales.amount) total_sales " 
				+ "from JPAQL_CLERK clerk "
				+ "left outer join JPAQL_SALE_CLERK_LINK slink on clerk.CLERK_ID=slink.CLERK_ID "
				+ "left outer join JPAQL_SALE sales on sales.SALE_ID=slink.SALE_ID "
				+ "group by clerk.CLERK_ID, "
				+ "clerk.FIRST_NAME, "
				+ "clerk.LAST_NAME, "
				+ "clerk.HIRE_DATE, "
				+ "clerk.TERM_DATE "
				+ "order by total_sales DESC",
				"Clerk.clerkSalesResult")
				.getResultList();
		for (Object[] result: results) {
			Clerk clerk = (Clerk) result[0];
			BigDecimal totalSales = (BigDecimal) result[1];
			log.info(String.format("%s, $ %s", clerk.getFirstName(), totalSales));
		}
		
		results = em.createNamedQuery("Clerk.clerkSales").getResultList();
		for (Object[] result: results) {
			Clerk clerk = (Clerk) result[0];
			BigDecimal totalSales = (BigDecimal) result[1];
			log.info(String.format("%s, $ %s", clerk.getFirstName(), totalSales));
		}
		log.info("");
	}
	
	@Test 
	public void exampleCriteriaStringAPI() {
		log.info("*** exampleCriteriaStringAPI ***");

		CriteriaBuilder cb = em.getCriteriaBuilder();
		//definition that will hold query expression
		CriteriaQuery<Customer> qdef = cb.createQuery(Customer.class);
		//c is part of the root query and 
		//represents rows from the Customer entity table(s)
		Root<Customer> c = qdef.from(Customer.class);
		//we hold onto the returned reference to c here so we can base
		//path references from it later
		qdef.select(c) //we are returning a single root object
		        //c.lastName obtained thru a path expressing using builder
		    .where(cb.equal(c.get("firstName"), 
		    	//property is evaluated against a parameter
		    		        cb.parameter(String.class,"firstName")))
		    	//we order by property located through a path based from root
		    .orderBy(cb.asc(c.get("lastName")));
		//use query string to build query
		TypedQuery<Customer> query = em.createQuery(qdef);

		//at this point we are query-type agnostic
		List<Customer> customers = query
				.setParameter("firstName", "thing")
				.getResultList();
		log.info("result=" + customers);
		assertEquals("unexpected number of results", 2, customers.size());
	}

	@Test 
	public void exampleCriteriaStringAPIOptimized() {
		log.info("*** exampleCriteriaStringAPIOptimized ***");

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Customer> qdef = cb.createQuery(Customer.class);
		Root<Customer> c = qdef.from(Customer.class);

		List<Customer> customers = em.createQuery(qdef.select(c)
		    .where(cb.equal(c.get("firstName"), "thing"))
		    .orderBy(cb.asc(c.get("lastName"))))
			.getResultList();

		log.info("result=" + customers);
		assertEquals("unexpected number of results", 2, customers.size());
	}


	@Test 
	public void exampleCriteriaMetamodelAPI() {
		log.info("*** exampleCriteriaMetamodelAPI ***");

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Customer> qdef = cb.createQuery(Customer.class);
		Root<Customer> c = qdef.from(Customer.class);
		//obtain reference to underlying JPA metamodel for this entity
		EntityType<Customer> c_ = c.getModel();
		
		log.info(String.format("%7s, %10s:%-30s", 
				c_.getPersistenceType(), 
				c_.getName(), 
				c_.getJavaType()));
		for (Attribute<? super Customer, ?> p: c_.getAttributes()) {
			log.info(String.format("%7s, %10s:%-30s", 
					p.getPersistentAttributeType(), 
					p.getName(), 
					p.getJavaType()));			
		}
		
		qdef.select(c) //we are returning a single root object
		    .where(cb.equal(
		    		c.get(c_.getSingularAttribute("firstName", String.class)), 
		    		cb.parameter(String.class,"firstName")))
		    .orderBy(cb.asc(c.get(c_.getSingularAttribute("lastName", String.class))));
		TypedQuery<Customer> query = em.createQuery(qdef);

		
		//at this point we are query-type agnostic
		List<Customer> customers = query
				.setParameter("firstName", "thing")
				.getResultList();
		log.info("result=" + customers);
		assertEquals("unexpected number of results", 2, customers.size());
	}

	@Test 
	public void exampleCriteriaMetamodelAPIOptimized() {
		log.info("*** exampleCriteriaMetamodelAPIOptimized ***");

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Customer> qdef = cb.createQuery(Customer.class);
		Root<Customer> c = qdef.from(Customer.class);
		EntityType<Customer> c_ = c.getModel();
		List<Customer> customers = em.createQuery(qdef.select(c)
		    .where(cb.equal(
		    		c.get(c_.getSingularAttribute("firstName", String.class)), "thing"))
		    .orderBy(cb.asc(c.get(c_.getSingularAttribute("lastName", String.class)))))
			.getResultList();

		log.info("result=" + customers);
		assertEquals("unexpected number of results", 2, customers.size());
	}

	@Test 
	public void exampleCriteriaCanonicalMetamodelAPI() {
		log.info("*** exampleCriteriaCanonicalMetamodelAPI ***");

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Customer> qdef = cb.createQuery(Customer.class);
		Root<Customer> c = qdef.from(Customer.class);
		
		qdef.select(c) //we are returning a single root object
		    .where(cb.equal(
		    		//use canonical meta-model to make easier, type-safe references
		    		c.get(Customer_.firstName), 
		    		cb.parameter(String.class,"firstName")))
		    .orderBy(cb.asc(c.get(Customer_.lastName)));
		TypedQuery<Customer> query = em.createQuery(qdef);

		
		//at this point we are query-type agnostic
		List<Customer> customers = query
				.setParameter("firstName", "thing")
				.getResultList();
		log.info("result=" + customers);
		assertEquals("unexpected number of results", 2, customers.size());
	}

	@Test 
	public void exampleCriteriaCanonicalMetamodelAPIOptimized() {
		log.info("*** exampleCriteriaCanonicalMetamodelAPIOptimized ***");

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Customer> qdef = cb.createQuery(Customer.class);
		Root<Customer> c = qdef.from(Customer.class);
		
		List<Customer> customers = em.createQuery(qdef.select(c)
		    .where(cb.equal(c.get(Customer_.firstName),"thing"))
		    .orderBy(cb.asc(c.get(Customer_.lastName))))
		    .getResultList();
		log.info("result=" + customers);
		assertEquals("unexpected number of results", 2, customers.size());
	}
}
