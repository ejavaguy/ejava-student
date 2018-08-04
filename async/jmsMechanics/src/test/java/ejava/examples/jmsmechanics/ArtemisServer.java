package ejava.examples.jmsmechanics;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.activemq.artemis.core.config.impl.SecurityConfiguration;
import org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS;
import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManager;
import org.apache.activemq.artemis.spi.core.security.jaas.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArtemisServer {
    private static final Logger logger = LoggerFactory.getLogger(ArtemisServer.class);
    public static final String DEFAULT_BASEDIR="target/test-classes";
    public static final String DEFAULT_USERS_FILE="users.properties";
    public static final String DEFAULT_ROLES_FILE="roles.properties";
    public static final String DONE_FILE="STOP_ME";
    
    static ArtemisServer instance = null;
    private SecurityConfiguration securityConfig = new SecurityConfiguration();
    private Map<String, String> options = new HashMap<>();
    private String usersFile=DEFAULT_USERS_FILE;
    private String rolesFile=DEFAULT_ROLES_FILE;
    private EmbeddedJMS server;
    private File doneFile;
    
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
        
        doneFile = new File(options.get("baseDir") + File.separatorChar + DONE_FILE);
        if (doneFile.exists()) {
            doneFile.delete();
        }
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
    
    public boolean isDone() {
        return doneFile.exists();
    }
    
    
    public static void main(String args[]) {
        boolean noExit=false;
        try {
            System.out.print("Artemis args:");
            Arrays.asList(args).forEach(a->System.out.print(a + " "));
            System.out.println();
            final ArtemisServer server = new ArtemisServer();
            for (int i=0; i<args.length; i++) {
                if ("-baseDir".equals(args[i])) {
                    server.setBaseDir(args[++i]);
                }
                else if ("-users".equals(args[i])) {
                    server.setUsersFile(args[++i]);
                }
                else if ("-roles".equals(args[i])) {
                    server.setRolesFile(args[++i]);
                }
                else if ("-noExit".equals(args[i])) {
                    noExit=true;
                }
            }
            server.start();
            for(boolean done=false;!done;) {
                done=server.isDone();
                logger.debug("waiting for done file: {}", ArtemisServer.DONE_FILE);
                Thread.sleep(1000);
            }
            server.stop();
        }
        catch (Exception ex) {
            logger.error("",ex);
            if (noExit) {
                throw new RuntimeException("error in publisher", ex);
            }
            System.exit(-1);
        }        
    }
}
