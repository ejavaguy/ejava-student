package myorg.entityex.annotated;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name="ENTITYEX_HORSE")
public class Horse {
	public static class Jockey implements Serializable {
		private static final long serialVersionUID = 1L;
		private String name;
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
	}
	@Id @GeneratedValue
	private int id;
	private String name;
	@Lob
	private String description;
	@Lob
	private char[] history;
	@Lob
	private byte[] photo;
	@Lob
	private Jockey jockey;
	
	public int getId() { return id; }
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() { return description; }
	public void setDescription(String description) {
		this.description = description;
	}
	
	public char[] getHistory() { return history; }
	public void setHistory(char[] history) {
		this.history = history;
	}
	
	public byte[] getPhoto() { return photo; }
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	
	public Jockey getJockey() { return jockey; }
	public void setJockey(Jockey jockey) {
		this.jockey = jockey;
	}
}
