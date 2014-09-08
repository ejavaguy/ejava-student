package myorg.entityex.annotated;

import javax.persistence.Embeddable;

@Embeddable
public class Street {
	private int number;
	private String name;
	
	public int getNumber() { return number; }
	public Street setNumber(int number) { this.number = number; return this; }
	
	public String getName() { return name; }
	public Street setName(String name) { this.name = name; return this; }
}