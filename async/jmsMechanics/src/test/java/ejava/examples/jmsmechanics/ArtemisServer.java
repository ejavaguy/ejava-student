package ejava.examples.jmsmechanics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.activemq.artemis.core.config.impl.SecurityConfiguration;
import org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS;
import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManager;
import org.apache.activemq.artemis.spi.core.security.jaas.PropertiesLoader;
import org.apache.activemq.artemis.spi.core.security.jaas.ReloadableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArtemisServer {
    private static final Logger logger = LoggerFactory.getLogger(ArtemisServer.class);
    public static final String DEFAULT_BASEDIR="target/test-classes";
    public static final String DEFAULT_USERS_FILE="users.properties";
    public static final String DEFAULT_ROLES_FILE="roles.properties";
    
    private SecurityConfiguration securityConfig = new SecurityConfiguration();
    private Map<String, String> options = new HashMap<>();
    private String usersFile=DEFAULT_USERS_FILE;
    private String rolesFile=DEFAULT_ROLES_FILE;
    private EmbeddedJMS server;
    
    ArtemisServer() {
        setBaseDir(DEFAULT_BASEDIR);
    }
    
    public void setBaseDir(String baseDir) {
        options.put("baseDir", baseDir);
    }
    public void setUsersFile(String usersFile) {
        this.usersFile = usersFile;
    }
    public void setRolesFile(String rolesFile) {
        this.rolesFile = rolesFile;
    }
    
    protected void init() {
        server = new EmbeddedJMS();

        PropertiesLoader propLoader = new PropertiesLoader();
        propLoader.init(options);
        propLoader.load("users", usersFile, options)
                  .getProps()
                  .forEach((user,password)->{
                      securityConfig.addUser((String)user, (String)password);
                      logger.info("{}={}", user, password);
                  });
        propLoader.load("roles", rolesFile, options)
                   .invertedPropertiesValuesMap()
                   .forEach((user,roles)-> {
                       ((Set<String>)roles).forEach(role->{
                           securityConfig.addRole(user, role);
                           logger.info("user {} => {} role", user, role);
                       });
                   });
        
        @SuppressWarnings("deprecation")
        ActiveMQSecurityManager security = new org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManagerImpl(securityConfig);
        server.setSecurityManager(security);
    }
    
    public void addUser(String user, String password) {
        securityConfig.addUser(user, password);
    }
    
    public void addRole(String user, String role) {
        securityConfig.addRole(user, role);
    }
    
    public void start() throws Exception {
        if (server==null) {
            init();
        }
        server.start();
    }
    
    public void stop() throws Exception {
        if (server!=null) {
            server.stop();
        }
    }
}
