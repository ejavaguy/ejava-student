package myorg.entityex.annotated;

import java.util.Calendar;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name="ENTITYEX_SHARK")
public class Shark {
	@Id @GeneratedValue
	private int id;
	//@Temporal(TemporalType.DATE)
	private Calendar aDate;
	//@Temporal(TemporalType.TIME)
	private Date aTime;
	//@Temporal(TemporalType.TIMESTAMP)
	private Date aTimestamp;
	
	public int getId() { return id; }
	public Shark setId(int id) {
		this.id = id; return this;
	}
	
	public Calendar getDate() { return aDate; }
	public Shark setDate(Calendar date) {
		this.aDate = date; return this;
	}
	
	public Date getTime() { return aTime; }
	public Shark setTime(Date time) {
		this.aTime = time; return this;
	}
	
	public Date getTimestamp() { return aTimestamp; }
	public Shark setTimestamp(Date timestamp) {
		this.aTimestamp = timestamp; return this;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
			.append("aDate=").append(aDate.getTime())
			.append(", aTime=").append(aTime)
			.append(", aTimestamp=").append(aTimestamp)
			.toString();
	}
}
