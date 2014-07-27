package ejava.jpa.examples.query.jpaql;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;

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

public class JPAQLTest extends QueryBase {
	private static final Log log = LogFactory.getLog(BulkQueryTest.class);

    private <T> List<T> executeQuery(String ejbqlString, Class<T> resultType) {
        return executeQuery(ejbqlString, null, resultType);
    }

    private <T> List<T> executeQuery(String ejbqlString, 
            Map<String, Object> params, Class<T> resultType) {
        TypedQuery<T> query = em.createQuery(ejbqlString, resultType);
        log.info("executing query:" + ejbqlString);
        if (params != null && !params.isEmpty()) {
            StringBuilder text=new StringBuilder();
            for(String key: params.keySet()) {
                Object param = params.get(key);
                text.append(key +"=" + param + ",");
                query.setParameter(key, param);
            }
            log.info("   with params:{" + text + "}");
        }
        List<T> objects = query.getResultList();
        for(Object o: objects) {
           log.info("found result:" + o);
        }
        return objects;
    }
    
    /**
     * This test demonstrates a single JPAQL query
     */
    @Test
    public void testSimpleSelect() {
        log.info("*** testSimpleSelect() ***");
        
        TypedQuery<Customer> query = em.createQuery(
        		"select object(c) from Customer as c", 
        		Customer.class);
        List<Customer> results = query.getResultList();
        for (Customer result : results) {
        	log.info("found=" + result);
        }
        int rows = results.size();
        assertTrue("unexpected number of customers", rows > 0);
    }
    
    /**
     * This test demonstrates querying for a non-entity. The property queried
     * for is located off a path from the root query term.
     */
    @Test
    public void testNonEntityQuery() {
        log.info("*** testNonEntityQuery() ***");
        
        TypedQuery<String> query = em.createQuery(
                "select c.lastName from Customer c", String.class);
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
        
        TypedQuery<Object[]> query = em.createQuery(
                "select c.firstName, c.hireDate from Clerk c", Object[].class);
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
        
        TypedQuery<Tuple> query = em.createQuery(
                "select c.firstName as firstName, c.hireDate as hireDate from Clerk c", Tuple.class);
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
        
        TypedQuery<Receipt> query = em.createQuery(
            String.format("select new %s(", Receipt.class.getName()) +
            "s.id,s.buyerId,s.date, s.amount) " +
            "from Sale s", Receipt.class);
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
        
        TypedQuery<Object[]> query = em.createQuery(
                "select s.id, s.store.name from Sale s", Object[].class);
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
        
        int rows = executeQuery(
//              "select sale.date from Clerk c INNER JOIN c.sales sale", 
//              "select sale.date from Clerk c, IN (c.sales) sale", 
              "select sale.date from Clerk c JOIN c.sales sale", 
                Date.class).size();
        assertTrue("unexpected number of sales", rows > 0);
    }

    /**
     * This test provides an example collection path using an LEFT OUTER JOIN
     */
    @Test
    public void testOuterJoin() {
        log.info("*** testOuterJoin() ***");
        
        TypedQuery<Object[]> query = em.createQuery(
            "select c.id, c.firstName, sale.amount " +
            "from Clerk c " +
//            "LEFT OUTER JOIN c.sales sale",
            "LEFT JOIN c.sales sale",
            Object[].class);
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
    	
    	int rows = executeQuery(
    		"select c from Sale s, Customer c " +
    		"where c.id = s.buyerId", 
    		Customer.class).size();
        assertTrue("unexpected number of customers", rows > 0);
    }
    

    /**
     * This test demonstrates the function of a JOIN FETCH to perform the 
     * EAGER retrieval of entities as a side-effect of the query
     */
    @Test
    public void testFetchJoin1() {        
        log.info("** testFetchJoin1() ***");
        EntityManager em2 = createEm();
        Store store = em2.createQuery(
        		"select s from Store s JOIN s.sales " +
        		"where s.name='Big Al''s'",
        		Store.class).getSingleResult();
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
        Store store = em2.createQuery(
        		"select s from Store s JOIN FETCH s.sales " +
        		"where s.name='Big Al''s'",
        		Store.class).getSingleResult();
        log.info("em.contains(" + em2.contains(store) + ")");
        em2.close();
       	store.getSales().get(0).getAmount();
    }

    /**
     * This test demonstrates how we can leverage multi-selects to extend a 
     * JOIN FETCH beyond a single root anchoring point.
     */
    @Test
    public void testMultiSelectEntities() {
        log.info("** testMultiSelectEntities() ***");

        EntityManager em2 = createEm();
        Sale sale = em2.createQuery(
        		"select sa as sale " +
        		"from Sale sa " +
        		"join sa.store st " +
        		"join sa.clerks c " +        		
        		"join fetch sa.clerks " +
        		"join fetch sa.store " +
        		"where st.name='Big Al''s' " +
        		"and sa member of c.sales",
        		Tuple.class).getResultList().get(0).get("sale", Sale.class);
        em2.close();
       	sale.getAmount();
       	sale.getClerks().get(0).getFirstName();
       	sale.getStore();
    }
    
