package org.myorg.encconfig.auditor.ejb.it;

import org.junit.Ignore;
import org.myorg.encconfig.auditor.ejb.AuditorCheckerITBase;
import org.myorg.encconfig.ejb.AuditorRemote;

//TODO: enc-config 11: activate this testcase 
@Ignore
public class ENCAuditorEJBIT extends AuditorCheckerITBase {
	private static final String jndiName = System.getProperty("enc.name",
		"ejb:/encconfig-labex-ejb/ENCAuditorEJB!" + AuditorRemote.class.getName());
	
	public ENCAuditorEJBIT() {
		super(jndiName);
	}
}
