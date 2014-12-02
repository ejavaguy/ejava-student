package org.myorg.jpatickets.ejb;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value=3, unit=TimeUnit.SECONDS)
public class TicketsInitEJB implements TicketsInitRemote {
    private static final Logger logger = LoggerFactory.getLogger(TicketsInitEJB.class);
    
    @EJB
    private TicketsInitTxEJB txHelper;
    
    @PostConstruct
    public void init() {
        resetDB();
    }
    
    @Override
    @Lock(LockType.WRITE)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void resetDB() {
        logger.info("*** resetDB ***");
        //execute the following in separate transactions
        txHelper.dropDB();
        txHelper.createDB();
    }
    
}
