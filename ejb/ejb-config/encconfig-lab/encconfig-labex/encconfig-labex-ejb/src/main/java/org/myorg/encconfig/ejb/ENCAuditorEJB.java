package org.myorg.encconfig.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class ENCAuditorEJB extends AuditorBase 
		implements AuditorRemote {
	private static final Logger log = LoggerFactory.getLogger(ENCAuditorEJB.class);
	//TODO: enc-config 12: define a resource value ENC injection
	//@Resource(name="val/publishJMS")
	private Boolean publishJMS;
	
	//TODO: enc-config 13: define a persistence context ENC injection
	//@PersistenceContext(name="jpa/em")
	private EntityManager em;
	
	//TODO: enc-config 14: define a resource-env ENC injection
	//@Resource(name="jms/topic")
	private Topic topic;

	//TODO: enc-config 15: define a resource ENC injection
	//@Resource(name="jms/cf")
	private ConnectionFactory cf;
	
	//TODO: enc-config 16: activate this method after injection
	//@PostConstruct
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
