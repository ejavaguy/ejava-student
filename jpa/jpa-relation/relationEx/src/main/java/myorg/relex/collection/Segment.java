package myorg.relex.collection;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This class represents an example entity that has an order in its parent's list.
 */
@Entity
@Table(name="RELATIONEX_SEGMENT")
public class Segment implements Comparable<Segment>{
	private static final Logger log = LoggerFactory.getLogger(Segment.class);
	@Id @GeneratedValue
	private int id;
	
	private int number; //a one-up sequence used to order a route
	
	@Column(name="TO", length=16)
	private String to;
	@Column(name="FM", length=16)
	private String from;
	
	
	public int getId() { return id; }

	public int getNumber() { return number; }
	public Segment setNumber(int number) {
		this.number = number;
		return this;
	}
	
	public String getTo() { return to; }
	public Segment setTo(String to) {
		this.to = to;
		return this;
	}
	
	public String getFrom() { return from; }
	public Segment setFrom(String from) {
		this.from = from;
		return this;
	}

	@Override
	public int compareTo(Segment rhs) {
		if (this == rhs) { return 0; }
		int result = number - rhs.number;
		log.debug(getClass().getSimpleName() + toString() + 
				".compareTo" + rhs.toString() + 
				"=" + result
				);
		return result;
	}
	
	@Override
	public String toString() {
		return "(id=" + id + ",number=" + number + ")";
	}

	/*
	@Override
	public int hashCode() {
		return number + (from==null?0 : from.hashCode()) + (to==null?0 : to.hashCode());
	}
	@Override
	public boolean equals(Object obj) {
		try {
			if (this == obj) { return true; }
			Segment rhs=(Segment)obj;
			return hashCode() == rhs.hashCode();
		} catch(Exception ex) { return false; }
	}
	*/
}
