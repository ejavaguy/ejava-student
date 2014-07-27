package myorg.relex.one2many;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * This entity class provides an example of the one side of a one-to-many, uni-directional relation
 * that is realized through a JoinTable.
 */
@Entity
@Table(name="RELATIONEX_BUS")
public class Bus {
	@Id
	private int number;

	@OneToMany
	@JoinTable(
			name="RELATIONEX_BUS_RIDER",
			joinColumns={@JoinColumn(name="BUS_NO")},
			inverseJoinColumns={@JoinColumn(name="RIDER_ID")}
	)
	private List<Rider> passengers;

	protected Bus() {}
	public Bus(int number) {
		this.number = number;
	}

	public int getNumber() { return number; }

	public List<Rider> getPassengers() {
		if (passengers==null) { passengers = new ArrayList<Rider>(); }
		return passengers; 
	}
	public void setPassengers(List<Rider> passengers) {
		this.passengers = passengers;
	}
}
