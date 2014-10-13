package info.ejava.examples.ejb.basic.webejb;

import info.ejava.examples.ejb.basic.dto.Greeting;

import info.ejava.examples.ejb.basic.dto.Name;
import info.ejava.examples.ejb.basic.ejb.BadRequestException;
import info.ejava.examples.ejb.basic.ejb.GreeterRemote;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class WebGreeterEJB implements GreeterRemote {
    private static final Logger logger = LoggerFactory.getLogger(WebGreeterEJB.class);

    @PostConstruct
    public void init() {
        logger.info("*** WebGreeterEJB ***");
    }
    
    /**
     * This method is an example of a business method that can be invoked by
     * users of this object (if a POJO) or bean (if deployed as an EJB).
     */
    @Override
    public String sayHello(String name) throws BadRequestException {
        logger.debug("sayHello({})", name);

        if (name == null || name.isEmpty()) {
            throw new BadRequestException(
                    "you must have a name for me to say hello");
        }
        return "hello " + name;
    }

    /**
     * This method is an example of a business method that
     */
    @Override
    public Greeting sayHello(Name name) throws BadRequestException {
        logger.debug("sayHello({})", name);

        if (name == null) {
            throw new BadRequestException(
                    "you must have a name for me to say hello");
        }
        return new Greeting(new Date(), 
                "hello " + name.getFirstName() + " " + name.getLastName());
    }

}
