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
	@Resource(name="val/publishJMS")
	private Boolean publishJMS;
	
	@PersistenceContext(unitName="encconfig-lab")
	private EntityManager em;
	
	@Resource(lookup="java:/JmsXA")
	private ConnectionFactory cf;
	
	@Resource(lookup="java:/topic/test")
	private Topic topic;

	@PostConstruct
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
