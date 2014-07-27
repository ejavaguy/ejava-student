package ejava.examples.orm.inheritance.annotated;

import javax.persistence.*;

/**
 * This class provides an example of an entity sub-class using separate
 * classes per-concrete derived class. This means that the base class
 * properties will get merged into this table (similar to non-entity 
 * inheritance).
 */
@Entity
@Table(name="ORMINH_INTERESTACCT")
public class InterestAccount extends Account {
    private double rate;
    
    public InterestAccount() {}
    public InterestAccount(long id) { super(id); }

    public void withdraw(double amount) throws AccountException {
        super.setBalance(super.getBalance() - amount);
    }
    
    public void processInterest() {
        super.setBalance(super.getBalance() * (1 + rate)); 
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", rate=" + rate);
        return text.toString();
    }
}
