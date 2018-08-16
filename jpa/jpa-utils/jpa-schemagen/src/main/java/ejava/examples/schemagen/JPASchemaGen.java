package ejava.examples.schemagen;

import javax.persistence.Persistence;

public class JPASchemaGen {
	public static void main(String args[]) {
		if (args!=null && args.length==1) {
			System.out.println("Generating schema for persistence-unit: " + args[0]);
			//Persistence.generateSchema(args[0], null);
			Persistence.createEntityManagerFactory(args[0]);
			System.exit(0);
		} else {
			System.err.println("usage: " + JPASchemaGen.class.getName() + " (persistenceUnit name)");
			throw new RuntimeException("incorrect usage");
		}
	}
}