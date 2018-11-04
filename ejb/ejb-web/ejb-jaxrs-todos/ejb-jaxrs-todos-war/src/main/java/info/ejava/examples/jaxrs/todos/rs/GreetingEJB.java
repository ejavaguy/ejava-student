package info.ejava.examples.jaxrs.todos.rs;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import info.ejava.examples.jaxrs.todos.ejb.InternalErrorException;
import info.ejava.examples.jaxrs.todos.ejb.InvalidRequestException;

@Stateless
public class GreetingEJB {
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String greet(String name) throws InvalidRequestException {
        try {
            if (name==null || name.isEmpty()) {
                throw new InvalidRequestException("Unable to greet, name not supplied");
            }
            
            return String.format("hello %s", name);  //core business code
            
        } catch (RuntimeException ex) {
            throw new InternalErrorException("Internal error greeting name[%s]", name);
        }
    }
}
