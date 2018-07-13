package ejava.jpa.hibernatemigration.legacyhbm;

import java.io.Serializable;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.mapping.PersistentClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import ejava.jpa.hibernatemigration.BaseMigrationTest;

public class CustomizedHBMTest extends BaseMigrationTest {
	private static final Logger log = LoggerFactory.getLogger(CustomizedHBMTest.class);
	private static SessionFactory sessionFactory;
	private Session session;
	
	@SuppressWarnings("serial")
	private static class CustomizedConfiguration extends Configuration {
		@Override
		public Configuration configure() throws HibernateException {
			Configuration config = super.configure();
			PersistentClass mapping = config.getClassMapping(Sale.class.getName());
			mapping.getTable().setName(mapping.getTable().getName()+"2");
			return config;
		}
	}
	
	@SuppressWarnings("serial")
	private static class CustomNamingStragey extends DefaultNamingStrategy {
		@Override
		public String classToTableName(String className) {
			log.debug("classToTableName(" + className + ")");
			String tableName = super.classToTableName(className);
			if (className.equals(Sale.class.getName())) {
				tableName = tableName + 3;
			}
			return tableName;
		}
		@Override
		public String tableName(String tableName) {
			log.debug("tableName(" + tableName + ")");
			return super.tableName(tableName);
		}
	}
	
	@BeforeClass
	public static void setUpClass() {
		log.debug("creating sessionFactory");
		sessionFactory=new CustomizedConfiguration().configure().buildSessionFactory();
		
		sessionFactory=new Configuration()
			.setNamingStrategy(new CustomNamingStragey())
			.configure()
			.buildSessionFactory();
	}
	
	@Before
	public void setUp() {
		log.debug("creating session");
		session = sessionFactory.getCurrentSession();
		session.beginTransaction();
	}
	
	@After
	public void tearDown() {
		if (session != null) {
			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}
		}
	}
	
	@AfterClass
	public static void tearDownClass() {
		if (sessionFactory!=null) {
			sessionFactory.close();
		}
	}
	
	@Override
	protected void save(Object entity) { session.save(entity); }
	@Override
	protected void flush() { session.flush(); }
	@Override
	protected void clear() { session.clear(); }
	@Override
	@SuppressWarnings("unchecked")
	protected <T> T get(Class<T> clazz, Serializable pk) { return (T)session.get(clazz, pk); }
	@Override
	protected void beginTransaction() { sessionFactory.getCurrentSession().beginTransaction(); }
	@Override
	protected void commitTransaction() { sessionFactory.getCurrentSession().getTransaction().commit(); }
}
