package org.myorg.encconfig.ejb;

import java.util.Date;

import javax.ejb.EJBException;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.myorg.encconfig.bo.AuditRecord;
import org.slf4j.Logger;

public class AuditorBase {
	private Logger log_;
	private boolean publishJMS_;
	private EntityManager em_;
	private ConnectionFactory cf_;
	private Topic topic_;

	public void setLog(Logger log) { this.log_ = log; }
	public void setEntityManager(EntityManager em) { this.em_ = em; }
	public void setConnectionFactory(ConnectionFactory cf) { this.cf_ = cf; }
	public void setTopic(Topic topic) { this.topic_ = topic; }
	public void setPublishJMS(boolean publishJMS) { 
		this.publishJMS_ = publishJMS; 
	}
	public int audit(String message) {
		AuditRecord rec = new AuditRecord(new Date(), message);
		int actions=0;
		if (persistRecord(rec) != null) {
			actions += 1;
		}
		if (publishJMS_ && cf_ != null) {
			publishRecord(rec);
			actions += 1;
		}
		return actions;
	}
	
	protected AuditRecord persistRecord(AuditRecord rec) {
		if (em_!=null) {
			em_.persist(rec);
			log_.debug("message persisted:{}", rec);
			return rec;		
		}
		return null;
	}
	
	
	protected void publishRecord(AuditRecord rec) {
		Connection connection=null;
		Session session = null;
		MessageProducer publisher = null;
		try {
			connection = cf_.createConnection();
			session = connection.createSession(
					false, Session.AUTO_ACKNOWLEDGE);
			TextMessage msg = session.createTextMessage(rec.getMessage());
			publisher = session.createProducer(topic_);
			publisher.send(msg);
			log_.debug("message sent:{}", msg);
		} catch (JMSException ex) {
			log_.error("error sending JMS message", ex);
			throw new EJBException("error sending JMS message:"+ex);
		} finally {
			close(connection, session);
		}
	}
	
	protected void close(Connection conn, Session session) {
		try { if (session != null) { session.close(); } } 
		catch (JMSException ex) {log_.info("error closing session", ex); }
		try { if (conn != null) { conn.close(); } } 
		catch (JMSException ex) {log_.info("error closing connection", ex); }
	}
	protected void close(Context jndi) {
		try { if (jndi != null) { jndi.close(); } } 
		catch (NamingException ex) {log_.info("error closing jndi", ex); }
	}

}
