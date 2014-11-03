package org.myorg.encconfig.auditor.ejb.it;

import org.myorg.encconfig.auditor.ejb.AuditorCheckerITBase;
import org.myorg.encconfig.ejb.AuditorRemote;

public class JNDIAuditorEJBIT extends AuditorCheckerITBase {
	private static final String jndiName = System.getProperty("jndi.name",
		"ejb:/encconfig-labsol-ejb/JNDIAuditorEJB!" + AuditorRemote.class.getName());
	
	public JNDIAuditorEJBIT() {
		super(jndiName);
	}
}
