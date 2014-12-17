package info.ejava.examples.ejb.interceptor.normalizer;

import info.ejava.examples.ejb.interceptor.bo.Contact;
import info.ejava.examples.ejb.interceptor.bo.PhoneInfo;
import info.ejava.examples.ejb.interceptor.bo.PostalAddress;
import info.ejava.examples.ejb.interceptor.bo.PostalInfo;

public class ContactNormalizer extends NormalizerBase {
    public Contact normalize(Contact contact) {
        if (contact==null) { return null; }
        
        //normalize name
        contact.setName(normalizeName(contact.getName()));
        
        return contact;
    }
    
    public PostalAddress normalize(PostalAddress address) {
        if (address==null) { return null; }
        
        address.setStreet1(normalizeName(address.getStreet1()));
        address.setStreet2(normalizeName(address.getStreet2()));
        address.setCity(normalizeName(address.getCity()));
        address.setState(toUpper(address.getState()));
        
        //todo normalize zip code
        return address;
    }
    
    public PostalInfo normalize(PostalInfo pi) {
        if (pi==null) { return null; }
        normalize(pi.getAddress());
        return pi;
    }
    
    
    public PhoneInfo normalize(PhoneInfo pi) {
        if (pi==null) { return null; }
        
        //todo normalize phone#
        
        return pi;
    }
}
