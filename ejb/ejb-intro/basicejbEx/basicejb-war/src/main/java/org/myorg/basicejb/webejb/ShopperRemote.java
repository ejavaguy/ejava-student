package org.myorg.basicejb.webejb;

import javax.ejb.Remote;

@Remote
public interface ShopperRemote {
    int ping();
    void close();
}
