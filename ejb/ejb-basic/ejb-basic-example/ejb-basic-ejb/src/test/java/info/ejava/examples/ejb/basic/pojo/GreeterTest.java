package info.ejava.examples.ejb.basic.pojo;

import static org.junit.Assert.*;

import info.ejava.examples.ejb.basic.dto.Greeting;
import info.ejava.examples.ejb.basic.dto.Name;
import info.ejava.examples.ejb.basic.ejb.BadRequestException;
import info.ejava.examples.ejb.basic.ejb.Greeter;
import info.ejava.examples.ejb.basic.ejb.GreeterEJB;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreeterTest {
    private static final Logger logger = LoggerFactory.getLogger(GreeterTest.class);
    private Greeter greeter;

    @Before
    public void setUp() {
        greeter = new GreeterEJB();
    }

    @Test
    public void pojoGreeter() throws BadRequestException {
        logger.info("*** pojoGreeter ***");
        String name = "cat inhat";
        String greeting = greeter.sayHello(name);
        assertTrue("greeter did not say my name", greeting.contains(name));
    }

    @Test(expected = BadRequestException.class)
    public void badName() throws BadRequestException {
        logger.info("*** badName ***");
        greeter.sayHello("");
    }

    @Test
    public void dto() throws BadRequestException {
        logger.info("*** dto ***");
        Name name = new Name("thing", "one");
        Greeting greeting = greeter.sayHello(name);
        assertTrue("greeter did not say my name", greeting.getGreeting()
                .contains(name.getFirstName()));
    }
}
