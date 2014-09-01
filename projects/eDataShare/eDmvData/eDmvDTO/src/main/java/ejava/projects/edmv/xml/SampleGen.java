package ejava.projects.edmv.xml;

import info.ejava.projects.edmv._1.Dmv;
import info.ejava.projects.edmv._1.PersonsType;
import info.ejava.projects.edmv._1.VehicleRegistrationsType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import gov.ojp.it.jxdm._3_0.AddressType;
import gov.ojp.it.jxdm._3_0.DecalType;
import gov.ojp.it.jxdm._3_0.IDType;
import gov.ojp.it.jxdm._3_0.ImageType;
import gov.ojp.it.jxdm._3_0.Person;
import gov.ojp.it.jxdm._3_0.PersonHeightMeasureType;
import gov.ojp.it.jxdm._3_0.PersonNameTextType;
import gov.ojp.it.jxdm._3_0.PersonNameType;
import gov.ojp.it.jxdm._3_0.PersonPhysicalDetailsType;
import gov.ojp.it.jxdm._3_0.PersonWeightMeasureType;
import gov.ojp.it.jxdm._3_0.ReferenceType;
import gov.ojp.it.jxdm._3_0.ResidenceType;
import gov.ojp.it.jxdm._3_0.StreetType;
import gov.ojp.it.jxdm._3_0.TextType;
import gov.ojp.it.jxdm._3_0.VehicleRegistration;
import gov.ojp.it.jxdm._3_0.VehicleType;
import gov.ojp.it.jxdm._3_0_3.proxy.ncic_2000._1_0.EYEType;
import gov.ojp.it.jxdm._3_0_3.proxy.ncic_2000._1_0.RACType;
import gov.ojp.it.jxdm._3_0_3.proxy.ncic_2000._1_0.SEXType;
import gov.ojp.it.jxdm._3_0_3.proxy.ncic_2000._1_0.VCOType;
import gov.ojp.it.jxdm._3_0_3.proxy.ncic_2000._1_0.VMAType;
import gov.ojp.it.jxdm._3_0_3.proxy.ncic_2000._1_0.VMOType;
import gov.ojp.it.jxdm._3_0_3.proxy.usps_states._1.USStateCodeType;
import gov.ojp.it.jxdm._3_0_3.proxy.xsd._1.Base64Binary;
import gov.ojp.it.jxdm._3_0_3.proxy.xsd._1.Date;
import gov.ojp.it.jxdm._3_0_3.proxy.xsd._1.GMonth;
import gov.ojp.it.jxdm._3_0_3.proxy.xsd._1.GYear;

public class SampleGen {
    private static int residenceId = 0;
    private static int personId = 0;
    private static int vehicleId = 0;
    
    public Dmv createDmv() throws Exception {
        Dmv dmv = new Dmv();
        dmv.setPeople(new PersonsType());
        dmv.setVehicleRegistrations(new VehicleRegistrationsType());
        
        Person person = createPerson("John", "C", "Doe", null);
        dmv.getPeople().getPerson().add(person);
        dmv.getVehicleRegistrations().getVehicleRegistration().add(
                createVehicleRegistration(person));
        
        return dmv;
    }

    public Person createPerson(String first, String middle, String last,
            String suffix) throws Exception {
        Person person = new Person();
        person.setId("p" + ++personId);
        person.setSourceIDText("" + personId);
        
        /*
        PersonNameTextType firstName = new PersonNameTextType();
        firstName.setValue(first);
        PersonNameType nameType = new PersonNameType();
        nameType.setPersonGivenName(firstName);
        person.setPersonName(nameType);
        */
        
        person.setPersonName(new PersonNameType(new PersonNameTextType(first,
                null, null), new PersonNameTextType(middle, null, null),
                new PersonNameTextType(last, null, null), new TextType(suffix,
                        null, null)));
        
        DatatypeFactory dtf = DatatypeFactory.newInstance();
        XMLGregorianCalendar cal = dtf.newXMLGregorianCalendar();
        cal.setYear(1960);
        cal.setMonth(01);
        cal.setDay(03);
        person.setPersonBirthDate(new Date());
        person.getPersonBirthDate().setValue(cal);

        person.setPersonPhysicalDetails(new PersonPhysicalDetailsType(
                new EYEType("green", null, null), new TextType("brown", null,
                        null), new PersonHeightMeasureType(new BigDecimal(72)),
                new PersonWeightMeasureType(new BigDecimal(220)), new RACType(
                        "blue", null, null), new SEXType("male", null, null),
                new ImageType(null, null, new Base64Binary(
                        new byte[] { 0x00, 0x01, 0x02 }, null, null))));

        ResidenceType homeAddress = new ResidenceType(null, null,
                new AddressType(new StreetType(new TextType("500", null, null),
                        new TextType("Foo Bar Street", null, null)),
                        new TextType("Baz City", null, null),
                        new USStateCodeType("MD", null, null), new IDType(
                                new TextType("21000", null, null))),
                null, null // end date
        );
        cal = dtf.newXMLGregorianCalendar();
        cal.setYear(2000);
        cal.setMonth(1);
        cal.setDay(1);
        homeAddress.setResidenceStartDate(new Date());
        homeAddress.getResidenceStartDate().setValue(cal);
        
        cal = dtf.newXMLGregorianCalendar();
        cal.setYear(2005);
        cal.setMonth(12);
        cal.setDay(31);
        homeAddress.setResidenceEndDate(new Date());
        homeAddress.getResidenceEndDate().setValue(cal);
        
        homeAddress.setSourceIDText("" + ++residenceId);

        person.getResidence().add(homeAddress);

        return person;
    }

    public VehicleRegistration createVehicleRegistration(Person owner) throws Exception {
        List<ReferenceType> owners = new ArrayList<ReferenceType>();
        owners.add(new ReferenceType(owner));
        
        VehicleRegistration registration = new VehicleRegistration(
                null, 
                null,
                new IDType(new TextType("12345678", null, null)), // plates
                new DecalType(null, null, 
                        new GMonth(), new GYear()), 
                new VehicleType(null, null, 
                        owners, 
                        new IDType(new TextType("vin123", null, null)), // vin
                        new VCOType("RED", null, null), // color code
                        new VMAType("FORD", null, null), //make
                        new VMOType("BRONCOII", null, null), //model
                        new GYear())
                );
        DatatypeFactory dtf = DatatypeFactory.newInstance();
        XMLGregorianCalendar cal = dtf.newXMLGregorianCalendar();
        cal.setMonth(01);
        registration.getVehicleRegistrationDecal().getDecalMonthDate().setValue(cal);

        cal = dtf.newXMLGregorianCalendar();
        cal.setYear(2006);
        registration.getVehicleRegistrationDecal().getDecalYearDate().setValue(cal);
        
        cal = dtf.newXMLGregorianCalendar();
        cal.setYear(1996);
        registration.getVehicle().getVehicleModelYearDate().setValue(cal);

        registration.setId("vr" + ++vehicleId);
        registration.setSourceIDText("" + vehicleId);
        
        return registration;
    }
}
