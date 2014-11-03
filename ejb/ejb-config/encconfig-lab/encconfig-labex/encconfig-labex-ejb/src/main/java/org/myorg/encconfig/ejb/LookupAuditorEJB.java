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
public class LookupAuditorEJB extends AuditorBase 
		implements AuditorRemote {
	private static final Logger log = LoggerFactory.getLogger(LookupAuditorEJB.class);
	//TODO: enc-config 02: define a resource value injection here 
	//@Resource(name="val/publishJMS")
	private Boolean publishJMS;
	
	//TODO: enc-config 04: define a persistence context injection here 
	//@PersistenceContext(unitName="encconfig-lab")
	private EntityManager em;
	
	//TODO: enc-config 06: define a resource value lookup and injection here
	//@Resource(lookup="java:/topic/test")
	private Topic topic;

	//TODO: enc-config 08: define a resource lookup and injection here
	//@Resource(lookup="java:/JmsXA")
	private ConnectionFactory cf;

	//TODO: enc-config 10: activate this method after injection occurs
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
