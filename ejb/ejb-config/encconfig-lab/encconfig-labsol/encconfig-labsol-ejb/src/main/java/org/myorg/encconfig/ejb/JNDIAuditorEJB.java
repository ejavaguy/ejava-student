package org.myorg.encconfig.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class JNDIAuditorEJB extends AuditorBase 
	implements AuditorRemote {
	private static final Logger log = LoggerFactory.getLogger(JNDIAuditorEJB.class);
	
	private Boolean publishJMS;
	private EntityManager em;
	private ConnectionFactory cf;
	private Topic topic;
	private @Resource SessionContext ctx;
	
	@PostConstruct
	public void init() {
		InitialContext jndi = null;
		try {
			jndi=new InitialContext();
			publishJMS = (Boolean) ctx.lookup("val/publishJMS");
			em = (EntityManager) ctx.lookup("jpa/em");
			cf = (ConnectionFactory) jndi.lookup("java:comp/env/jms/cf");
			topic = (Topic) jndi.lookup("java:comp/env/jms/topic");
		} catch (NamingException ex) {
			log.error("error looking up resources", ex);
			throw new EJBException("error looking up resources:" + ex);
		} finally {
			close(jndi);
		}
		super.setLog(log);
		super.setConnectionFactory(cf);
		super.setEntityManager(em);
		super.setTopic(topic);
		super.setPublishJMS(isPublishJMS());
	}

	@Override
	public boolean isPublishJMS() {
		return publishJMS!=null && publishJMS;
	}
	@Override
	public boolean havePersistenceContext() {
		return em!=null;
	}
	@Override
	public boolean haveTopic() {
		return topic!=null;
	}
	@Override
	public boolean haveConnectionFactory() {
		return cf!=null;
	}
	
	@Override
	public int audit(String message) {
		return super.audit(message);
	}
}
