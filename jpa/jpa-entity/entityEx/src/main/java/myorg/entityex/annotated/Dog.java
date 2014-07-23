package myorg.entityex.annotated;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="ENTITYEX_DOG")
@Access(AccessType.FIELD)
public class Dog {
	public enum Sex {
		MALE, FEMALE
	}
	public enum Color {
		WHITE, BLACK, BROWN, MIX 
	}
	public enum Breed {
		LABRADOR("Lab"),
		SAINT_BERNARD("Saint Bernard");
		public final String prettyName;
		private Breed(String prettyName) { this.prettyName = prettyName; }
		
		public static Breed getBreed(String prettyName) {
			for (Breed breed : values()) {
				if (breed.prettyName.equals(prettyName)) {
					return breed;
				}
			}
			return null;
		}
	}
	
	@Id @GeneratedValue
	private int id;
	@Enumerated(EnumType.ORDINAL)
	private Sex gender;
	@Enumerated(EnumType.STRING)
	@Column(length=16)
	private Color color;
	@Transient
	private Breed breed;
	
	public int getId() { return id; }
	public void setId(int id) {
		this.id = id;
	}

	public Sex getGender() { return gender; }
	public Dog setGender(Sex gender) {
		this.gender = gender;
		return this;
	}

	public Color getColor() { return color; }
	public Dog setColor(Color color) {
		this.color = color;
		return this;
	}

	public Breed getBreed() {
		return breed;
	}
	public Dog setBreed(Breed breed) {
		this.breed = breed;
		return this;
	}
	
	@Access(AccessType.PROPERTY)
	@Column(name="BREED", length=32)
	protected String getDBBreed() {
		return breed==null ? null : breed.prettyName;
	}
	protected void setDBBreed(String dbValue) {
		breed=Breed.getBreed(dbValue);
	}
}
