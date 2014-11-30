package org.myorg.jpatickets.ejb;

import javax.ejb.Remote;

@Remote
public interface TicketsInitRemote {
    void resetDB();
}
