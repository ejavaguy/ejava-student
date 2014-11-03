package org.myorg.encconfig.auditor.ejb.it;

import org.myorg.encconfig.auditor.ejb.AuditorCheckerITBase;
import org.myorg.encconfig.ejb.AuditorRemote;

public class LookupAuditorEJBIT extends AuditorCheckerITBase {
	private static final String jndiName = System.getProperty("lookup.name",
		"ejb:/encconfig-labsol-ejb/LookupAuditorEJB!" + AuditorRemote.class.getName());
	
	public LookupAuditorEJBIT() {
		super(jndiName);
	}
}
