package org.myorg.basicejb.ejb;

import javax.ejb.Remote;

@Remote
public interface ReservationRemote {
    void ping();
}
