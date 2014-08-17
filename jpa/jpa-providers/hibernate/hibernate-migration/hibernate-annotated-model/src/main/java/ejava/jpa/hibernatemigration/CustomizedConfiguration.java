package ejava.jpa.hibernatemigration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;

/**
 * This class implements overrides of persistent class properties using an extension of
 * the Configuration class. In this specific example, this class will override the 
 * annotated class's @Table.name if provided as one of the context properties.
 */
public class CustomizedConfiguration extends AnnotationConfiguration {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(CustomizedConfiguration.class);
	private Map<String, String> tableMap = new HashMap<String, String>();
	
	@Override
	protected void secondPassCompile() throws MappingException {
		super.secondPassCompile();
		for (Entry<String, String> e: tableMap.entrySet()) {
			String className = e.getKey();
			String tableName = e.getValue();
			
			PersistentClass pc = getClassMapping(className);
			if (pc == null) {
				log.warn("entity class " + tableName + " not found in session configuration");
				continue;
			}
			
			String oldName = pc.getTable().getName();
			if (!tableName.equals(oldName)) {
				pc.getTable().setName(tableName);
				log.info(String.format("changed %s tableName from %s to %s", 
						className, oldName, tableName));
			}
		}
	}

	@Override
	public Configuration configure() throws HibernateException {
		log.info(AnnotationConfiguration.class.getResource("AnnotationConfiguration.class"));
		Configuration config = super.configure();
		for (Object o : getProperties().keySet()) {
			String key = (String)o;
			log.info("checking " + o);
			//different versions of hibernate express key property differently
			if (key.matches("(hibernate.)*(tableName:)+.*")) {
				String[] tokens = key.split(":");
				if (tokens.length != 2) {
					log.warn("bad tableName key:" + key);
					continue;
				}
				String className = tokens[1];
				String tableName = getProperty(key);
				if (tableName==null || tableName.length()==0) {
					log.warn("empty tableName value:" + key);
					continue;
				}
				tableMap.put(className, tableName);
			}
		}
		return config;
	}
	
}
