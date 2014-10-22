package org.myorg.encconfig.ejb;

import javax.ejb.Remote;

@Remote
public interface SampleRemote {
    String whoAreYou();
}