    /**
     * This test demonstrates the use of DISTINCT to limit the results
     * to only unique values
     */
    @Test
    public void testDISTINCT() {
        log.info("*** testDISTINCT() ***");
        
        int rows = executeQuery(
                "select DISTINCT c.lastName from Customer c",
                Object[].class).size();
        assertEquals("unexpected number of rows", 3, rows);
        rows = executeQuery(
                "select DISTINCT c.firstName from Customer c",
                Object[].class).size();
        assertEquals("unexpected number of rows for DISTINCT", 2, rows);
    }
    
    
    //where clauses
    
    
    /**
     * This test provides an example of an equality test in the where clause
     */
    @Test
    public void testLiteral() {
        log.info("*** testLiteral() ***");
        int rows = executeQuery(
                "select c from Customer c " +
                "where c.firstName='cat'",
                Customer.class).size();
        assertEquals("unexpected number of rows:", 1, rows);
    }

    @Test
    public void testSpecialCharacter() {
        log.info("*** testSpecialCharacter() ***");
        int rows = executeQuery(
                "select s from Store s " +
                "where s.name='Big Al''s'",
                Store.class).size();
        assertEquals("unexpected number of rows", 1, rows);
    }
    
    /**
     * This test demonstrates the use of like in where clauses
     */
    @Test
    public void testLike() {
        log.info("*** testLike() ***");
        
        int rows = executeQuery(
                  "select c from Clerk c " +
                  "where c.firstName like 'M%'",
                  Clerk.class).size();
        assertEquals("unexpected number of rows", 2, rows);
        
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("firstName", "M%");
        rows = executeQuery(
                "select c from Clerk c " +
                "where c.firstName like :firstName)",
                params,Clerk.class).size();
        assertEquals("unexpected number of rows", 2, rows);        

        params = new HashMap<String, Object>();
        params.put("firstName", "M");
        rows = executeQuery(
                "select c from Clerk c " +
                "where c.firstName like concat(:firstName,'%')",
                params, Clerk.class).size();
        assertEquals("unexpected number of rows", 2, rows);    
        
        rows = executeQuery(
                "select c from Clerk c " +
                "where c.firstName like '_anny'",
                Clerk.class).size();
        assertEquals("unexpected number of rows", 1, rows);
        
    }
    
    /**
     * This test provides a demonstration of using a math formual within the 
     * where clause.
     */
    @Test
    public void testFormulas() {
        log.info("*** testFormulas() ***");
        
        String jpaql = "select count(s) from Sale s " +
                       "where (s.amount * :tax) > :amount";
        TypedQuery<Number> query = em.createQuery(jpaql, Number.class)
        		.setParameter("amount", new BigDecimal(10.00));
                
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
        int rows = executeQuery(
            "select c from Customer c " +
            "where (c.firstName='cat' AND c.lastName='inhat')" +
                "OR c.firstName='thing'",
            Customer.class).size();
        assertEquals("unexpected number of rows", 3, rows);        

        rows = executeQuery(
            "select c from Customer c " +
            "where (NOT (c.firstName='cat' AND c.lastName='inhat')) " +
                "OR c.firstName='thing'",
            Customer.class).size();
        assertEquals("unexpected number of rows", 2, rows);        
    }
    
    /**
     * This test provides a demonstration for comparing two entities within
     * a query
     */
    @Test
    public void testEquality() {
        log.info("*** testEquality() ***");
        
        //get a clerk entity
        Clerk clerk = em.createQuery(
            	"select c from Clerk c where c.firstName = 'Manny'", 
            	Clerk.class)
               .getSingleResult();
        
        //find all sales that involve this clerk
        List<Sale> sales = em.createQuery( 
            "select s from Sale s " +
            "JOIN s.clerks c " +
            "where c = :clerk", 
            Sale.class)
	            .setParameter("clerk", clerk)
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
        
        String query = "select s from Sale s " +
            "where s.amount BETWEEN :low AND :high";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("low", new BigDecimal(90.00));
        params.put("high", new BigDecimal(110.00));
                
        int rows = executeQuery(query, params, Sale.class).size();    
        assertEquals("unexpected number of rows", 1, rows);

        query = "select s from Sale s " +
                "where s.amount NOT BETWEEN :low AND :high";
        params = new HashMap<String, Object>();
        params.put("low", new BigDecimal(90.00));
        params.put("high", new BigDecimal(110.00));
                    
        rows = executeQuery(query, params, Sale.class).size();    
        assertEquals("unexpected number of rows", 1, rows);
    }
    
