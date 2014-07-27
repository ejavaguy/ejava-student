package ejava.jpa.examples.query.criteria;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.Trimspec;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LazyInitializationException;
import org.junit.Test;

import ejava.jpa.examples.query.Clerk;
import ejava.jpa.examples.query.Customer;
import ejava.jpa.examples.query.QueryBase;
import ejava.jpa.examples.query.Receipt;
import ejava.jpa.examples.query.Sale;
import ejava.jpa.examples.query.Store;

public class CriteriaTest extends QueryBase {
	private static final Log log = LogFactory.getLog(CriteriaTest.class);

	private <T> List<T> executeQuery(CriteriaQuery<T> qdef) {
        return executeQuery(qdef, null);
    }

    private <T> List<T> executeQuery(CriteriaQuery<T> qdef, 
            Map<String, Object> params) {
        TypedQuery<T> query = em.createQuery(qdef);
        if (params != null && !params.isEmpty()) {
            for(String key: params.keySet()) {
                query.setParameter(key, params.get(key));
            }
        }
        List<T> objects = query.getResultList();
        for(T o: objects) {
           log.info("found result:" + o);
        }
        return objects;
    }
	
    /**
     * This test demonstrates a single entity query.
     */
	@Test
    public void testSimpleSelect() {
        log.info("*** testSimpleSelect() ***");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> qdef = cb.createQuery(Customer.class);
        
        //"select object(c) from Customer as c"
        Root<Customer> c = qdef.from(Customer.class);        
        qdef.select(c);
        
        TypedQuery<Customer> query = em.createQuery(qdef);
        List<Customer> results = query.getResultList();
        for (Customer result : results) {
        	log.info("found=" + result);
        }
        int rows = results.size();
        assertTrue("unexpected number of customers:" + rows, rows > 0);
    }
	
