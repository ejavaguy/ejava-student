package info.ejava.examples.ejb.cdisales.web;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@SuppressWarnings("serial")
@Named("errorController")
@SessionScoped
public class ErrorController implements Serializable {

    private String error;
    private Exception exception;
        
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
    
    public String getStackTrace() {
        if (exception != null) {
            StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw));
            return sw.toString();
        } else {
            return null;
        }
    }
    
    public Exception getException() {
        return exception;
    }
    public void setException(Exception exception) {
        this.exception = exception;
    }
}
