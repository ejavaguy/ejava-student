package org.myorg.encconfig.auditor.ejb.it;

import org.junit.Ignore;
import org.myorg.encconfig.auditor.ejb.AuditorCheckerITBase;
import org.myorg.encconfig.ejb.AuditorRemote;

//TODO: enc-config 23: activate this test case 
@Ignore
public class XMLInjectAuditorEJBIT extends AuditorCheckerITBase {
	private static final String jndiName = System.getProperty("xmlinject.name",
		"ejb:/encconfig-labex-ejb/XMLInjectAuditorEJB!" + AuditorRemote.class.getName());
	
	public XMLInjectAuditorEJBIT() {
		super(jndiName);
	}
}
