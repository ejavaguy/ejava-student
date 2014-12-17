package info.ejava.examples.ejb.interceptor.interceptors;

import javax.interceptor.Interceptor;

import info.ejava.examples.ejb.interceptor.bo.PostNormalizedCheck;

@Validation
@Interceptor
public class PostNormizedInterceptor extends ValidatorInterceptor {
    public PostNormizedInterceptor() {
        super(new Class<?>[]{PostNormalizedCheck.class});
    }
}
