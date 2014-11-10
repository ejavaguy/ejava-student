package info.ejava.examples.securepingclient.ejbclient;

import java.io.IOException;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;

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
    private String name;
    private char[] password;
    private String realm="ApplicationRealm";
    private static CallbackHandler login;

    public BasicCallbackHandler() {}
    public BasicCallbackHandler(String name, char[] password) {
        this.name = name;
        setPassword(password);
    }
    public BasicCallbackHandler(String name, String password) {
        this.name = name;
        setPassword(password);
    }

    public void setName(String name) { this.name = name; }
    public void setPassword(String password) {
        this.password = new char[password.length()];
        password.getChars(0,this.password.length,this.password,0);
    }
    public void setPassword(char[] password) {
        this.password = new char[password.length];
        System.arraycopy(password,0,this.password,0,this.password.length);
    }
    
    public String getRealm() { return realm; }
    public void setRealm(String realm) {
        this.realm = realm;
    }
    
    public void handle(Callback[] callbacks) 
        throws UnsupportedCallbackException, IOException {
        if (login!=null && login!=this) {
            login.handle(callbacks);
            return;
        }

        for (Callback cb : callbacks) {
            if (cb instanceof NameCallback) {
                log_.debug("name callback:" + name);
                ((NameCallback)cb).setName(name);                
            }
            else if (cb instanceof PasswordCallback) {
                log_.debug("password callback:" + new String(password));
                ((PasswordCallback)cb).setPassword(password);
            }
            else if (cb instanceof RealmCallback) {
                log_.debug("realm callback:" + realm);
                ((RealmCallback)cb).setText(realm);
            }
            else {
                log_.debug("unknown callback:" + cb);
                throw new UnsupportedCallbackException(cb);
            }
        }
    }

    /** returns the <bold><underline>clear text</underline><bold> values of 
     *  the username/password 
     */
    public String toString() {
        StringBuffer text = new StringBuffer();
        if (name != null) { 
            text.append(name); 
        }
        else {
            text.append("(null)");
        }

        text.append('/');

        if (password != null) {
            for(int i=0; i<password.length; i++) {
                text.append(password[i]);
            }
        }
        else {
            text.append("(null");
        }

        return text.toString();
    }
    
    public static void setLogin(CallbackHandler login) {
        BasicCallbackHandler.login=login;
    }
    public static CallbackHandler getLogin() {
        return login;
    }
}
