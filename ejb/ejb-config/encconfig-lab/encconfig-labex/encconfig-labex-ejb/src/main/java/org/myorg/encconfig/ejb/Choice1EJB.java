package org.myorg.encconfig.ejb;

import javax.ejb.Stateless;

@Stateless
//TODO: enc-config 34
public class Choice1EJB implements SampleLocal /*, SampleRemote */ {
    public String whoAreYou() {
        return "Choice1EJB";
    }
}
