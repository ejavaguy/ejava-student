package ejava.jpa.example.validation;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * This class provides an example of several of the pre-defined constraints
 * supplied within the validation API
 */
@Entity
@Table(name="VALIDATION_PERSON")
public class Person {
	@Id @GeneratedValue
	private int id;
	
	@Column(name="FIRST_NAME", length=12, nullable=false)
	//@NotNull
	//@Size(min=1,max=12)
	//@Pattern(regexp="^[a-zA-Z\\ \\-]+$", message="invalid characters in name")
	@ValidName(min=1, max=12, regexp="^[a-zA-Z\\ \\-]+$", message="invalid first name")
	private String firstName;
	
	@Column(name="LAST_NAME", length=20, nullable=false)
	//@NotNull
	//@Size(min=1,max=20)
	//@Pattern(regexp="^[a-zA-Z\\ \\-]+$", message="invalid characters in name")
	@ValidName(min=1, max=20, regexp="^[a-zA-Z\\ \\-]+$", message="invalid last name")
	private String lastName;
	
	@Temporal(TemporalType.DATE)
	@NotNull(groups={Drivers.class, POCs.class})
	@Past(groups=Drivers.class)
	@MinAge.List({
		@MinAge(age=18, groups=POCs.class),
		@MinAge(age=16, groups=Drivers.class)
	})
	private Date birthDate;
	
	@Column(name="EMAIL", length=50)
	@NotNull(groups=POCs.class)
	@Size(min=7,max=50)
	@Pattern(regexp="^.+@.+\\..+$")
	private String email;
	
	public int getId() { return id; }
	
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

	public String getEmail() { return email; }
	public Person setEmail(String email) {
		this.email = email;
		return this;
	}

	@Override
	public String toString() {
		DateFormat df = new SimpleDateFormat("YYYY");
		return (firstName==null ? "null" : firstName) + 
			   (lastName==null ?  ", null" : ", " + lastName) + 
			   (birthDate==null ? ", null dob" : ", " + df.format(birthDate)) +
			   (email==null ?  ", null email" : ", " + email) 
			   ;
	}	
}
