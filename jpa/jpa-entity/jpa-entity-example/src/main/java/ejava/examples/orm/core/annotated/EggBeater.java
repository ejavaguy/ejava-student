package ejava.examples.orm.core.annotated;

import javax.persistence.*;

/**
 * This class provides an example of using a TABLE GeneratedValue schema. The
 * definition of the primary key generation is within the annotations supplied
 * in this class.
 */
@Entity
@Table(name="ORMCORE_EGGBEATER")
@TableGenerator(  //note that all but name are optional if generating schema
        name="eggbeaterGenerator",     //logical name of generator
        table="ORMCORE_EB_UID",        //name of table storing seq
        pkColumnName="UID_ID",         //pk column for seq table
        pkColumnValue="ORMCORE_EGGBEATER",  //pk value in pk column
        valueColumnName="UID_VAL",     //column for seq value
        allocationSize=5              //increment UID_ID after using this many
    )        
public class EggBeater {
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, //use DB table 
            generator="eggbeaterGenerator")        //point to logical def
    private long id;
    private String make;    
    
    public EggBeater() {}
    public EggBeater(long id) { this.id = id; }
    
    public long getId() { return id; }

    public String getMake() { return make; }
    public void setMake(String make) {
        this.make = make;
    }

    @Override
    public String toString() {
            return new StringBuilder()
                      .append(super.toString())	       
                      .append(", id=").append(id)
                      .append(", make=").append(make)
                      .toString();
    }    
}
