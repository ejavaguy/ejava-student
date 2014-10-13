package ejava.ejb.examples.encconfig.ejb.it;

public class XMLConfiguredEJBIT extends JNDIVerifierITBase {
	private static final String ejbJNDI = System.getProperty("jndi.name",
			"ejb:/enc-config-example-ejb/XMLConfiguredEJB!ejava.ejb.examples.encconfig.ejb.JNDIReaderRemote");
	
	public XMLConfiguredEJBIT() {
		super(ejbJNDI);
	}
}
