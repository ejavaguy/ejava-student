package info.ejava.examples.ejb.cdisales.ejb;

import info.ejava.examples.ejb.cdisales.bl.UserMgmt;
import info.ejava.examples.ejb.cdisales.bo.CurrentUser;

import javax.ejb.Local;

@Local
public interface UserMgmtLocal extends UserMgmt {
    CurrentUser login();
}
