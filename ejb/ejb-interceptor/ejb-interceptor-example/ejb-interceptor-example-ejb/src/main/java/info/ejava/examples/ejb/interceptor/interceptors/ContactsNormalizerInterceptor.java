package info.ejava.examples.ejb.interceptor.interceptors;

import info.ejava.examples.ejb.interceptor.bo.Contact;
import info.ejava.examples.ejb.interceptor.normalizer.ContactNormalizer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContactsNormalizerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ContactsNormalizerInterceptor.class);
    private ContactNormalizer contactNormalizer=new ContactNormalizer();

    @PostConstruct
    public void init() {
        logger.debug("{}:init({})", getClass().getSimpleName(), super.hashCode());
    }
    @PreDestroy
    public void destroy() {
        logger.debug("{}:destroy({})", getClass().getSimpleName(), super.hashCode());
    }
    
    @AroundInvoke
    public Object invoke(InvocationContext ctx) throws Exception {
        logger.debug("intercepting: {}", ctx.getMethod());
        try {
            for (Object param: ctx.getParameters()) {
                if (param instanceof Contact) {
                    logger.debug("normalizing: {}", param);
                    contactNormalizer.normalize((Contact) param);
                    logger.debug("normalized: {}", param);
                }
            }
            return ctx.proceed();
        } finally {
            
        }
    }
}
