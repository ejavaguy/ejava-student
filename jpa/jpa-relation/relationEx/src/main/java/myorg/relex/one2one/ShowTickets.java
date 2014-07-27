package myorg.relex.one2one;

import java.util.Date;

import javax.persistence.*;

/**
 * This class provides an example of a the owning entity of a
 * one-to-one, uni-directional relationship where the dependent's
 * primary key is derived from the parent and the parent uses
 * a composite primary key.
 */
@Entity
@Table(name="RELATIONEX_SHOWTICKETS")
@IdClass(ShowEventPK.class)
public class ShowTickets {
	@Id 
	@Temporal(TemporalType.DATE)
	@Column(name="TICKET_DATE")
	private Date date;
	@Id
	@Temporal(TemporalType.TIME)
	@Column(name="TICKET_TIME")
	private Date time;
	
	@OneToOne(optional=false, fetch=FetchType.EAGER)
	@PrimaryKeyJoinColumns({
		@PrimaryKeyJoinColumn(name="TICKET_DATE", referencedColumnName="date"),
		@PrimaryKeyJoinColumn(name="TICKET_TIME", referencedColumnName="time"),
	})
	private ShowEvent show;
	
	@Column(name="TICKETS")
	private int ticketsLeft;

	public ShowTickets() {}
	public ShowTickets(ShowEvent show) {
		this.date = show.getDate();
		this.time = show.getTime();
		this.show = show;
	}

	public Date getDate() { return show==null ? null : show.getDate(); }
	public Date getTime() { return show==null ? null : show.getTime(); }
	public ShowEvent getShow() { return show; }

	public int getTicketsLeft() { return ticketsLeft; }
	public void setTicketsLeft(int ticketsLeft) {
		this.ticketsLeft = ticketsLeft;
	}
}
