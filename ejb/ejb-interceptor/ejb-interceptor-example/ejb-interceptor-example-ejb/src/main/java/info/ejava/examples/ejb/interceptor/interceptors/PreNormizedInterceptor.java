package info.ejava.examples.ejb.interceptor.interceptors;

import info.ejava.examples.ejb.interceptor.bo.PreNormalizedCheck;

public class PreNormizedInterceptor extends ValidatorInterceptor {
    public PreNormizedInterceptor() {
        super(new Class<?>[]{PreNormalizedCheck.class});
    }
}
