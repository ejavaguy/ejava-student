package org.myorg.basicejb.webejb;

import javax.annotation.PostConstruct;

import javax.annotation.PreDestroy;
import javax.ejb.Stateful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateful
public class ShopperEJB implements ShopperRemote {
    private static Logger logger = LoggerFactory.getLogger(ShopperEJB.class);
    
    //we can only track conversation state here if we are stateful
    private int counter=0;

    @PostConstruct
    public void init() {
        logger.debug("*** ShopperEJB({}).init() ***", super.hashCode());
    }
    
    @PreDestroy
    public void destroy() {
        logger.debug("*** ShopperEJB({}).destroy() ***", super.hashCode());
    }
    
    @Override
    public int ping() {
        logger.debug("ping({}) called, returned {}", super.hashCode(), counter);
        return counter++;
    }
}
