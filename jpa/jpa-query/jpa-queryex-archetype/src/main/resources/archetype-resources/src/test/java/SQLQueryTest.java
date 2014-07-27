#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import static org.junit.Assert.*;



import java.util.Date;
import java.util.List;

import javax.persistence.EntityResult;
import javax.persistence.FieldResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

public class SQLQueryTest extends QueryBase {
    private static final Log log = LogFactory.getLog(SQLQueryTest.class);
    
    /**
     * This test method demonstrates building and executing a SQL query using 
     * the entity manager.
     */
    @Test @Ignore
    public void testSQLQuery() {
    	log.info("*** testSQLQuery ***");    	
    }
    
    /**
     * The test method demonstrates using a custom SQL query to derive the 
     * values used to populate a JPA entity class. The columns returned must
     * match the columns expected by the provider or we must use a
     * @SQLResultSetMapping. In this case -- we are returning a single entity
     * and can simply specify the entity and have the mappings taken from the 
     * entity class' JPA annotations.  
     */
    @Test @Ignore
    public void testSQLResultMapping() {
    	log.info("*** testSQLResultMapping ***");    	
    }

    /**
     * This test method provides an example of using SQLResultSetMapping to 
     * define more than one returned entity. Default names for each of the columns
     * are being returned here. This will cause some ambiguity with two of the 
     * entities and require some refinement in the next two test methods.
     */
    @Test @Ignore
    public void testSQLMultiResultMapping() {
    	log.info("*** testSQLMultiResultMapping ***");    	
    }

    /**
     * This test method is a slight refinement of the test method above in that 
     * is explicitly names each table-alias.column that gets returned by the select.
     * Using this explicit query makes it easier for us to spot the ambiguity 
     * between MOVIE.ID and PERSON.ID. 
     */
    @Test @Ignore
    public void testSQLMultiResultMapping1() {
    	log.info("*** testSQLMultiResultMapping ***");    	
    }

    /**
     * This test method provides the final refinement of the above two test methods --
     * where the second of two tables is aliased so there is no ambiguity between
     * the two tables. This alias is registered in the field mapping of the SqlResultMapping
     * for the particular @EntityResult.
     */
    @Test @Ignore
    public void testSQLMultiResultMapping2() {
    	log.info("*** testSQLMultiResultMapping ***");    	
   }
}