package ejava.ejb.examples.encconfig.ejb.it;

public class AnnotatedEJBIT extends JNDIVerifierITBase {
	private static final String ejbJNDI = System.getProperty("jndi.name",
			"ejb:/enc-config-example-ejb/AnnotatedEJB!ejava.ejb.examples.encconfig.ejb.JNDIReaderRemote");
	public AnnotatedEJBIT() {
		super(ejbJNDI);
	}
}
