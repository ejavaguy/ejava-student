package info.ejava.examples.ejb.interceptor.interceptors;

import info.ejava.examples.ejb.interceptor.bo.PostNormalizedCheck;

public class PostNormizedInterceptor extends ValidatorInterceptor {
    public PostNormizedInterceptor() {
        super(new Class<?>[]{PostNormalizedCheck.class});
    }
}
