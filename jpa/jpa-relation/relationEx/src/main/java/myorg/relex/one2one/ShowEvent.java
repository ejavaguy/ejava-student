package myorg.relex.one2one;

import java.util.Date;

import javax.persistence.*;

/**
 * This class represents the passive side of a one-to-one
 * uni-directional relationship where the parent uses
 * a composite primary key that must be represented in 
 * the dependent entity's relationship.
 */
@Entity
@Table(name="RELATIONEX_SHOWEVENT")
@IdClass(ShowEventPK.class)
public class ShowEvent {
	@Id
	@Temporal(TemporalType.DATE)
	private Date date;
	@Id
	@Temporal(TemporalType.TIME)
	private Date time;
	@Column(length=20)
	private String name;
	
	public ShowEvent() {}
	public ShowEvent(Date date, Date time) {
		this.date = date;
		this.time = time;
	}
	public Date getDate() { return date; }
	public Date getTime() { return time; }

	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
}
