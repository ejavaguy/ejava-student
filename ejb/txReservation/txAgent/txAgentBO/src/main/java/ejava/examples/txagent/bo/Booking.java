package ejava.examples.txagent.bo;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ejava.examples.txhotel.bo.Reservation;

@SuppressWarnings("serial")
public class Booking implements Serializable {
    private long id;
    private long version;
    private String confirmation;
    private Set<String> hotelConfirmations = new HashSet<String>();
    private Set<Reservation> hotelReservations = new HashSet<Reservation>();
    
    public Booking() {}
    public Booking(
            long id, long version, String confirmation) {
        setId(id);
        setVersion(version);
        setConfirmation(confirmation);
    }
    
    public long getId() {
        return id;
    }
    private void setId(long id) {
        this.id = id;
    }
    public String getConfirmation() {
        return confirmation;
    }
    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }
    public Set<String> getHotelConfirmations() {
        return Collections.unmodifiableSet(hotelConfirmations);
    }
    @SuppressWarnings("unused")
    private void setHotelConfirmations(Set<String> hotelConfirmations) {
        this.hotelConfirmations = hotelConfirmations;
    }
    public Set<Reservation> getHotelReservations() {
        return Collections.unmodifiableSet(hotelReservations);
    }
    @SuppressWarnings("unused")
    private void setHotelReservations(Set<Reservation> hotelReservations) {
        this.hotelReservations = hotelReservations;
    }
    public void addHotelReservation(Reservation reservation) {
    	if (!hotelConfirmations.contains(reservation.getConfirmation())) {
	        this.hotelConfirmations.add(reservation.getConfirmation());
	        this.hotelReservations.add(reservation);
    	}
    }
    //hibernate wouldn't persist ArrayList<String> as @Lob
    @SuppressWarnings("unused")
    private byte[] getHotelConfirmationsAsBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(hotelConfirmations);
        return bos.toByteArray();        
    }
    @SuppressWarnings({ "unchecked", "unused" })
    private void setHotelConfirmationsAsBytes(byte data[]) 
        throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis);
        hotelConfirmations = (Set<String>)ois.readObject();
    }
    
    
    @Override
	public boolean equals(Object obj) {
		if (this==obj) return true;
		if (obj!= null && obj instanceof Booking) {
			return confirmation.equals(((Booking)obj).confirmation);
		}
		return false;
	}
    
	@Override
	public int hashCode() {
		return confirmation.hashCode();
	}

	public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", version=" + version);
        text.append(", conf#" + confirmation);
        text.append(", hotel confirmations=" + hotelConfirmations);
        text.append(", hotel reservations=" + hotelReservations);
        return text.toString();
    }
}