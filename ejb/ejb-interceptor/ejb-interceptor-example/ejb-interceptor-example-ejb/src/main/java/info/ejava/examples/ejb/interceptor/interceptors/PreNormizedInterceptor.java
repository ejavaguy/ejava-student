package info.ejava.examples.ejb.interceptor.interceptors;

import javax.interceptor.Interceptor;

import info.ejava.examples.ejb.interceptor.bo.PreNormalizedCheck;

@Validation
@Interceptor
public class PreNormizedInterceptor extends ValidatorInterceptor {
    public PreNormizedInterceptor() {
        super(new Class<?>[]{PreNormalizedCheck.class});
    }
}