    /**
     * This test provides a demonstration of testing for a null value.
     */
    @Test
    public void testIsNull() {
        log.info("*** testIsNull() ***");
        
        String query = "select s from Sale s " +
            "where s.store IS NULL";
                
        int rows = executeQuery(query, Sale.class).size();    
        assertEquals("unexpected number of rows", 0, rows);

        query = "select s from Sale s " +
            "where s.store IS NOT NULL";
            
        rows = executeQuery(query, Sale.class).size();    
        assertEquals("unexpected number of rows", 2, rows);
    }

    /**
     * This test provides an example of testing whether the collection
     * is empty
     */
    @Test
    public void testIsEmpty() {
        log.info("*** testIsEmpty() ***");
        
        String query = "select c from Clerk c " +
            "where c.sales IS EMPTY";
                
        int rows = executeQuery(query, Clerk.class).size();    
        assertEquals("unexpected number of rows", 1, rows);

        query = "select c from Clerk c " +
            "where c.sales IS NOT EMPTY";
            
        rows = executeQuery(query, Clerk.class).size();    
        assertEquals("unexpected number of rows", 2, rows);
    }

    /**
     * This test provides a demonstration of testing membership in 
     * a collection.
     */
    @Test
    public void testMemberOf() {
        log.info("*** testMemberOf() ***");
        
        //get a clerk entity
        Clerk clerk = em.createQuery(
            "select c from Clerk c where c.firstName = 'Manny'",
            Clerk.class)
              .getSingleResult();
        
        //find all sales that involve this clerk
        List<Sale> sales = em.createQuery(
            "select s from Sale s " +
            "where :clerk MEMBER OF s.clerks", Sale.class)
                .setParameter("clerk", clerk)
                .getResultList();
        
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
       
       List<Customer> results = executeQuery(
               "select c from Customer c " +
               "where c.id IN " +
                   "(select s.buyerId from Sale s " +
                    "where s.amount > 100)",
              Customer.class);       
       assertEquals("unexpected number of rows", 1, results.size());
    }
    
    
    /**
     * This test provides a demonstration for using the ALL subquery
     * result evaluation.
     */
    @Test
    public void testAll() {
        log.info("*** testAll() ***");  
        
        //executeQuery("select s from Sale s", Sale.class);
        
		List<Clerk> results = executeQuery(
                "select c from Clerk c " +
                "where 125 < ALL " +
                "   (select s.amount from c.sales s)",
               Clerk.class);       
        assertEquals("unexpected number of rows", 2, results.size());
        
        results = executeQuery(
                "select c from Clerk c " +
                "where 125 > ALL " +
                "   (select s.amount from c.sales s)",
               Clerk.class);       
        assertEquals("unexpected number of rows", 1, results.size());
    }
    
    /**
     * This test provides a demonstration for using the ANY subquery
     * result evaluation
     */
    @Test
    public void testAny() {
		List<Clerk> results = executeQuery(
                "select c from Clerk c " +
                "where 125 < ANY " +
                "   (select s.amount from c.sales s)",
               Clerk.class);       
        assertEquals("unexpected number of rows", 2, results.size());

		results = executeQuery(
                "select c from Clerk c " +
                "where 125 > ANY " +
                "   (select s.amount from c.sales s)",
               Clerk.class);       
        assertEquals("unexpected number of rows", 1, results.size());
     }

    
    
    /**
     * This test method demonstrates several date functions
     */
    @Test
    public void testStringFunctions() {
        log.info("*** testStringFunctions() ***");

        int rows = executeQuery(
                "select c from Customer c " +
                "where c.firstName='CAT'",
                Customer.class).size();
        assertEquals("unexpected number of rows", 0, rows);

        rows = executeQuery(
                "select c from Customer c " +
                "where c.firstName=LOWER('CAT')",
                Customer.class).size();
        assertEquals("unexpected number of rows", 1, rows);
    
        rows = executeQuery(
                "select UPPER(c.firstName) from Customer c " +
                "where c.firstName=LOWER('CAT')",
                String.class).size();
        assertEquals("unexpected number of rows", 1, rows);
    
        rows = executeQuery(
                "select TRIM(LEADING 'c' FROM c.firstName) from Customer c " +
                "where c.firstName='cat')",
                String.class).size();
        assertEquals("unexpected number of rows", 1, rows);
        
        rows = executeQuery(
                "select c from Customer c " +
                "where CONCAT(CONCAT(c.firstName,' '),c.lastName) ='cat inhat')",
                Customer.class).size();
        assertEquals("unexpected number of rows", 1, rows);
        
        rows = executeQuery(
                "select c from Customer c " +
                "where LENGTH(c.firstName) = 3",
                Customer.class).size();
        assertEquals("unexpected number of rows", 1, rows);
        
        rows = executeQuery(
                "select c from Customer c " +
                "where LOCATE('cat',c.firstName,2) > 0",
                Customer.class).size();
        assertEquals("unexpected number of rows", 0, rows);        

        rows = executeQuery(
                "select c from Customer c " +
                "where LOCATE('at',c.firstName,2) > 1",
                Customer.class).size();
        assertEquals("unexpected number of rows", 1, rows);        

        rows = executeQuery(
                "select SUBSTRING(c.firstName,2,2) from Customer c " +
                "where c.firstName = 'cat'",
                String.class).size();
        assertEquals("unexpected number of rows", 1, rows);
        
        rows = executeQuery(
                "select c from Customer c " +
                "where SUBSTRING(c.firstName,2,2) = 'at'",
                Customer.class).size();
        assertEquals("unexpected number of rows", 1, rows);        
    }

