package ejava.jpa.hibernatemigration;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.DefaultNamingStrategy;

/**
 * This class provides an example of a means to override names within a session
 * configuration -- and specifically to override names provided by a Java annotation
 * within a session. It is given to the hibernate Configuration instead of extending
 * the Configuration. I found this approach to be consistent with the Hibernate Ant tools
 * since the Ant configuration task would not allow a custom configuration class
 * to be specified but would allow a custom naming strategy.
 */
@SuppressWarnings("serial")
public class CustomizedNamingStrategy extends DefaultNamingStrategy {
	private static final Log log = LogFactory.getLog(CustomizedNamingStrategy.class);
	//mapping from className to tableName
	private Map<String, String> classTableMap = new HashMap<String, String>();
	//tableName override
	private Map<String, String> tableMap = new HashMap<String, String>();
	
	public CustomizedNamingStrategy addClassTableMapping(String className, String tableName) {
		classTableMap.put(className, tableName);
		return this;
	}
	public CustomizedNamingStrategy addTableMapping(String oldName, String newName) {
		tableMap.put(oldName, newName);
		return this;
	}
	
	@Override
	public String classToTableName(String className) {
		log.debug("classToTableName(" + className + ")");
		String tableName = super.classToTableName(className);
		
		String newName = classTableMap.get(className);
		if (newName != null) {
			log.info(String.format("updating %s from tableName: %s to %s", 
					className, tableName, newName));
			tableName = newName;
		}
		return tableName;
	}

	@Override
	public String tableName(String tableName) {
		log.debug("tableName(" + tableName + ")");
		
		String newName = tableMap.get(tableName);
		if (newName != null) {
			log.info(String.format("updating tableName from %s to %s", 
					tableName, newName));
			tableName = newName;
		}
		return tableName;
	}
}
