package ejava.jpa.examples.tuning.stuff;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.annotation.AxisRange;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;

import ejava.jpa.examples.tuning.bo.Person;

@Ignore
@AxisRange(min = 0, max = 10)
@BenchmarkMethodChart(filePrefix = "benchmark-lists")
@BenchmarkOptions(warmupRounds=1, benchmarkRounds=3, concurrency=1)
public class MovieRoleFKIndexTest extends QueryBase {
	private static final Log log = LogFactory.getLog(MovieRoleFKIndexTest.class);
	
	@Rule
	public BenchmarkRule benchmarkRun = new BenchmarkRule();
	
	private static Person kevinBacon;
	
	@Before
    public void setUp() throws Exception {
		log.info("**** setUp() ***");
		EntityManager em = emf.createEntityManager();
    	//get the Kevin Bacon who played in the movie "Tremors"
    	kevinBacon = em.createQuery(
    			"select r.actor.person " +
    			"from MovieRole r " +
    			"where r.movie.title = 'Tremors' and " +
    			"r.actor.person.lastName='Bacon' and " +
    			"r.actor.person.firstName='Kevin'", Person.class)
    			.getSingleResult();
    	em.close();
    }
    	
	//find people who are 1 step from Kevin Bacon
    @Test
    public void oneStepPerson() {
    	EntityManager em=emf.createEntityManager();
    	List<Person> people = em.createQuery(
    			"select a.person from Actor a " +
    			"join a.roles ar " +
    			"join a.person ap " +
    			"where ar.movie in (select m from Movie m " +
    			    "inner join m.cast mr " +
    		        "inner join mr.actor ma " +
    		        "inner join ma.person mp " +
    			    "where mp.id = :id))" +
    			 "and ap.id not = :id", Person.class)
    			 .setParameter("id", kevinBacon.getId())
    			.getResultList();
    	log.debug("1 step=" + people.size());
    	em.close();
    }
}
