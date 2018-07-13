package myorg.relex.collection;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used as a common base implementation by several implementations
 * of hashCode/equals.
 */
@MappedSuperclass
public abstract class Ship {
	@Transient
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private static AtomicInteger instanceId = new AtomicInteger();
	@Transient
	private int oid = instanceId.getAndAdd(1);
	
	@Id
	@GeneratedValue
	protected int id;
	
	@Column(length = 16)
	protected String name; //businessId
	
	@Temporal(TemporalType.TIMESTAMP)
	protected Date created;

	
	public int getId() { return id; }
	public Ship setId(int id) {
		this.id = id;
		return this;
	}

	public String getName() { return name; }
	public Ship setName(String name) {
		this.name = name;
		return this;
	}

	public Date getCreated() { return created; }
	public Ship setCreated(Date created) {
		this.created = created;
		return this;
	}
	
	public abstract int peekHashCode();
	protected int objectHashCode() {
		return super.hashCode();
	}
	
	@Override
	public int hashCode() {
		return logHashCode(peekHashCode());
	}
	
	public int logHashCode(int hashCode) {
		log.info(toString() +
				".hashCode=" + hashCode);
		return hashCode;
	}
	
	public boolean logEquals(Object obj, boolean equals) {
		log.info(new StringBuilder()
		    .append(toString())
		    .append(".equals(id=")
		    .append(obj==null?null : ((Ship)obj).id + ",oid=" + ((Ship)obj).oid)
		    .append(")=")
		    .append(equals)
                    .toString());
		return equals;
	}
	
	public String toString() {
		return getClass().getSimpleName() + "(id=" + id + ",oid=" + oid + ")";
	}
}
