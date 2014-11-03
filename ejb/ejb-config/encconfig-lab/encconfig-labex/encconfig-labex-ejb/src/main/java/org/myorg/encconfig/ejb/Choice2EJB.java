package org.myorg.encconfig.ejb;

import javax.ejb.Stateless;

@Stateless
//TODO: enc-config 32
public class Choice2EJB implements /*SampleLocal, */ SampleRemote {
    public String whoAreYou() {
        return "Choice2EJB";
    }
}
