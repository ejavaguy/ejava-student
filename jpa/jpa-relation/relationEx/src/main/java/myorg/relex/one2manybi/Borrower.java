package myorg.relex.one2manybi;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
/**
 * This class provides an example of the one/parent side of a one-to-many, bi-directional relationship
 * that will be realized through a foreign key from the many/child side of the relationship. Being the 
 * one side of the one-to-many relationship, this class must implement the inverse side.
 */
@Entity
@Table(name="RELATIONEX_BORROWER")
public class Borrower {
    @Id @GeneratedValue
    private int id;
    
    @OneToMany(
    		mappedBy="borrower"
    		, cascade={CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REMOVE}
    		, orphanRemoval=true
    		, fetch=FetchType.EAGER
    		)
    private List<Loan> loans;
    
    @Column(length=12)
    private String name;

	public int getId() { return id; }

	public List<Loan> getLoans() {
		if (loans == null) {
			loans = new ArrayList<Loan>();
		}
		return loans;
	}

	public void setLoans(List<Loan> loans) {
		this.loans = loans;
	}

	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
}
