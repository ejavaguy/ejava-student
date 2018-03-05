package myorg.relex.one2one;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;

/**
 * This class will be used as an IdClass for the ShowEvent
 * entity.
 */
@Embeddable
public class ShowEventPK implements Serializable {
	private static final long serialVersionUID = 1L;
	private Date date;
	private Date time;
	
	public ShowEventPK(){}
	public ShowEventPK(Date date, Date time) {
		this.date = date;
		this.time = time;
	}
	
	public Date getDate() { return date; }
	public Date getTime() { return time; }
	
	@Override
	public int hashCode() { return date.hashCode() + time.hashCode(); }
	@Override
	public boolean equals(Object obj) {
		try {
			if (this==obj) { return true; }
			return date.equals(((ShowEventPK)obj).date) &&
					time.equals(((ShowEventPK)obj).time);
		} catch (Exception ex) { return false; }
	}
}
