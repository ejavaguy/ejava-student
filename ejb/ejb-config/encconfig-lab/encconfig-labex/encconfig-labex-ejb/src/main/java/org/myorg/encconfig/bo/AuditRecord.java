package org.myorg.encconfig.bo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="EJBCONFIG_AUDITREC")
public class AuditRecord {
	@Id @GeneratedValue
	private int id;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date timestamp;
	@Column(length=200, nullable=false)
	private String message;
	
	protected AuditRecord(){}
	public AuditRecord(Date timestamp, String message) {
		this.timestamp = timestamp;
		this.message = message;
	}
	
	public int getId() { return id; }
	public Date getTimestamp() { return timestamp; }
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getMessage() { return message; }
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
		return (timestamp==null?null : df.format(timestamp)) + 
				", " + message;
	}
}
