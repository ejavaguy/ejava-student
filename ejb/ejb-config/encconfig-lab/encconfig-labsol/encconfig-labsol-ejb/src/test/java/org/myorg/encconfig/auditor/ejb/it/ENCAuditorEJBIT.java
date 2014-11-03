package org.myorg.encconfig.auditor.ejb.it;

import org.myorg.encconfig.auditor.ejb.AuditorCheckerITBase;
import org.myorg.encconfig.ejb.AuditorRemote;

public class ENCAuditorEJBIT extends AuditorCheckerITBase {
	private static final String jndiName = System.getProperty("enc.name",
		"ejb:/encconfig-labsol-ejb/ENCAuditorEJB!" + AuditorRemote.class.getName());
	
	public ENCAuditorEJBIT() {
		super(jndiName);
	}
}
