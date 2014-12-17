package info.ejava.examples.ejb.interceptor.ejb;

import java.util.Arrays;
import java.util.List;

import info.ejava.examples.ejb.interceptor.bo.Contact;
import info.ejava.examples.ejb.interceptor.bo.ContactInfo;
import info.ejava.examples.ejb.interceptor.interceptors.Validation;
import info.ejava.examples.ejb.interceptor.normalizer.ContactNormalizer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Validation
//@Interceptors({
//    PreNormizedInterceptor.class,
//    ContactsNormalizerInterceptor.class,
//    PostNormizedInterceptor.class,
//})
public class ContactsEJB implements ContactsRemote {
    private static final Logger logger = LoggerFactory.getLogger(ContactsEJB.class);
    
    @PersistenceContext(unitName="ejbinterceptor-contacts")
    private EntityManager em;

    @PostConstruct
    public void init() {
        logger.debug("*** {}:init({}) ***", getClass().getSimpleName(), super.hashCode());
    }
    @PreDestroy
    public void destroy() {
        logger.debug("*** {}:destroy({}) ***", getClass().getSimpleName(), super.hashCode());
    }
    
    @Override
    public int cleanup() {
        int count = 0;
        for (String entity : Arrays.asList("PostalInfo", "PhoneInfo", "Contact")) {
            count+=em.createQuery(String.format("delete from %s", entity)).executeUpdate();
        }
        return count;
    }
    
    @Override
    public Contact createContact(Contact contact) throws InvalidParam {
        logger.debug("createContact({})", contact);
        em.persist(contact);
        return contact;
    }
    
    /**
     * This method will explicitly attempt to normalize the contact within the 
     * body of the call.
     */
    @Override
    public Contact createNormalizedContact(Contact contact) throws InvalidParam {
        logger.debug("createNormalizedContact({})", contact);
        contact = new ContactNormalizer().normalize(contact);
        logger.debug("normalizedContact={}", contact);
        em.persist(contact);
        return contact;
    }
    
    @Override
    public void addContactInfo(int contactId, ContactInfo contactInfo) throws ContactNotFound, InvalidParam {
        logger.debug("addContactInfo({}, {})", contactId, contactInfo);
        Contact contact = em.find(Contact.class, contactId);
        if (contact==null) {
            throw new ContactNotFound(contactId);
        }
        contactInfo.setContact(contact);
        em.persist(contactInfo);
    }
    
    @Override
    public List<Contact> getContacts(String name, int offset, int limit) {
        TypedQuery<Contact> query = em.createNamedQuery("EJBInterceptorContact.getContact", Contact.class)
            .setParameter("name", name==null ? null : name.toLowerCase().trim());
        if (offset>0) {
            query.setFirstResult(offset);
        }
        if (limit>0) {
            query.setMaxResults(limit);
        }
        List<Contact> contacts = query.getResultList();
        logger.debug("getContacts(name={}, offset={}, limit={})={}", name, offset, limit, contacts.size());
        return contacts;
    }
    
    @Schedule(second="0", minute="*/10", hour="*", dayOfWeek="*", dayOfMonth="*", persistent=false)
    public void timeout(Timer timer) {
        logger.debug("timeout({}), next={})", timer, timer.getNextTimeout());
    }
}
