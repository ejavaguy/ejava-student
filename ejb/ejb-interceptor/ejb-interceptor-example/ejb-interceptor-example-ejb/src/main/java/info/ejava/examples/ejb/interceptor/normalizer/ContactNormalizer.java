package info.ejava.examples.ejb.interceptor.normalizer;

import info.ejava.examples.ejb.interceptor.bo.Contact;

public class ContactNormalizer extends NormalizerBase {
    public Contact normalize(Contact contact) {
        if (contact==null) { return null; }
        
        //normalize name
        contact.setName(normalizeName(contact.getName()));
        
        return contact;
    }
    
}
