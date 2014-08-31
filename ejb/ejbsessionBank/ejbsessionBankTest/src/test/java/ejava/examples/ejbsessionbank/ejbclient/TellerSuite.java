package ejava.examples.ejbsessionbank.ejbclient;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TellerRemotingIT.class,
	TellerOwnerRemotingIT.class,
	TellerEJBClientIT.class,
	TellerOwnerEJBClientIT.class,
})
public class TellerSuite {
}
