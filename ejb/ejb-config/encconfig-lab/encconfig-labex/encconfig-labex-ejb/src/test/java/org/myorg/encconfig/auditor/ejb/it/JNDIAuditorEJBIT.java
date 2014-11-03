package org.myorg.encconfig.auditor.ejb.it;

import org.junit.Ignore;
import org.myorg.encconfig.auditor.ejb.AuditorCheckerITBase;
import org.myorg.encconfig.ejb.AuditorRemote;

//TODO: enc-config 17: activate this testcase
@Ignore
public class JNDIAuditorEJBIT extends AuditorCheckerITBase {
	private static final String jndiName = System.getProperty("jndi.name",
		"ejb:/encconfig-labex-ejb/JNDIAuditorEJB!" + AuditorRemote.class.getName());
	
	public JNDIAuditorEJBIT() {
		super(jndiName);
	}
}
