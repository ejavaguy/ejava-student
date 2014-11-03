package ejava.examples.secureping.ejbclient;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.log4j.Logger;

/**
 * This class implements a very simple username/password callback handler
 * for the JAAS framework. The values used for the login are provided to
 * the handler and returned as requested. Internal storage of the security
 * values are not protected by the class in any way. This only provides a
 * basic functionality handler for the JAAS login.
 */
public class BasicCallbackHandler implements CallbackHandler {
	Logger log_ = Logger.getLogger(BasicCallbackHandler.class);
    private String name_;
    private char[] password_;

    public BasicCallbackHandler() {}
    public BasicCallbackHandler(String name, char[] password) {
        name_ = name;
        setPassword(password);
    }
    public BasicCallbackHandler(String name, String password) {
        name_ = name;
        setPassword(password);
    }

    public void setName(String name) { name_ = name; }
    public void setPassword(String password) {
        password_ = new char[password.length()];
        password.getChars(0,password_.length, password_,0);
    }
    public void setPassword(char[] password) {
        password_ = new char[password.length];
        System.arraycopy(password,0,password_,0,password_.length);
    }

    public void handle(Callback[] callbacks) 
        throws UnsupportedCallbackException {

        for (Callback cb : callbacks) {
            if (cb instanceof NameCallback) {
                log_.debug("name callback:" + name_);
                ((NameCallback)cb).setName(name_);                
            }
            else if (cb instanceof PasswordCallback) {
                log_.debug("password callback:" + new String(password_));
                ((PasswordCallback)cb).setPassword(password_);
            }
            else {
                log_.debug("unknown callback");
                throw new UnsupportedCallbackException(cb);
            }
        }
    }

    /** returns the <bold><underline>clear text</underline><bold> values of 
     *  the username/password 
     */
    public String toString() {
        StringBuffer text = new StringBuffer();
        if (name_ != null) { 
            text.append(name_); 
        }
        else {
            text.append("(null)");
        }

        text.append('/');

        if (password_ != null) {
            for(int i=0; i<password_.length; i++) {
                text.append(password_[i]);
            }
        }
        else {
            text.append("(null");
        }

        return text.toString();
    }
}
