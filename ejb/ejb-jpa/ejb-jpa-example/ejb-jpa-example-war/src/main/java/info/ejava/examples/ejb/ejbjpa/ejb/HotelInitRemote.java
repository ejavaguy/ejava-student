package info.ejava.examples.ejb.ejbjpa.ejb;

import javax.ejb.Remote;

@Remote
public interface HotelInitRemote {
    void clearAll();
    void populate();
}
