package ejava.examples.jndidemo.ejb;

import javax.persistence.*;

@Entity @Table(name="JNDIDEMO_RESERVATION")
public class Reservation {
    private long id;
    private String name;
    
    @Id
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
