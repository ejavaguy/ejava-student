package org.myorg.encconfig.ejb;

import javax.ejb.Remote;

@Remote
public interface ConfigBeanRemote {
    boolean haveNoIfaceEJB();
    boolean haveLocalEJB();
    boolean haveRemoteEJB();
}
