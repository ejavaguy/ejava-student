package ejava.examples.ejbsessionbank.ejb;

import java.util.List;

import javax.ejb.Remote;

import ejava.examples.ejbsessionbank.bl.BankException;
import ejava.examples.ejbsessionbank.bl.Teller;
import ejava.examples.ejbsessionbank.bo.Ledger;
import ejava.examples.ejbsessionbank.bo.Owner;
import ejava.examples.ejbsessionbank.dto.OwnerDTO;

@Remote
public interface TellerRemote extends Teller {
    Ledger getLedger2() throws BankException;
    List<Owner> getOwnersLoaded(int index, int count) throws BankException;
    List<Owner> getOwnersPOJO(int index, int count) throws BankException;
    List<OwnerDTO> getOwnersDTO(int index, int count) throws BankException;
    String whoAmI();
}
