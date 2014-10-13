package ejava.ejb.examples.encconfig.ejb.it;

import java.util.Enumeration;
import java.util.List;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingException;

import ejava.ejb.examples.encconfig.dto.NCPair;
import ejava.ejb.examples.encconfig.ejb.JNDIReaderRemote;

/**
 * This class is used to query for JNDI information.
 */
public class JNDIHelper {
	
	public static StringBuilder listJNDI(
			Context jndi, StringBuilder text, String name, Object object) 
			throws NamingException {
		if (object==null) {
			listJNDI(jndi, text, name, jndi.lookup(name));
		} else if (object instanceof Context) {
			for (Enumeration<NameClassPair> e=jndi.list(name);e.hasMoreElements();) {
				NameClassPair ncp = e.nextElement();
				String n = name + "/" + ncp.getName();
				listJNDI(jndi, text, n, jndi.lookup(n));
			}
		} else {
			text.append(String.format("%s:%s\n", name, object));
		}
		return text;
	}

	public static StringBuilder listJNDI(
			JNDIReaderRemote reader, StringBuilder text, String name, Object object) 
			throws NamingException {
		if (object instanceof String && 
				(((String)object).contains("NamingContext") ||
				((String)object).contains("javax.naming.Context"))) {
			listJNDI(reader, text, name, reader.listJNDI(name));
		} else if (object instanceof List) {
			@SuppressWarnings("unchecked")
			List<NCPair> contents = (List<NCPair>)object;
			for (NCPair ncp : contents) {
				String n = name.equals("/") || name.isEmpty() ? ncp.getName() : name + "/" + ncp.getName();
				n = n.replace("//", "/");
				listJNDI(reader, text, n, reader.lookupJNDI(n));
			}
			if (contents.size()==0) {
				text.append(String.format("%s\n", name));
			}
		} else { 
			text.append(String.format("%s:%s\n", name, object));
		}
		return text;
	}
	
	public static StringBuilder listSessionContext(
			JNDIReaderRemote reader, StringBuilder text, String name, Object object) 
			throws NamingException {
		if (object instanceof String && ((String)object).contains("Exception")) {
			text.append(String.format("%s:%s\n", name, object));
		}
		else if (object instanceof String && 
				(((String)object).contains("NamingContext") ||
				((String)object).contains("javax.naming.Context"))) {
			listSessionContext(reader, text, name, reader.listSessionContext(name));
		} else if (object instanceof List) {
			@SuppressWarnings("unchecked")
			List<NCPair> contents = (List<NCPair>)object;
			for (NCPair ncp : contents) {
				String n = name.equals("/") || name.isEmpty() ? ncp.getName() : name + "/" + ncp.getName();
				n = n.replace("//", "/");
				listSessionContext(reader, text, n, reader.lookupSessionContext(n));
			}
			if (contents.size()==0) {
				text.append(String.format("%s\n", name));
			}
		} else { 
			text.append(String.format("%s:%s\n", name, object));
		}
		return text;
	}
}