    /**
     * This test demonstrates querying for a non-entity. The property queried
     * for is located off a path from the root query term.
     */
    @Test
    public void testNonEntityQuery() {
        log.info("*** testNonEntityQuery() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> qdef = cb.createQuery(String.class);
        
        //select c.lastName from Customer c
        Root<Customer> c = qdef.from(Customer.class);
        qdef.select(c.<String>get("lastName"));
        
        TypedQuery<String> query = em.createQuery(qdef);
        List<String> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(String result : results) {
            log.info("lastName=" + result);
        }
    }
	
    /**
     * This test demonstrates a query for multiple properties. In this
     * version we will use a generic Object[] for the return type.
     */
    @Test
    public void testMultiSelectObjectArray() {
        log.info("*** testMultiSelectObjectArray() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> qdef = cb.createQuery(Object[].class);
        
        //select c.firstName, c.hireDate from Clerk c
        Root<Clerk> c = qdef.from(Clerk.class);
        qdef.select(cb.array(c.get("firstName"), c.get("hireDate")));
        
        TypedQuery<Object[]> query = em.createQuery(qdef);
        List<Object[]> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Object[] result : results) {
            assertEquals("unexpected result length", 2, result.length);
            String firstName = (String) result[0];
            Date hireDate = (Date) result[1];
            log.info("firstName=" + firstName + " hireDate=" + hireDate);
        }
    }

    /**
     * This query demonstrates a query for multiple properties -- same as above
     * -- except this example used a Tuple return type and select aliases 
     */
    @Test
    public void testMultiSelectTuple() {
        log.info("*** testMultiSelectTuple() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> qdef = cb.createTupleQuery();
        
        //select c.firstName as firstName, c.hireDate as hireDate from Clerk c
        Root<Clerk> c = qdef.from(Clerk.class);
        qdef.select(cb.tuple(
        		c.get("firstName").alias("firstName"), 
        		c.get("hireDate").alias("hireDate")));

        TypedQuery<Tuple> query = em.createQuery(qdef);
        List<Tuple> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Tuple result : results) {
            assertEquals("unexpected result length", 2, result.getElements().size());
            String firstName = result.get("firstName", String.class);
            Date hireDate = result.get("hireDate", Date.class);
            log.info("firstName=" + firstName + " hireDate=" + hireDate);
        }
    }
    
    /**
     * This test provides another demonstration of selecting multiple properties --
     * with this example using a constructor expression to return a typed 
     * object for each result in the query.
     */
    @Test
    public void testMultiSelectConstructor() {
        log.info("*** testMultiSelectConstructor() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Receipt> qdef = cb.createQuery(Receipt.class);
        
        //select new ejava.jpa.examples.query.Receipt(s.id,s.buyerId,s.date, s.amount)
        //from Sale s
        Root<Sale> s = qdef.from(Sale.class);
        qdef.select(cb.construct(
		        		Receipt.class, 
		        		s.get("id"), 
		        		s.get("buyerId"),
		        		s.get("date"),
		        		s.get("amount")));
        
        TypedQuery<Receipt> query = em.createQuery(qdef);
        List<Receipt> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Receipt receipt : results) {
            assertNotNull("no receipt", receipt);
            log.info("receipt=" + receipt);
        }        
    }

    /**
     * This test provides an example of navigating a path formed by a 
     * relationship. In this case the path used is a single element.
     */
    @Test
    public void testPathExpressions() {
        log.info("*** testPathExpressions() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> qdef = cb.createQuery(Object[].class);
        
        //select s.id, s.store.name from Sale s
        Root<Sale> s = qdef.from(Sale.class);
        qdef.select(cb.array(s.get("id"),
        		             s.get("store").get("name")));
        
        TypedQuery<Object[]> query = em.createQuery(qdef);
        List<Object[]> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Object[] result : results) {
            assertEquals("unexpected result length", 2, result.length);
            Long id = (Long) result[0];
            String name = (String) result[1];
            log.info("sale.id=" + id + ", sale.store.name=" + name);
        }
    }

    /**
     * This test provides an example collection path using an INNER JOIN
     */
    @Test
    public void testCollectionPathExpressionsInnerJoin() {
        log.info("*** testCollectionPathExpressionsInnerJoin ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Date> qdef = cb.createQuery(Date.class);
        
        //select sale.date from Clerk c JOIN c.sales sale
        Root<Clerk> c = qdef.from(Clerk.class);
        Join<Clerk, Sale> sale = c.join("sales", JoinType.INNER);
        qdef.select(sale.<Date>get("date"));

        int rows=executeQuery(qdef).size();
        assertTrue("unexpected number of sales:" + rows, rows > 0);
    }

    /**
     * This test provides an example collection path using an LEFT OUTER JOIN
     */
    @Test
    public void testOuterJoin() {
        log.info("*** testOuterJoin() ***");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> qdef = cb.createQuery(Object[].class);
        
        //select c.id, c.firstName, sale.amount 
        //from Clerk c 
        //LEFT JOIN c.sales sale
        Root<Clerk> c = qdef.from(Clerk.class);
        Join<Clerk, Sale> sale = c.join("sales", JoinType.LEFT);
        qdef.select(cb.array(c.get("id"),
        		             c.get("firstName"),
        		             sale.get("amount")));
        
        TypedQuery<Object[]> query = em.createQuery(qdef);
        List<Object[]> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Object[] result : results) {
            assertEquals("unexpected result length", 3, result.length);
            Long id = (Long) result[0];
            String name = (String) result[1];
            BigDecimal amount = (BigDecimal) result[2];
            log.info("clerk.id=" + id + ", clerk.firstName=" + name +
                    ", amount=" + amount);
        }
    }
    
    /**
     * This test demonstrates creating an explicit JOIN based on adhoc criteria
     */
    @Test
    public void testExplicitJoin() {
    	log.info("*** testExplicitJoin ***");
    	
    	CriteriaBuilder cb = em.getCriteriaBuilder();
    	CriteriaQuery<Customer> qdef = cb.createQuery(Customer.class);
    	
    	//select c from Sale s, Customer c 
    	//where c.id = s.buyerId
    	Root<Sale> s = qdef.from(Sale.class);
    	Root<Customer> c = qdef.from(Customer.class);
    	qdef.select(c)
    	    .where(cb.equal(c.get("id"), s.get("buyerId")));
    	
    	int rows = executeQuery(qdef).size();
        assertTrue("unexpected number of customers:" + rows, rows > 0);
    }
    
    /**
     * This test demonstrates the function of a JOIN FETCH to perform the 
     * EAGER retrieval of entities as a side-effect of the query
     */
    @Test
    public void testFetchJoin1() {        
        log.info("** testFetchJoin1() ***");
        
        EntityManager em2 = createEm();
        CriteriaBuilder cb = em2.getCriteriaBuilder();
        CriteriaQuery<Store> qdef = cb.createQuery(Store.class);
        
        //select s from Store s JOIN s.sales
        //where s.name='Big Al''s'
        Root<Store> s = qdef.from(Store.class);
        s.join("sales");
        qdef.select(s)
            .where(cb.equal(s.get("name"), "Big Al's"));
        
        Store store = em2.createQuery(qdef).getSingleResult();
        log.info("em.contains(" + em2.contains(store) + ")");
        em2.close();
        try {
        	store.getSales().get(0).getAmount();
        	fail("did not trigger lazy initialization exception");
        } catch (LazyInitializationException expected) {
        	log.info("caught expected exception:" + expected);
        }
    }
    @Test
    public void testFetchJoin2() {        
        log.info("** testFetchJoin2() ***");

        EntityManager em2 = createEm();
        CriteriaBuilder cb = em2.getCriteriaBuilder();
        CriteriaQuery<Store> qdef = cb.createQuery(Store.class);

        //select s from Store s JOIN FETCH s.sales
        //where s.name='Big Al''s'
        Root<Store> s = qdef.from(Store.class);
        s.fetch("sales");
        qdef.select(s)
            .where(cb.equal(s.get("name"), "Big Al's"));
        
        Store store = em2.createQuery(qdef).getSingleResult();
        log.info("em.contains(" + em2.contains(store) + ")");
        em2.close();
       	store.getSales().get(0).getAmount();
    }
    
    /**
     * This test demonstrates the use of DISTINCT to limit the results
     * to only unique values
     */
    @Test
    public void testDISTINCT() {
        log.info("*** testDISTINCT() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        {
	        CriteriaQuery<String> qdef = cb.createQuery(String.class);
	        
	        //select DISTINCT c.lastName from Customer c
	        Root<Customer> c = qdef.from(Customer.class);
	        qdef.select(c.<String>get("lastName"))
	            .distinct(true);
	        
	        int rows = executeQuery(qdef).size();
	        assertEquals("unexpected number of rows", 3, rows);
        }
        
        {        
        	CriteriaQuery<String> qdef = cb.createQuery(String.class);
        	
        	//select DISTINCT c.firstName from Customer c
        	Root<Customer> c = qdef.from(Customer.class);
        	qdef.select(c.<String>get("firstName"))
        		.distinct(true);
        
	        int rows = executeQuery(qdef).size();
	        assertEquals("unexpected number of rows for DISTINCT", 2, rows);
        }
    }
    
    //where clauses
    
    
    /**
     * This test provides an example of an equality test in the where clause
     */
    @Test
    public void testLiteral() {
        log.info("*** testLiteral() ***");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> qdef = cb.createQuery(Customer.class);
        
        //select c from Customer c 
        //where c.firstName='cat'
        Root<Customer> c = qdef.from(Customer.class);
        qdef.select(c)
            .where(cb.equal(c.get("firstName"), "cat"));
        
        int rows = executeQuery(qdef).size();
        assertEquals("unexpected number of rows:", 1, rows);
    }

    /**
     * This test demonstrates how literal values are automatically escaped
     */
    @Test
    public void testSpecialCharacter() {
        log.info("*** testSpecialCharacter() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Store> qdef = cb.createQuery(Store.class);
        
        //select s from Store s 
        //where s.name='Big Al''s'
        Root<Store> s = qdef.from(Store.class);
        qdef.select(s)
            .where(cb.equal(s.get("name"), "Big Al's"));
        
        int rows = executeQuery(qdef).size();
        assertEquals("unexpected number of rows", 1, rows);
    }
    
    /**
     * This test demonstrates the use of like in where clauses
     */
    @Test
    public void testLike() {
        log.info("*** testLike() ***");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        {
        	CriteriaQuery<Clerk> qdef = cb.createQuery(Clerk.class);
        	
        	//select c from Clerk c 
            //where c.firstName like 'M%'
        	Root<Clerk> c = qdef.from(Clerk.class);
        	qdef.select(c)
        	    .where(cb.like(c.<String>get("firstName"), "M%"));

        	int rows = executeQuery(qdef).size();
        	assertEquals("unexpected number of rows", 2, rows);
        }
        
        {
        	CriteriaQuery<Clerk> qdef = cb.createQuery(Clerk.class);
        	
        	//select c from Clerk c
            //where c.firstName like :firstName
        	Root<Clerk> c = qdef.from(Clerk.class);
        	qdef.select(c)
        	    .where(cb.like(c.<String>get("firstName"), 
        	    		       cb.parameter(String.class, "firstName")));
        	TypedQuery<Clerk> query = em.createQuery(qdef)
        			.setParameter("firstName", "M%");
        	List<Clerk> results = query.getResultList();
            for(Object o: results) {
                log.info("found result:" + o);
             }
            assertEquals("unexpected number of rows", 2, results.size());        
        }
        
        {
        	CriteriaQuery<Clerk> qdef = cb.createQuery(Clerk.class);
        	
        	//select c from Clerk c
            //where c.firstName like concat(:firstName,'%')
        	Root<Clerk> c = qdef.from(Clerk.class);
        	qdef.select(c)
        	    .where(cb.like(c.<String>get("firstName"),
        	    		       cb.concat(cb.parameter(String.class, "firstName"), "%")));
        	TypedQuery<Clerk> query = em.createQuery(qdef)
        			.setParameter("firstName", "M");
        	List<Clerk> results = query.getResultList();
            for(Object o: results) {
                log.info("found result:" + o);
             }
            assertEquals("unexpected number of rows", 2, results.size());        
        }
        
        {
        	CriteriaQuery<Clerk> qdef = cb.createQuery(Clerk.class);
        	
        	//select c from Clerk c
            //where c.firstName like '_anny'
        	Root<Clerk> c = qdef.from(Clerk.class);
        	qdef.select(c)
        	    .where(cb.like(c.<String>get("firstName"),"_anny"));
        	TypedQuery<Clerk> query = em.createQuery(qdef);
        	List<Clerk> results = query.getResultList();
            for(Object o: results) {
                log.info("found result:" + o);
             }
            assertEquals("unexpected number of rows", 1, results.size());        
        }
        
    }
    
    /**
     * This test provides a demonstration of using a math formual within the 
     * where clause.
     */
    @Test
    public void testFormulas() {
        log.info("*** testFormulas() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Number> qdef = cb.createQuery(Number.class); 
        
        //select count(s) from Sale s 
        //where (s.amount * :tax) > :amount"
        Root<Sale> s = qdef.from(Sale.class);
        qdef.select(cb.count(s))
            .where(cb.greaterThan(
            	cb.prod(s.<BigDecimal>get("amount"), cb.parameter(BigDecimal.class, "tax")), 
            	new BigDecimal(10.0)));
        TypedQuery<Number> query = em.createQuery(qdef);
                
        //keep raising taxes until somebody pays $10.00 in tax
        double tax = 0.05;
        for (;query.setParameter("tax", new BigDecimal(tax))
        		   .getSingleResult().intValue()==0;
        	  tax += 0.01) {
        	log.debug("tax=" + NumberFormat.getPercentInstance().format(tax));
        }
        log.info("raise taxes to: " + NumberFormat.getPercentInstance().format(tax));
        
        assertEquals("unexpected level for tax:" + tax, 0.07, tax, .01);
    }
    
    /**
     * This test provides a demonstration of using logical AND, OR, and NOT
     * within a query where clause
     */
    @Test
    public void testLogical() {
        log.info("*** testLogical() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        {
        	CriteriaQuery<Customer> qdef = cb.createQuery(Customer.class);
        	
        	//select c from Customer c 
            //where (c.firstName='cat' AND c.lastName='inhat')
            //  OR c.firstName='thing'
        	Root<Customer> c = qdef.from(Customer.class);
        	qdef.select(c)
        	    .where(cb.or(
        	    		cb.and(cb.equal(c.get("firstName"), "cat"), 
        	    			   cb.equal(c.get("lastName"), "inhat")),
        	    		cb.equal(c.get("firstName"), "thing")));
        	
        	int rows=executeQuery(qdef).size();
            assertEquals("unexpected number of rows", 3, rows);        
        }

        {
        	CriteriaQuery<Customer> qdef = cb.createQuery(Customer.class);
        	
            //select c from Customer c
            //where (NOT (c.firstName='cat' AND c.lastName='inhat'))
            //  OR c.firstName='thing'
        	Root<Customer> c = qdef.from(Customer.class);
        	qdef.select(c)
        	    .where(cb.or(
	        	    	cb.not(cb.and(cb.equal(c.get("firstName"), "cat"), 
	        	    		          cb.equal(c.get("lastName"), "inhat"))),
        	    		cb.equal(c.get("firstName"), "thing"))
        	    	);
        	
        	int rows=executeQuery(qdef).size();
            assertEquals("unexpected number of rows", 2, rows);        
        }
    }

    /**
     * This test provides a demonstration for comparing two entities within
     * a query
     */
    @Test
    public void testEquality() {
        log.info("*** testEquality() ***");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
       	CriteriaQuery<Clerk> qdef = cb.createQuery(Clerk.class);

       	//select c from Clerk c where c.firstName = 'Manny'", 
       	Root<Clerk> c = qdef.from(Clerk.class);
       	qdef.select(c)
       	    .where(cb.equal(c.get("firstName"), "Manny"));
       	Clerk clerk = em.createQuery(qdef).getSingleResult();

       	//find all sales that involve this clerk
       	
        //select s from Sale s
        //JOIN s.clerks c
        //where c = :clerk 
       	CriteriaQuery<Sale> qdef2 = cb.createQuery(Sale.class);
       	Root<Sale> s = qdef2.from(Sale.class);
       	Join<Sale, Clerk> c2 = s.join("clerks");
       	qdef2.select(s)
       	     .where(cb.equal(c2, clerk));
       	
        List<Sale> sales = em.createQuery(qdef2)
	            .getResultList();
        for (Sale result : sales) {
        	log.info("found=" + result);
        }
        assertEquals("unexpected number of rows", 2, sales.size());
    }
    
    /**
     * This test provides an example of using between condition
     */
    @Test
    public void testBetween() {
        log.info("*** testBetween() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        {
	        CriteriaQuery<Sale> qdef = cb.createQuery(Sale.class);
	        
	        //select s from Sale s
	        //where s.amount BETWEEN :low AND :high"
	        Root<Sale> s = qdef.from(Sale.class);
	        qdef.select(s)
	            .where(cb.between(s.<BigDecimal>get("amount"), 
		            		new BigDecimal(90.00), 
		            		new BigDecimal(110.00)));
	        List<Sale> sales = em.createQuery(qdef).getResultList();
	        for (Sale result : sales) {
	        	log.info("found=" + result);
	        }
	        assertEquals("unexpected number of rows", 1, sales.size());
        }

        {
	        CriteriaQuery<Sale> qdef = cb.createQuery(Sale.class);
	        
	        //select s from Sale s
	        //where s.amount NOT BETWEEN :low AND :high"
	        Root<Sale> s = qdef.from(Sale.class);
	        qdef.select(s)
	            .where(cb.not(cb.between(s.<BigDecimal>get("amount"), 
		            		new BigDecimal(90.00), 
		            		new BigDecimal(110.00))));
	        List<Sale> sales = em.createQuery(qdef).getResultList();
	        for (Sale result : sales) {
	        	log.info("found=" + result);
	        }
	        assertEquals("unexpected number of rows", 1, sales.size());
        }
    }
    
    /**
     * This test provides a demonstration of testing for a null value.
     */
    @Test
    public void testIsNull() {
        log.info("*** testIsNull() ***");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        {
            CriteriaQuery<Sale> qdef = cb.createQuery(Sale.class);
            
            //select s from Sale s 
            //where s.store IS NULL
            Root<Sale> s = qdef.from(Sale.class);
            qdef.select(s)
                .where(cb.isNull(s.get("store")));
            	//.where(cb.equal(s.get("store"), cb.nullLiteral(Store.class)));
                    
            List<Sale> sales = em.createQuery(qdef).getResultList();
            for (Sale result : sales) {
            	log.info("found=" + result);
            }
            assertEquals("unexpected number of rows", 0, sales.size());
        }
        {
            CriteriaQuery<Sale> qdef = cb.createQuery(Sale.class);
            
            //select s from Sale s 
            //where s.store IS NOT NULL
            Root<Sale> s = qdef.from(Sale.class);
            qdef.select(s)
                .where(cb.isNotNull(s.get("store")));
            	//.where(cb.not(cb.equal(s.get("store"), cb.nullLiteral(Store.class))));
                    
            List<Sale> sales = em.createQuery(qdef).getResultList();
            for (Sale result : sales) {
            	log.info("found=" + result);
            }
            assertEquals("unexpected number of rows", 2, sales.size());
        }
    }
    
    /**
     * This test provides an example of testing whether the collection
     * is empty
     */
    @Test
    public void testIsEmpty() {
        log.info("*** testIsEmpty() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        {
            CriteriaQuery<Clerk> qdef = cb.createQuery(Clerk.class);
            
            //select c from Clerk c
            //where c.sales IS EMPTY
            Root<Clerk> c = qdef.from(Clerk.class);
            qdef.select(c)
                .where(cb.isEmpty(c.<List<Sale>>get("sales")));
            
            int rows = executeQuery(qdef).size();
            assertEquals("unexpected number of rows", 1, rows);
        }

        {
            CriteriaQuery<Clerk> qdef = cb.createQuery(Clerk.class);
            
            //select c from Clerk c
            //where c.sales IS NOT EMPTY
            Root<Clerk> c = qdef.from(Clerk.class);
            qdef.select(c)
                .where(cb.isNotEmpty(c.<List<Sale>>get("sales")));
            
            int rows = executeQuery(qdef).size();
            assertEquals("unexpected number of rows", 2, rows);
        }
    }
    
    /**
     * This test provides a demonstration of testing membership in 
     * a collection.
     */
    @Test
    public void testMemberOf() {
        log.info("*** testMemberOf() ***");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Clerk> qdef = cb.createQuery(Clerk.class);
        
        //select c from Clerk c where c.firstName = 'Manny'
        Root<Clerk> c = qdef.from(Clerk.class);
        qdef.select(c)
            .where(cb.equal(c.get("firstName"), "Manny"));
        Clerk clerk = em.createQuery(qdef).getSingleResult();
        
        //find all sales that involve this clerk
        CriteriaQuery<Sale> qdef2 = cb.createQuery(Sale.class);
        //select s from Sale s
        //where :clerk MEMBER OF s.clerks",
        Root<Sale> s = qdef2.from(Sale.class);
        qdef2.select(s)
             .where(cb.isMember(clerk, s.<List<Clerk>>get("clerks")));
        List<Sale> sales = em.createQuery(qdef2).getResultList();
        
        for (Sale result : sales) {
        	log.info("found=" + result);
        }
        assertEquals("unexpected number of rows", 2, sales.size());
    }
    
    
    /**
     * This test provides a demonstration of using an explicit subquery
     */
    @Test
    public void testSubqueries() {
       log.info("*** testSubqueries() ***");   

       CriteriaBuilder cb = em.getCriteriaBuilder();
       CriteriaQuery<Customer> qdef = cb.createQuery(Customer.class);
       
       //select c from Customer c
 	   //where c.id IN
       //    (select s.buyerId from Sale s
       //     where s.amount > 100)
       
       		//form subquery
       Subquery<Long> sqdef = qdef.subquery(Long.class);
       Root<Sale> s = sqdef.from(Sale.class);
       sqdef.select(s.<Long>get("buyerId"))
            .where(cb.greaterThan(s.<BigDecimal>get("amount"), new BigDecimal(100)));

  		//form outer query
   	   Root<Customer> c = qdef.from(Customer.class);
	   qdef.select(c)
           .where(cb.in(c.get("id")).value(sqdef));

       int rows = executeQuery(qdef).size();
       assertEquals("unexpected number of rows", 1, rows);
    }
    
    /**
     * This test provides a demonstration for using the ALL subquery
     * result evaluation.
     */
    @Test
    public void testAll() {
        log.info("*** testAll() ***");  
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Clerk> qdef = cb.createQuery(Clerk.class);
        Root<Clerk> c = qdef.from(Clerk.class);
        qdef.select(c);
        
        //select c from Clerk c
        //where 125 < ALL " +
        //(select s.amount from c.sales s)",
        Subquery<BigDecimal> sqdef = qdef.subquery(BigDecimal.class);
        Root<Clerk> c1 = sqdef.from(Clerk.class);
        Join<Clerk,Sale> s = c1.join("sales");
        sqdef.select(s.<BigDecimal>get("amount"))
             .where(cb.equal(c, c1));

        Predicate p1 = cb.lessThan(
        		cb.literal(new BigDecimal(125)), 
        		cb.all(sqdef));

        qdef.where(p1);
        List<Clerk> results1 = executeQuery(qdef);
        assertEquals("unexpected number of rows", 2, results1.size());

        //select c from Clerk c
        //where 125 > ALL
        //(select s.amount from c.sales s)
        Predicate p2 = cb.greaterThan(
        		cb.literal(new BigDecimal(125)), 
        		cb.all(sqdef));
        
        qdef.where(p2);
        List<Clerk> results2 = executeQuery(qdef);
        assertEquals("unexpected number of rows", 1, results2.size());
    }
    
    
    /**
     * This test provides a demonstration for using the ANY subquery
     * result evaluation
     */
    @Test
    public void testAny() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Clerk> qdef = cb.createQuery(Clerk.class);
        Root<Clerk> c = qdef.from(Clerk.class);
        qdef.select(c);
        
        //select c from Clerk c
        //where 125 < ANY " +
        //(select s.amount from c.sales s)",
        Subquery<BigDecimal> sqdef = qdef.subquery(BigDecimal.class);
        Root<Clerk> c1 = sqdef.from(Clerk.class);
        Join<Clerk,Sale> s = c1.join("sales");
        sqdef.select(s.<BigDecimal>get("amount"))
             .where(cb.equal(c, c1));

        Predicate p1 = cb.lessThan(
        		cb.literal(new BigDecimal(125)), 
        		cb.any(sqdef));

        qdef.where(p1);
        List<Clerk> results1 = executeQuery(qdef);
        assertEquals("unexpected number of rows", 2, results1.size());

        //select c from Clerk c
        //where 125 > ANY
        //(select s.amount from c.sales s)
        Predicate p2 = cb.greaterThan(
        		cb.literal(new BigDecimal(125)), 
        		cb.any(sqdef));
        
        qdef.where(p2);
        List<Clerk> results2 = executeQuery(qdef);
        assertEquals("unexpected number of rows", 1, results2.size());
     }
    
    
    /**
     * This test method demonstrates several date functions
     */
    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testStringFunctions() {
        log.info("*** testStringFunctions() ***");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery qdef = cb.createQuery();
		Root<Customer> c = qdef.from(Customer.class);
        
        //select c from Customer c
        //where c.firstName='CAT'
        qdef.select(c)
            .where(cb.equal(c.get("firstName"),"CAT"));
        int rows = executeQuery(qdef).size();
        assertEquals("unexpected number of rows", 0, rows);

        //select c from Customer c
        //where c.firstName=LOWER('CAT')"
        qdef.select(c)
            .where(cb.equal(c.get("firstName"),cb.lower(cb.literal("CAT"))));
        rows = executeQuery(qdef).size();
        assertEquals("unexpected number of rows", 1, rows);
            
        //select UPPER(c.firstName) from Customer c
        //where c.firstName=LOWER('CAT')
        qdef.select(cb.upper(c.<String>get("firstName")))
            .where(cb.equal(c.get("firstName"),cb.lower(cb.literal("CAT"))));
        rows = executeQuery(qdef).size();
        assertEquals("unexpected number of rows", 1, rows);

        //TODO: determine why SQL generated without quotes here with H2Dialect
/*
Caused by: org.h2.jdbc.JdbcSQLException: Column "C" not found; SQL statement:
select trim(LEADING c from customer0_.FIRST_NAME) as col_0_0_ from JPAQL_CUSTOMER customer0_ 
where customer0_.FIRST_NAME=?  
*/
        
        //select TRIM(LEADING 'c' FROM c.firstName) from Customer c
        //where c.firstName='cat')
        qdef.select(cb.trim(Trimspec.LEADING, 'c', c.<String>get("firstName")))
            .where(cb.equal(c.get("firstName"),"cat"));
//        List<String> result = executeQuery(qdef);        
//        assertEquals("unexpected number of rows", 1, rows);
//        assertEquals("unexpected value", "at", result.get(0));
        
        //select c from Customer c
        //where CONCAT(CONCAT(c.firstName,' '),c.lastName) ='cat inhat')
        qdef.select(c)
        	.where(cb.equal(
        			cb.concat(
        					cb.concat(c.<String>get("firstName"), " "),
        					c.<String>get("lastName")),
        			"cat inhat"));
        rows = executeQuery(qdef).size();
        assertEquals("unexpected number of rows:" + rows, 1, rows);
        
        //select c from Customer c
        //where LENGTH(c.firstName) = 3
        qdef.select(c)
            .where(cb.equal(cb.length(c.<String>get("firstName")),3));
        rows = executeQuery(qdef).size();
        assertEquals("unexpected number of rows:" + rows, 1, rows);
        
        //select c from Customer c " +
        //where LOCATE('cat',c.firstName,2) > 0",
        qdef.select(c)
            .where(cb.greaterThan(cb.locate(c.<String>get("firstName"), "cat", 2),0));        
        rows = executeQuery(qdef).size();
        assertEquals("unexpected number of rows:" + rows, 0, rows);        

        //select c from Customer c
        //where LOCATE('at',c.firstName,2) > 1
        qdef.select(c)
            .where(cb.greaterThan(cb.locate(c.<String>get("firstName"), "at", 2),1));        
        rows = executeQuery(qdef).size();
        assertEquals("unexpected number of rows:" + rows, 1, rows);        

        //select SUBSTRING(c.firstName,2,2) from Customer c " +
        //where c.firstName = 'cat'",
        qdef.select(cb.substring(c.<String>get("firstName"),  2, 2))
            .where(cb.equal(c.get("firstName"), "cat"));
        List<String> result = executeQuery(qdef);        
        assertEquals("unexpected number of rows", 1, rows);
        assertEquals("unexpected value", "at", result.get(0));
        
        //select c from Customer c
        //where SUBSTRING(c.firstName,2,2) = 'at'
        qdef.select(c)
            .where(cb.equal(
            		cb.substring(c.<String>get("firstName"), 2, 2), 
            		"at"));
        rows = executeQuery(qdef).size();
        assertEquals("unexpected number of rows:" + rows, 1, rows);
    }
 
    /**
     * This test method demonstrates using date functions.
     */
    @Test
    public void testDates() {        
        log.info("*** testDates() ***");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Sale> qdef = cb.createQuery(Sale.class);
        Root<Sale> s = qdef.from(Sale.class);
        qdef.select(s);

        //select s from Sale s
        //where s.date < CURRENT_DATE
        qdef.where(cb.lessThan(s.<Date>get("date"), cb.currentDate()));        
        int rows = executeQuery(qdef).size();
        assertEquals("unexpected number of rows", 2, rows);
        
        //select s from Sale s
        //where s.date = CURRENT_DATE
        qdef.where(cb.equal(s.<Date>get("date"), cb.currentDate()));        
        rows = executeQuery(qdef).size();
        assertEquals("unexpected number of rows", 0, rows);

        //bulk query capability added to Criteria API in JPA 2.1
        
        //update Sale s
    	//set s.date = CURRENT_DATE
        CriteriaUpdate<Sale> qupdate = cb.createCriteriaUpdate(Sale.class);
        Root<Sale> s2 = qupdate.from(Sale.class);
        qupdate.set(s2.<Date>get("date"), cb.currentDate());
        rows = em.createQuery(qupdate).executeUpdate();
        assertEquals("unexpected number of rows", 2, rows);
        
        em.getTransaction().commit();
        em.clear(); //remove stale objects in cache
        
        //select s from Sale s
        //where s.date = CURRENT_DATE
        qdef.where(cb.equal(s.<Date>get("date"), cb.currentDate()));        
        rows = executeQuery(qdef).size();
        assertEquals("unexpected number of rows", 2, rows);
    }
    
    /**
     * This test method provides a demonstration of order by capability.
     */
    @Test
    public void testOrderBy() {
        log.info("*** testOrderBy() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Sale> qdef = cb.createQuery(Sale.class);
        Root<Sale> s = qdef.from(Sale.class);
        qdef.select(s);

        //select s from Sale s ORDER BY s.amount ASC
        qdef.orderBy(cb.asc(s.get("amount")));
        List<Sale> results = executeQuery(qdef); 
        assertEquals("unexpected number of rows", 2, results.size());
        assertEquals("unexpected first element", 
                100, 
                results.get(0).getAmount().intValue());
        assertEquals("unexpected first element", 
                150, 
                results.get(1).getAmount().intValue());

        
        //select s from Sale s ORDER BY s.amount DESC
        qdef.orderBy(cb.desc(s.get("amount")));
        results = executeQuery(qdef); 
        assertEquals("unexpected number of rows", 2, results.size());
        assertEquals("unexpected first element", 
                150, 
                results.get(0).getAmount().intValue());
        assertEquals("unexpected first element", 
                100, 
                results.get(1).getAmount().intValue());
    }
    
    /**
     * This test provides a demonstration of the COUNT aggregate function
     */    
    @Test
    public void testCount() {        
        log.info("*** testCount() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Number> qdef = cb.createQuery(Number.class);
        Root<Sale> s = qdef.from(Sale.class);
        
        //select COUNT(s) from Sale s
        qdef.select(cb.count(s));

        List<Number> results= executeQuery(qdef);
        assertEquals("unexpected number of rows", 1, results.size());
        assertEquals("unexpected result", 2, results.get(0).intValue());
    }
    
    /**
     * This test provides a demonstration of the MIN and MAX aggregate functions
     */
    @Test
    public void testMaxMin() {        
        log.info("*** testMaxMin() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Number> qdef = cb.createQuery(Number.class);
        Root<Sale> s = qdef.from(Sale.class);

        //select max(s.amount) from Sale s
        qdef.select(cb.max(s.<BigDecimal>get("amount")));
        List<Number> results= executeQuery(qdef);
        assertEquals("unexpected number of rows", 1, results.size());
        assertEquals("unexpected result", 150, results.get(0).intValue());
        
        //select min(s.amount) from Sale s
        qdef.select(cb.min(s.<BigDecimal>get("amount")));
        results= executeQuery(qdef);
        assertEquals("unexpected number of rows", 1, results.size());
        assertEquals("unexpected result", 100, results.get(0).intValue());
    }

    /**
     * This test provides a demonstration of the SUM and AVE aggregate functions
     */
    @Test
    public void testSumAve() {        
        log.info("*** testSumAve() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Number> qdef = cb.createQuery(Number.class);
        Root<Sale> s = qdef.from(Sale.class);

        //select sum(s.amount) from Sale s
        qdef.select(cb.sum(s.<BigDecimal>get("amount")));
        List<Number> results=executeQuery(qdef);
        assertEquals("unexpected number of rows", 1, results.size());
        assertEquals("unexpected result", 250, results.get(0).intValue());
        
        //select avg(s.amount) from Sale s
        qdef.select(cb.avg(s.<BigDecimal>get("amount")));
        results= executeQuery(qdef);
        assertEquals("unexpected number of rows", 1, results.size());
        assertEquals("unexpected result", 125, results.get(0).intValue());
    }
    
    /**
     * This test method provides an example of using group by 
     */
    @Test
    public void testGroupBy() {
        log.info("*** testGroupBy() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> qdef = cb.createQuery(Object[].class);
        Root<Clerk> c = qdef.from(Clerk.class);
        Join<Clerk,Sale> s = c.join("sales", JoinType.LEFT);
	        //select c, COUNT(s) from Clerk c
	        //LEFT JOIN c.sales s
	        //GROUP BY c
        qdef.select(cb.array(c, cb.count(s)))
            .groupBy(c);
        
        List<Object[]> results= em.createQuery(qdef)
                				  .getResultList();
        for (Object[] result : results) {
        	log.info("found=" + Arrays.toString(result));
        }
        assertEquals("unexpected number of rows", 3, results.size());
    }
    


    /**
     * This test provides an example usage of the HAVING aggregate query
     * function.
     */
    @Test
    public void testHaving() {
        log.info("*** testHaving() ***");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> qdef = cb.createQuery(Object[].class);
        Root<Clerk> c = qdef.from(Clerk.class);
        Join<Clerk,Sale> s = c.join("sales", JoinType.LEFT);
        
        //select c, COUNT(s) from Clerk c
        //LEFT JOIN c.sales s
        //GROUP BY c " +
        //HAVING COUNT(S) <= 1
        qdef.select(cb.array(c, cb.count(s)))
        	.groupBy(c)
        	.having(cb.le(cb.count(s), 1));
             
        List<Object[]> results= em.createQuery(qdef)
                .getResultList();        

        for (Object[] result : results) {
            log.info("found=" + Arrays.toString(result));
        }
        assertEquals("unexpected number of rows", 2, results.size());
    }
    
}
