package myorg.relex.one2manybi;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

/**
 * This class is an example of the one/inverse side of a one-to-many, bi-directional
 * relationship mapped using a compound foreign key that is partially derived from the 
 * parent primary key.
 */
@Entity
@Table(name="RELATIONEX_CAR")
public class Car {
	@Id @GeneratedValue
	private int id;
	
	@OneToMany(
			mappedBy="car",
			cascade={CascadeType.PERSIST, CascadeType.DETACH}, 
			orphanRemoval=true,
			fetch=FetchType.LAZY)
	private Set<Tire> tires;
	
	@Column(length=16)
	private String model;
	@Temporal(TemporalType.DATE)
	private Date year;

	public int getId() { return id; }
	public Set<Tire> getTires() {
		if (tires==null) {
			tires=new HashSet<Tire>();
		}
		return tires;
	}

	public String getModel() { return model; }
	public void setModel(String model) {
		this.model = model;
	}
	
	public Date getYear() { return year; }
	public void setYear(Date year) {
		this.year = year;
	}
	
	@Override
	public int hashCode() {
		return (model==null?0:model.hashCode()) + (year==null?0:year.hashCode());
	}
	@Override
	public boolean equals(Object obj) {
		try {
			if (this==obj) { return true; }
			Car rhs = (Car)obj;
			return id==0 ? super.equals(obj) : id==rhs.id;
		} catch (Exception ex) { return true; }
	}
}
