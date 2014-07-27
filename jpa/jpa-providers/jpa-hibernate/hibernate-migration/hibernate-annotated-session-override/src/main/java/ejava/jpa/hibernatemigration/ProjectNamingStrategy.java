package ejava.jpa.hibernatemigration;

import ejava.jpa.hibernatemigration.annotated.Customer;

@SuppressWarnings("serial")
public class ProjectNamingStrategy extends CustomizedNamingStrategy {
	public ProjectNamingStrategy() {
		//this is in case the class metadata did not specify a tableName
		addClassTableMapping(Customer.class.getName(), "HMIG_CUSTOMER_STRATEGY");
		//this is in case the class metadata did spec a tableName we want to override
		addTableMapping("HMIG_CUSTOMER", "HMIG_CUSTOMER_STRATEGY");
	}
}