    /**
     * This test method demonstrates using date functions.
     */
    @Test
    public void testDates() {        
        log.info("*** testDates() ***");

        int rows = executeQuery(
                "select s from Sale s " +
                "where s.date < CURRENT_DATE",
                Sale.class).size();
        assertEquals("unexpected number of rows", 2, rows);
        
        rows = executeQuery(
                "select s from Sale s " +
                "where s.date = CURRENT_DATE",
                Sale.class).size();
        assertEquals("unexpected number of rows", 0, rows);

        rows = em.createQuery(
                "update Sale s " +
                "set s.date = CURRENT_DATE").executeUpdate();
        assertEquals("unexpected number of rows", 2, rows);
        
        em.getTransaction().commit();
        em.clear(); //remove stale objects in cache
        
        rows = executeQuery(
                "select s from Sale s " +
                "where s.date = CURRENT_DATE",
                Sale.class).size();
        assertEquals("unexpected number of rows", 2, rows);
    }
    
    
    /**
     * This test method provides a demonstration of order by capability.
     */
    @Test
    public void testOrderBy() {
        log.info("*** testOrderBy() ***");

        List<Sale> results = executeQuery(
            "select s from Sale s ORDER BY s.amount ASC", Sale.class); 
        assertEquals("unexpected number of rows", 2, results.size());
        assertEquals("unexpected first element", 
                100, 
                results.get(0).getAmount().intValue());
        assertEquals("unexpected first element", 
                150, 
                results.get(1).getAmount().intValue());

        
        results = executeQuery(
                "select s from Sale s ORDER BY s.amount DESC", Sale.class); 
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

        List<Number> results= executeQuery(
                "select COUNT(s) from Sale s", Number.class);
        assertEquals("unexpected number of rows", 1, results.size());
        assertEquals("unexpected result", 2, results.get(0).intValue());
    }
    
    /**
     * This test provides a demonstration of the MIN and MAX aggregate functions
     */
    @Test
    public void testMaxMin() {        
        log.info("*** testMaxMin() ***");

        List<Number> results= executeQuery(
                "select max(s.amount) from Sale s", Number.class);
        assertEquals("unexpected number of rows", 1, results.size());
        assertEquals("unexpected result", 150, results.get(0).intValue());
        
        results= executeQuery(
                "select min(s.amount) from Sale s", Number.class);
        assertEquals("unexpected number of rows", 1, results.size());
        assertEquals("unexpected result", 100, results.get(0).intValue());
    }

    /**
     * This test provides a demonstration of the SUM and AVE aggregate functions
     */
    @Test
    public void testSumAve() {        
        log.info("*** testSumAve() ***");

        List<Number> results= executeQuery(
            "select sum(s.amount) from Sale s", Number.class);
        assertEquals("unexpected number of rows", 1, results.size());
        assertEquals("unexpected result", 250, results.get(0).intValue());
        
        results= executeQuery(
                "select avg(s.amount) from Sale s", Number.class);
        assertEquals("unexpected number of rows", 1, results.size());
        assertEquals("unexpected result", 125, results.get(0).intValue());
    }
    
    /**
     * This test method provides an example of using group by 
     */
    @Test
    public void testGroupBy() {
        log.info("*** testGroupBy() ***");
    	
        List<Object[]> results= em.createQuery(
                "select c, COUNT(s) from Clerk c " +
                "LEFT JOIN c.sales s " +
                "GROUP BY c", Object[].class)
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
    	
        List<Object[]> results= em.createQuery(
                "select c, COUNT(s) from Clerk c " +
                "LEFT JOIN c.sales s " +
                "GROUP BY c " +
                "HAVING COUNT(s) <= 1", Object[].class)
                .getResultList();
        for (Object[] result : results) {
        	log.info("found=" + Arrays.toString(result));
        }
        assertEquals("unexpected number of rows", 2, results.size());
    }
}
