package info.ejava.examples.ejb.tx.ejb;

import javax.annotation.PostConstruct;

import javax.annotation.PreDestroy;
import javax.ejb.AfterBegin;
import javax.ejb.AfterCompletion;
import javax.ejb.BeforeCompletion;
import javax.ejb.Stateful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The sole purpose of this EJB is to enlist is to print the status of the 
 * transaction after being enlisted in the transaction by the caller.
 */
@Stateful
public class TxWatcherEJB {
    private Logger logger = LoggerFactory.getLogger(TxWatcherEJB.class);
    
    private String beanName;
    private String txName;
    
    @PostConstruct
    public void init() {
        beanName = getClass().getSimpleName() + ":" + super.hashCode();
        logger.debug("*** {}:init({}) ***", beanName, super.hashCode());
    }
    @PreDestroy
    public void destroy() {
        logger.debug("*** {}:destroy({}) ***", beanName, super.hashCode());
    }
    
    /**
     * By calling this (or any other business method, the sessionEJB will be created
     * and enlisted in the current transaction
     */
    public void watchTransaction(Class<?> clazz, int hashCode) {
        String txName = clazz.getSimpleName() + ":" + hashCode;
        if (this.txName==null || !this.txName.equals(txName)) {
            logger = LoggerFactory.getLogger(clazz);
            logger.debug("watcher EJB {} enlisted in transaction for {}", beanName, txName);
            this.txName = txName;
        } else {
            logger.debug("transaction continued for {}", this.txName);
        }
    }

    @AfterBegin
    public void afterBegin() {
        logger.debug("transaction for {} has started", beanName);
    }
    
    @BeforeCompletion
    public void beforeCompletion() {
        logger.debug("transaction for {} is committing", beanName);
    }
    
    @AfterCompletion
    public void afterCompletion(boolean committed) {
        logger.debug("transaction committed for {}, commited={}", beanName, committed);
    }
}
