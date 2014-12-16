package info.ejava.examples.ejb.interceptor.ejb;

@SuppressWarnings("serial")
public class ContactNotFound extends Exception {
    int contactId;
    public ContactNotFound(int contactId) {
        super(String.format("contact[%d] not found", contactId));
        this.contactId = contactId;
    }
}
