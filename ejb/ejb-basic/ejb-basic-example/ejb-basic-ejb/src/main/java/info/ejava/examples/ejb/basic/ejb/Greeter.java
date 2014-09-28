package info.ejava.examples.ejb.basic.ejb;

public interface Greeter {
	String sayHello(String name) throws BadNameException;
	
	Greeting sayHello(Name name) throws BadNameException;
}
