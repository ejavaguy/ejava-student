package info.ejava.examples.ejb.interceptor.interceptors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.interceptor.AroundConstruct;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Interceptor //needed for @AroundConstruct and @PostConstruct non-business method interceptors
public class LifecycleInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LifecycleInterceptor.class);
    
    @Resource
    private EJBContext ejbCtx;
    
    @AroundConstruct
    public void ctor(InvocationContext ctx) {
        try {
            logger.debug("*** Contructor: {}, ejbCtx={}", ctx.getConstructor(), ejbCtx);
            ctx.proceed();
        } catch (Exception ex) {
            throw new RuntimeException("error calling post construct", ex);
        }
    }

    @PostConstruct
    public void init(InvocationContext ctx) {
        logger.debug("*** Lifecycle event: {}::INIT", ctx.getTarget());
        try {
            ctx.proceed();
        } catch (Exception ex) {
            throw new RuntimeException("error calling post construct", ex);
        }
    }
    
    @AroundInvoke
    public Object invoke(InvocationContext ctx) throws Exception {
        logger.debug("*** Business Method: {}, caller={}", ctx.getMethod(), ejbCtx.getCallerPrincipal().getName());
        Object response = ctx.proceed();
        logger.debug("*** Response Object: {}", response);
        return response;
    }
    
    @AroundTimeout
    public Object timeout(InvocationContext ctx) throws Exception {
        logger.debug("*** Timeout: {}", ctx.getTimer());
        Object response = ctx.proceed();
        return response;
    }

    @PreDestroy
    public void destory(InvocationContext ctx) {
        logger.debug("*** Lifecycle event: {}::DESTROY", ctx.getTarget());
        try {
            ctx.proceed();
        } catch (Exception ex) {
            throw new RuntimeException("error calling pre destroy", ex);
        }
    }
}
