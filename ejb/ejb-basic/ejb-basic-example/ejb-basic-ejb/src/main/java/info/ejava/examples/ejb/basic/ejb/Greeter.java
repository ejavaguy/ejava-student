package info.ejava.examples.ejb.basic.ejb;

import info.ejava.examples.ejb.basic.dto.Greeting;
import info.ejava.examples.ejb.basic.dto.Name;

public interface Greeter {
    String sayHello(String name) throws BadRequestException;

    Greeting sayHello(Name name) throws BadRequestException;
}
