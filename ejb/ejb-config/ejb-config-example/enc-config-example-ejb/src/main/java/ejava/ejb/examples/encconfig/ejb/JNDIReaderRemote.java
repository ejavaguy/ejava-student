package ejava.ejb.examples.encconfig.ejb;

import javax.ejb.Remote;

@Remote
public interface JNDIReaderRemote {
	String lookupJNDI(String name);
	String lookupSessionContext(String name);
	/**
	 * Returns either a List<NameClassPair> or a String with object at name
	 * when name is not a context
	 */
	Object listJNDI(String name);
	Object listSessionContext(String name);
}
