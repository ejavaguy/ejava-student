package myorg.entityex.annotated;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CowPK implements Serializable { //required to be Serializable
	private static final long serialVersionUID = 1L;
	@Column(name="HERD", length=16)
	private String herd;
    private String name;
    
    public CowPK(){} //required default ctor
	public CowPK(String herd, String name) {
		this.herd = herd;
		this.name = name;
	};
	
	public String getHerd() { return herd; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	@Override
	public int hashCode() { //required hashCode method
		return herd.hashCode() + name.hashCode();
	} 
	
	@Override
	public boolean equals(Object obj) { //required equals method
		try {
			return herd.equals(((CowPK)obj).herd) && 
					name.equals(((CowPK)obj).name);
		} catch (Exception ex) {
			return false;
		}
	}
    
}
