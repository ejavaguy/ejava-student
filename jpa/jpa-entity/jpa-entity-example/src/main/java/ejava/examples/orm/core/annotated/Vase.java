package ejava.examples.orm.core.annotated;

import java.util.Date;

import javax.persistence.*;

import ejava.examples.orm.core.ColorType;

/**
 * This class provides an example of mapping various types to the database,
 * like dates, enums, etc.
 */
@Entity
@Table(name="ORMCORE_VASE")
public class Vase {
    @Id
    private long id;
    @Temporal(TemporalType.DATE)
    private Date aDate;
    @Temporal(TemporalType.TIME)
    private Date aTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Date aTimestamp;
    @Enumerated(EnumType.ORDINAL)
    private ColorType colorId;
    @Enumerated(EnumType.STRING)
    private ColorType colorName;

    public Vase() {}
    public Vase(long id) { this.id = id; }

    public long getId() { return id; }
    
    public Date getADate() { return aDate; }
    public void setADate(Date date) {
        aDate = date;
    }
    
    public Date getATime() { return aTime; }
    public void setATime(Date time) {
        aTime = time;
    }
    
    public Date getATimestamp() { return aTimestamp; }
    public void setATimestamp(Date timestamp) {
        aTimestamp = timestamp;
    }
    
    public ColorType getColorId() { return colorId; }
    public void setColorId(ColorType colorId) {
        this.colorId = colorId;
    }

    public ColorType getColorName() { return colorName; }
    public void setColorName(ColorType colorName) {
        this.colorName = colorName;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString())
           .append(", id=").append(id)
           .append(", aDate=").append(aDate)
               .append(", aTime=").append(aTime)
               .append(", aTimestamp=").append(aTimestamp)
               .append(", colorId=").append(colorId)
               .append(", colorName=").append(colorName);
        return builder.toString();
    }
}

