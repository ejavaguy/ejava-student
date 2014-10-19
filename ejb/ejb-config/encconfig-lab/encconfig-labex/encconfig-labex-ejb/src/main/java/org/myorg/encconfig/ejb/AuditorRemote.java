package org.myorg.encconfig.ejb;

import javax.ejb.Remote;

@Remote
public interface AuditorRemote {
	boolean isPublishJMS();
	boolean havePersistenceContext();
	boolean haveTopic();
	boolean haveConnectionFactory();
	int audit(String message);
}
