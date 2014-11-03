package org.myorg.encconfig.auditor.ejb.it;

import org.myorg.encconfig.auditor.ejb.AuditorCheckerITBase;
import org.myorg.encconfig.ejb.AuditorRemote;

public class XMLInjectAuditorEJBIT extends AuditorCheckerITBase {
	private static final String jndiName = System.getProperty("xmlinject.name",
		"ejb:/encconfig-labsol-ejb/XMLInjectAuditorEJB!" + AuditorRemote.class.getName());
	
	public XMLInjectAuditorEJBIT() {
		super(jndiName);
	}
}
