package ejava.examples.ejbsessionbank.ejb;

import javax.ejb.Local;

import ejava.examples.ejbsessionbank.bl.Teller;

@Local
public interface TellerLocal extends Teller {

}
