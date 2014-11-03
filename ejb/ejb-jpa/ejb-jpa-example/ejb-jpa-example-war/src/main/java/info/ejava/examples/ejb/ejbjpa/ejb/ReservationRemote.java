package info.ejava.examples.ejb.ejbjpa.ejb;

import java.util.List;

import info.ejava.examples.ejb.ejbjpa.bl.RoomUnavailableExcepton;
import info.ejava.examples.ejb.ejbjpa.bo.Guest;

import javax.ejb.Remote;

@Remote
public interface ReservationRemote {

    int addGuest(Guest guest);

    List<Guest> reserveRooms() throws RoomUnavailableExcepton;

    List<Guest> reserveRoomsPessimistic() throws RoomUnavailableExcepton;

}
