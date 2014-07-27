package ejava.examples.orm.ejbql.annotated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.*;


@Entity @Table(name="ORMQL_CLERK")
public class Clerk {
    private long id;
    private String firstName;
    private String lastName;
    private Date hireDate;
    private Date termDate;
    private Collection<Sale> sales = new ArrayList<Sale>();

    @Id @GeneratedValue @Column(name="CLERK_ID")
    public long getId() {
        return id;
    }
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }
    @ManyToMany(mappedBy="clerks", fetch=FetchType.LAZY)
    public Collection<Sale> getSales() {
        return sales;
    }
    public void setSales(Collection<Sale> sales) {
        this.sales = sales;
    }
    
    @Temporal(TemporalType.DATE)
    public Date getHireDate() {
        return hireDate;
    }
    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }
    @Temporal(TemporalType.DATE)
    public Date getTermDate() {
        return termDate;
    }
    public void setTermDate(Date termDate) {
        this.termDate = termDate;
    }    

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }    
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", firstName=" + firstName);
        text.append(", lastName=" + lastName);
        text.append(", hireDate=" + hireDate);
        text.append(", termDate=" + termDate);
        text.append(", sales(" + sales.size() + ")={");
        for(Sale s : sales) {
            text.append(s.getId() + ", ");
        }
        text.append("}");
        return text.toString();
    }
}
