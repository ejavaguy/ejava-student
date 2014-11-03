package org.myorg.encconfig.ejb;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLInjectAuditorEJB extends AuditorBase 
	implements AuditorRemote {
	private static final Logger log = LoggerFactory.getLogger(XMLInjectAuditorEJB.class);
	
	private Boolean publishJMS;
	private EntityManager em;
	private ConnectionFactory cf;
	private Topic topic;
	
	public void init() {
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
