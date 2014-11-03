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
	
	//TODO: enc-config 18: activate initialization method to perform ENC lookups 
	//@PostConstruct
	public void init() {
		InitialContext jndi = null;
		try {
			jndi=new InitialContext();
			//TODO enc-config 19: lookup resource value in ENC 
			//publishJMS = (Boolean) ctx.lookup("val/publishJMS");
			//TODO enc-config 20: lookup persistence context in ENC 
			//em = (EntityManager) ctx.lookup("jpa/em");
			//TODO enc-config 21: lookup resource in ENC using JNDI java:comp/env 
			//topic = (Topic) jndi.lookup("java:comp/env/jms/topic");
			//TODO enc-config 22: lookup resource in ENC using JNDI java:comp/env
			//cf = (ConnectionFactory) jndi.lookup("java:comp/env/jms/cf");
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
