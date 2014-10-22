package org.myorg.encconfig.ejb;

import javax.ejb.Local;

@Local
public interface SampleLocal {
    String whoAreYou();
}
