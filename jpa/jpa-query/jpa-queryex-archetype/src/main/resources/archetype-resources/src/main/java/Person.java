#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="QUERYEX_PERSON")
public class Person {
	@Id 
	@Column(name="ID", length=36)
	private String id;
	
	@Column(name="FIRST_NAME", length=16)
	private String firstName;
	
	@Column(name="LAST_NAME", length=16)
	private String lastName;
	
	@Temporal(TemporalType.DATE)
	@Column(name="BIRTH_DATE")
	private Date birthDate;
	
	protected Person() {}
	public Person(String id) { this.id = id; }
	
	public String getId() {
		return id;
	}
	public String getFirstName() { return firstName; }
	public Person setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}
	
	public String getLastName() { return lastName; }
	public Person setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}
	
	public Date getBirthDate() { return birthDate; }
	public Person setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
		return this;
	}

	@Override
	public int hashCode() {
		return (firstName==null?0:firstName.hashCode()) + 
				(lastName==null?0:lastName.hashCode()) +
				(birthDate==null?0:birthDate.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if (this == obj) { return true; }
			if (obj==null) { return false; }
			Person rhs = (Person)obj;
			if (firstName!=null && lastName!=null && birthDate!=null) {
				return firstName.equals(rhs.firstName) && 
						lastName.equals(rhs.lastName) &&
						birthDate.equals(rhs.birthDate) &&
						id==null?true:id.equals(rhs.id);
			}
			if ((firstName==null && rhs.firstName!=null) ||
					(lastName==null && rhs.lastName!=null) ||
					(birthDate==null && rhs.birthDate!=null)) { 
				return false; 
			}
			return id==null?true:id.equals(rhs.id);
		} catch (Exception ex) { return false; }
	}
	
	@Override
	public String toString() {
		return firstName + " " + lastName;
	}
	
}
