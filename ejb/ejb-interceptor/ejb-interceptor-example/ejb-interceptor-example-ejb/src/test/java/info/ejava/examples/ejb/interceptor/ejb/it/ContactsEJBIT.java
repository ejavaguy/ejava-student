package info.ejava.examples.ejb.interceptor.ejb.it;

import static org.junit.Assert.*;


import java.util.List;

import info.ejava.examples.ejb.interceptor.bo.Contact;
import info.ejava.examples.ejb.interceptor.bo.ContactInfo;
import info.ejava.examples.ejb.interceptor.bo.ContactRole;
import info.ejava.examples.ejb.interceptor.bo.AddressType;
import info.ejava.examples.ejb.interceptor.bo.PhoneInfo;
import info.ejava.examples.ejb.interceptor.bo.PostalAddress;
import info.ejava.examples.ejb.interceptor.bo.PostalInfo;
import info.ejava.examples.ejb.interceptor.ejb.ContactNotFound;
import info.ejava.examples.ejb.interceptor.ejb.ContactsRemote;
import info.ejava.examples.ejb.interceptor.ejb.InvalidParam;
import info.ejava.examples.ejb.interceptor.normalizer.ContactNormalizer;
import info.ejava.examples.ejb.interceptor.normalizer.NormalizerBase;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.utils.JNDIUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContactsEJBIT  {
	private static final Logger logger = LoggerFactory.getLogger(ContactsEJBIT.class);
    private static final String contactsJNDI = System.getProperty("contacts.jndi.name",
            "ejb:/ejb-interceptor-example-ejb/ContactsEJB!" + ContactsRemote.class.getName());
	private Context jndi;
	private ContactsRemote contacts;
	
	@BeforeClass
	public static void setUpClass() throws NamingException {
	    //try { Thread.sleep(250); } catch (InterruptedException e) {} //needs a little time to finish deploying
	}
	
	@Before
	public void setUp() throws NamingException {
        jndi=new InitialContext();
        logger.debug("looking up jndi.name={}", contactsJNDI);
	    contacts = (ContactsRemote)jndi.lookup(contactsJNDI);
        
        logger.debug("{} rows deleted", contacts.cleanup());
	}
	
	private PostalInfo makePostalInfo(ContactRole role, AddressType type) {
	    PostalAddress pa = new PostalAddress();
	    pa.setStreet1("1600 pEnn ave");
	    pa.setCity("washington");
	    pa.setState("dC");
	    pa.setZip("20500");
	    PostalInfo pi = new PostalInfo();
	    pi.setAddress(pa);
	    pi.setRole(role);
        pi.setType(type);
        return pi;
	}
	
	private PhoneInfo makePhoneInfo(ContactRole role) {
	    PhoneInfo pi = new PhoneInfo();
	    pi.setPhoneNumber("202-456-1414");
        pi.setRole(role);
        return pi;
	}
	
	/**
	 * This test verifies a contact pre-configured with values that match the normlization
	 * rules will be accepted and echoed back in identical form. 
	 * @throws InvalidParam 
	 */
	@Test
	public void createContact() throws InvalidParam {
        String originalName = "John Doe";
	    Contact contact = contacts.createContact(
	        new ContactNormalizer().normalize(new Contact().withName(originalName)));
	    assertNotNull("null contact", contact);
	    assertTrue("no ID assigned", contact.getId() > 0);
	    
	    List<Contact> list = contacts.getContacts(contact.getName(), 0, 0);
	    assertEquals("unexpected number of contacts", 1, list.size());
	    Contact contact2 = list.get(0);
        assertEquals("unexpected name", originalName, contact2.getName());
	    assertEquals("unexpected contact info", 0, contact2.getContactInfo().size());
	}
    
	/**
	 * This test verifies contact info pre-configured with values that match the normalization
	 * rules will be accepted and echoed back in identical form.
	 * @throws ContactNotFound
	 * @throws  
	 */
    @Test
    public void addNormalizedContactInfo() throws InvalidParam, ContactNotFound  {
        Contact contact = contacts.createContact(new Contact().withName("John Doe"));
        ContactInfo workAddress = new ContactNormalizer().normalize(
                makePostalInfo(ContactRole.WORK, AddressType.PHYSICAL));
        contacts.addContactInfo(contact.getId(), workAddress);
        ContactInfo workPhone = new ContactNormalizer().normalize(
                makePhoneInfo(ContactRole.WORK));
        contacts.addContactInfo(contact.getId(), workPhone);
        
        List<Contact> list = contacts.getContacts(contact.getName(), 0, 0);
        assertEquals("unexpected number of contacts", 1, list.size());
        Contact contact2 = list.get(0);
        assertEquals("unexpected contact info", 2, contact2.getContactInfo().size());
        for (ContactInfo ci: contact2.getContactInfo()) {
            switch (ci.getContactType()) {
            case ADDRESS:
                assertEquals("unexpected work address", workAddress.toString(), ci.toString());
                break;
            case PHONE:
                assertEquals("unexpected work phone", workPhone.toString(), ci.toString());
                break;
            default:
                fail("unexpected contactType:" + ci.getContactType());
                break;
            }
        }
    }
    
    /**
     * This test verifies the name will be normalized when creating the contact.
     */
    @Test
    public void createNonNormalizedContact() throws InvalidParam {
        //use a non-normalized name
        String originalName = "john Doe";
        String normalizedName = new NormalizerBase().normalizeName(originalName);
        Contact contact = contacts.createContact(new Contact().withName(originalName));
        assertEquals("name not normalized:" + contact.getName(), normalizedName, contact.getName());
    }
    
    /**
     * This test verifies the name will be normalized when creating the contact.
     */
    @Test
    public void createInvalidContact() {
        //use a non-normalized name
        String originalName = "@#@$@%@ Doe";
        try {
            Contact contact=contacts.createContact(new Contact().withName(originalName));
            fail("did not validate bad contact:" + contact);
        } catch (InvalidParam ex) {
            logger.debug("received expected exception", ex);
        }
    }
    
    @Test
    public void addNonNormalizedContactInfo() throws InvalidParam, ContactNotFound  {
        Contact contact = new Contact().withName("john doe");
        logger.debug("original contact:{}",contact);
        contact = contacts.createContact(contact);
        ContactInfo workAddress = makePostalInfo(ContactRole.WORK, AddressType.PHYSICAL);
        logger.debug("original workAddress:{}",workAddress);
        contacts.addContactInfo(contact.getId(), workAddress);
        ContactInfo workPhone = makePhoneInfo(ContactRole.WORK);
        logger.debug("original workPhone:{}",workPhone);
        contacts.addContactInfo(contact.getId(), workPhone);
        
        List<Contact> list = contacts.getContacts(contact.getName(), 0, 0);
        assertEquals("unexpected number of contacts", 1, list.size());
        Contact contact2 = list.get(0);
        logger.debug("created contact: {}\n{}",contact2, contact2.getContactInfo());
        assertEquals("unexpected contact info", 2, contact2.getContactInfo().size());
        for (ContactInfo ci: contact2.getContactInfo()) {
            switch (ci.getContactType()) {
            case ADDRESS:
                assertEquals("unexpected work address", 
                    new ContactNormalizer().normalize((PostalInfo)workAddress).toString(), 
                    ci.toString());
                break;
            case PHONE:
                assertEquals("unexpected work phone", 
                    new ContactNormalizer().normalize((PhoneInfo)workPhone).toString(), 
                    ci.toString());
                break;
            default:
                fail("unexpected contactType:" + ci.getContactType());
                break;
            }
        }
        
        logger.debug("{}\n{}",contact2, contact2.getContactInfo());
    }
    
}
