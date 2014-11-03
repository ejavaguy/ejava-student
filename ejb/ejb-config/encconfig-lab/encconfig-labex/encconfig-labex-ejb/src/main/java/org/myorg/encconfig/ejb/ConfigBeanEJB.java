package org.myorg.encconfig.ejb;

import javax.ejb.EJB;
import javax.ejb.Stateless;

//TODO: enc-config 30
//@Stateless
public class ConfigBeanEJB implements ConfigBeanRemote {
    //TODO: enc-config 31
    //@EJB
    private SampleNoIfaceEJB noIface;
    
    //TODO: enc-config 33
    @EJB//(beanName="Choice1EJB")
    private SampleLocal localEJB;
    
    //TODO: enc-config 35
    @EJB//(lookup="java:module/Choice2EJB!org.myorg.encconfig.ejb.SampleRemote")
    private SampleRemote remoteEJB;
    
    @Override
    public boolean haveNoIfaceEJB() {
        return noIface!=null;
    }
    
    @Override
    public boolean haveLocalEJB() {
        return localEJB!=null && "Choice1EJB".equals(localEJB.whoAreYou()); 
    }
    
    @Override
    public boolean haveRemoteEJB() {
        return remoteEJB!=null && "Choice2EJB".equals(remoteEJB.whoAreYou());
    }
}
