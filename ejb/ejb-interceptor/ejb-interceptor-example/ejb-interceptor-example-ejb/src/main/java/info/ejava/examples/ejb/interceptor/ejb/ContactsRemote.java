package info.ejava.examples.ejb.interceptor.ejb;

import java.util.List;

import info.ejava.examples.ejb.interceptor.bo.Contact;
import info.ejava.examples.ejb.interceptor.bo.ContactInfo;

import javax.ejb.Remote;

@Remote
public interface ContactsRemote {

    int cleanup();

    Contact createContact(Contact contact) throws InvalidParam;
    Contact createNormalizedContact(Contact contact) throws InvalidParam;
    void addContactInfo(int contactId, ContactInfo contactInfo) throws ContactNotFound, InvalidParam;
    List<Contact> getContacts(String name, int offset, int limit);

}
