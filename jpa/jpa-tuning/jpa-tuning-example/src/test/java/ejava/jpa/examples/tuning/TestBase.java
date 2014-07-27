package ejava.jpa.examples.tuning;

import java.io.IOException;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkOptionsSystemProperties;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.IResultsConsumer;
import com.carrotsearch.junitbenchmarks.Result;
import com.carrotsearch.junitbenchmarks.WriterConsumer;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkHistoryChart;
import com.carrotsearch.junitbenchmarks.h2.H2Consumer;

import ejava.jpa.examples.tuning.dao.MovieDAOImpl;

@BenchmarkOptions(warmupRounds=0, benchmarkRounds=1)
@BenchmarkHistoryChart
public class TestBase {
    protected static Log log = LogFactory.getLog(TestBase.class);
    protected static String PERSISTENCE_UNIT = "movietune-test-thin";
    protected static EntityManagerFactory emf;
    private static EntityManager em_;
	private static MovieDAOImpl dao;
	private static H2Consumer h2;
	private static final ResultsConsumer resultsConsumer;
	static {
		System.setProperty("jub.db.file", "target/benchmarks");
		System.setProperty(BenchmarkOptionsSystemProperties.CHARTS_DIR_PROPERTY, "target/charts");
		h2=new H2Consumer();
		resultsConsumer=new ResultsConsumer();
	}
	
	public static class ResultsConsumer implements IResultsConsumer {
		private List<Result> results = new LinkedList<Result>();
		@Override
		public void accept(Result result) throws IOException {
			results.add(result);
		}
		public List<Result> getResults() { return results; }
	};

	public @Rule BenchmarkRule benchmarkRun;
	public static ResultsConsumer getResultsConsumer() { return resultsConsumer; }
	protected static String getLabel(Object o) {
		if (o instanceof Class<?>) {
			Class<?> type = (Class<?>)o;
			TestLabel label = type.getAnnotation(TestLabel.class);
			return label != null ? label.label() : type.getSimpleName();
		} else if (o instanceof Method) {
			Method m = (Method)o;
			TestLabel label = m.getAnnotation(TestLabel.class);
			return label != null ? label.label() : m.getName();
		}
		return o.toString();
	}
	public static void printResults() {
		StringBuilder text = new StringBuilder();
		text.append("\n=========================================================================\n\n");
		for (Result r : TestBase.getResultsConsumer().getResults()) {
			text.append(String.format("%s.%s:", getLabel(r.getTestClass()), getLabel(r.getTestMethod())));
			text.append(String.format("warmups=%d",r.warmupRounds));
			text.append(String.format(", rounds=%d",r.benchmarkRounds));
			text.append(String.format(", ave=%s",r.roundAverage.toString()));
			text.append("\n");
		}
		text.append("\n=========================================================================");
		log.info(text.toString());
	}
	
	protected static EntityManagerFactory getEMF() {
		if (emf==null) {
	        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		}
		return emf;
	}
	protected static MovieDAOImpl getDAO() {
		if (dao == null) {
			dao = new MovieDAOImpl();
			em_ = getEMF().createEntityManager();
			dao.setEntityManager(em_);
		}
		return dao;
	}
	
	@BeforeClass
    public static void setUpBaseClass() {
        EntityManager em = getEMF().createEntityManager();
        cleanup(em);
        em.close();
    }
	
	public TestBase() {
		benchmarkRun=new BenchmarkRule(resultsConsumer, new WriterConsumer(), h2);
	}
	
	@Before
	public void setUpBase() {
        EntityManager em = getEMF().createEntityManager();
		new MovieFactory().setEntityManager(em).flush();
		em.close();
		if (em_!=null) {
			em_.clear();
		}
	}
	@After
	public void tearDownBase() {
		log.debug("");
	}
	
	@AfterClass
	public static void tearDownBaseClass() {
		if (em_ != null) {
			em_.close(); 
			em_ = null;
			dao = null;
		}
		if (emf!=null) {
			emf.close(); emf=null;
		}
	}

	/**
	 * Get back to a known starting point
	 * @param em
	 */
	public static void cleanup(EntityManager em) {
    	new MovieFactory().setEntityManager(em).cleanup();
    }
}
