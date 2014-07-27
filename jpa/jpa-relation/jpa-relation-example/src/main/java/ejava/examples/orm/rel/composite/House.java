package ejava.examples.orm.rel.composite;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.*;

@Entity @Table(name="ORMREL_HOUSE")
public class House {
	@Id @GeneratedValue
	private int id;
	
	@OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE}, 
			fetch=FetchType.LAZY, mappedBy="house")
	private Collection<Room> rooms=new ArrayList<Room>();

	@OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE}, 
			fetch=FetchType.LAZY, mappedBy="house")
	private Collection<Door> doors=new ArrayList<Door>();

	@OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE}, 
			fetch=FetchType.LAZY, mappedBy="house")
	private Collection<Resident> residents=new ArrayList<Resident>();
	
	@OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE}, 
			fetch=FetchType.LAZY, mappedBy="house")
	private Collection<Mortgage> mortgages=new ArrayList<Mortgage>();
	
	public House() {}
	public House(int id) { this.id=id; }
	
	public int getId() { return id; }
	
	public Collection<Room> getRooms() { return rooms; }
	public Collection<Door> getDoors() { return doors; }
	public Collection<Resident> getResidents() { return residents; }
	public Collection<Mortgage> getMortgages() { return mortgages; }
}
