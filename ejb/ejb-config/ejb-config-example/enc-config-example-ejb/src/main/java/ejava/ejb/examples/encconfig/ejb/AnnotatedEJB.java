package ejava.ejb.examples.encconfig.ejb;

import javax.annotation.PostConstruct;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class AnnotatedEJB extends JNDIReader 
	implements JNDIReaderRemote {
	private static final Logger log = LoggerFactory.getLogger(AnnotatedEJB.class);
	private @Resource SessionContext ctx;
	
	private static final int DEFAULT_VALUE=1;
	@Resource(name="val/value1")
	private Integer value1=DEFAULT_VALUE; //default value that is not overridden
	@Resource(name="val/value2")
	private String value2="" + DEFAULT_VALUE;//default value that is overridden
	@Resource(name="val/value3")
	private String value3;        //value provided
	
	@Resource(lookup="java:jboss/datasources/ExampleDS")
	private DataSource ds1;
	@Resource(lookup="java:jboss/datasources/ExampleDS", name="jdbc/ds2")
	private DataSource ds2;
	@Resource(name="jdbc/ds3") //setup in jboss-ejb3.xml
	private DataSource ds3;
	
	@Resource(lookup="java:/queue/test")
	private Queue queue1;
	@Resource(lookup="java:/queue/test", name="jms/queue2")
	private Queue queue2;
	@Resource(name="jms/queue3")
	private Queue queue3;
	
	@Resource(lookup="java:/topic/test")
	private Topic topic1;
	@Resource(lookup="java:/topic/test", name="jms/topic2")
	private Topic topic2;
	@Resource(name="jms/topic3")
	private Topic topic3;
	
	@Resource(lookup="java:/JmsXA")
	private ConnectionFactory cf1;
	@Resource(lookup="java:/JmsXA", name="jms/cf2")
	private ConnectionFactory cf2;
	@Resource(name="jms/cf3")
	private ConnectionFactory cf3;
	
	@EJB
	private InjectedEJB ejb1;
	@EJB(name="ejb/ejb2")
	private InjectedEJB ejb2;
	@EJB(lookup="java:global/enc-config-example-ejb/InjectedEJB!ejava.ejb.examples.encconfig.ejb.InjectedEJB")
	private InjectedEJB ejb3;
	@EJB(lookup="java:app/enc-config-example-ejb/InjectedEJB!ejava.ejb.examples.encconfig.ejb.InjectedEJB")
	private InjectedEJB ejb4;
	@EJB(lookup="java:module/InjectedEJB!ejava.ejb.examples.encconfig.ejb.InjectedEJB")
	private InjectedEJB ejb5;
	@EJB(lookup="java:global/enc-config-example-ejb/InjectedEJB")
	private InjectedEJB ejb6;
	@EJB(lookup="java:app/enc-config-example-ejb/InjectedEJB")
	private InjectedEJB ejb7;
	@EJB(lookup="java:module/InjectedEJB")
	private InjectedEJB ejb8;

	@PersistenceContext(unitName="enc-config")
	private EntityManager em1;
	@PersistenceContext(unitName="enc-config", name="jpa/em2")
	private EntityManager em2;
	@PersistenceContext(name="jpa/em2")
	private EntityManager em3;

	@PersistenceUnit(unitName="enc-config")
	private EntityManagerFactory emf1;
	@PersistenceUnit(unitName="enc-config", name="jpa/emf2")
	private EntityManagerFactory emf2;
	@PersistenceUnit(name="jpa/emf3")
	private EntityManagerFactory emf3;
	
	@PostConstruct
	public void init() {
		super.init();
		log.info("*** AnnotatedEJB ***");
		InitialContext jndi = null;
		try {
			jndi = new InitialContext();
			verify(jndi, "val", "value", value1, value2, value3);
			assert(value1==DEFAULT_VALUE) : "value1 value replaced";
			assert(!value2.equals(""+DEFAULT_VALUE)) : "value2 value not replaced";
			verify(jndi, "jdbc", "ds", ds1, ds2, ds3);
			verify(jndi, "jms", "queue", queue1, queue2, queue3);
			verify(jndi, "jms", "topic", topic1, topic2, topic3);
			verify(jndi, "jms", "cf", cf1, cf2, cf3);
			verify(jndi, "ejb", "ejb", ejb1, ejb2, ejb3, ejb4, ejb5, ejb6, ejb7, ejb8);
			verify(jndi, "jpa", "em", em1, em2, em3);
			verify(jndi, "jpa", "emf", emf1, emf2, emf3);
		} catch (NamingException ex) {
			throw new EJBException(ex);
		} finally {
			if (jndi!=null) {
				try { jndi.close(); }
				catch (NamingException ex) { ex.printStackTrace(); }
			}
		}
	}
}
