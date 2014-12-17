package info.ejava.examples.ejb.interceptor.interceptors;

import info.ejava.examples.ejb.interceptor.ejb.InvalidParam;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidatorInterceptor {
    private static Logger logger = LoggerFactory.getLogger(ValidatorInterceptor.class);
    @Inject
    private Validator validator;
    
    private Class<?>[] groups;
    
    protected ValidatorInterceptor() {}
    public ValidatorInterceptor(Class<?>[] groups) {
        this.groups = groups;
    }
    
    @PostConstruct
    public void init() {
        logger.debug("{}:init({}, groups={})", getClass().getSimpleName(), super.hashCode(), Arrays.toString(groups));
    }
    @PreDestroy
    public void destroy() {
        logger.debug("{}:destroy({})", getClass().getSimpleName(), super.hashCode());
    }
    
    @AroundInvoke
    public Object invoke(InvocationContext ctx) throws Exception {
        logger.debug("validating method: {}, groups: {}", ctx.getMethod(), Arrays.toString(groups));
        try {
            //validate each parameter
            for (Object param: ctx.getParameters()) {
                logger.debug("validating param: {}, groups: {}", param, Arrays.toString(groups));
                Set<ConstraintViolation<Object>> violations = validator.validate(param, groups);
                if (!violations.isEmpty()) {
                    Exception ex = new InvalidParam(param.toString(), getErrors(violations));
                    logger.debug("aborting call, found error: {}", ex.getMessage());
                    throw ex;
                }
            }
            return ctx.proceed();
        } finally {
            
        }
    }
    
    private List<String> getErrors(Set<ConstraintViolation<Object>> violations) {
        List<String> errors = new ArrayList<String>(violations.size());
        for (ConstraintViolation<Object> v: violations) {
            errors.add(v.toString());
        }
        return errors;
    }
}
