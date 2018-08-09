package ejava.utils.jpa;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Persistence;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;

/**
 * This plugin will generate SQL schema for a specified persistence unit. It is targeted/tuned to 
 * have the features desired for use with the ejava course examples. Thus it inserts hibernate-specific
 * extensions to enable pretty-printing and line terminators. Use this as an example of how you could
 * create something more general purpose if you would like.
 */
@Mojo( name = "generate", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES , requiresDependencyResolution = ResolutionScope.TEST, threadSafe=true  )
@Execute(phase=LifecyclePhase.TEST_COMPILE)
public class JPASchemaGenMojo extends AbstractMojo {
	
	/**
	 * The name of the persistence unit from within META-INF/persistence.xml. Only required
	 * if there are multiple persistence units within the project. Otherwise, it will use the
	 * only one found in the path.
	 */
    @Parameter( property = "persistenceUnit", required=false)
    private String persistenceUnit;
    
    /**
     * The path to write the create script.
     */
    @Parameter( property = "createPath", required=false, defaultValue="target/classes/ddl/${persistenceUnit}-create.ddl")
    private String createPath;
    
    /**
     * The path to write the drop script.
     */
    @Parameter( property = "dropPath", required=false, defaultValue="target/classes/ddl/${persistenceUnit}-drop.ddl")
    private String dropPath;
    
    /**
     * Statement termination string
     */
    @Parameter( property = "delimiter", required=false, defaultValue=";")
    private String delimiter;
    
    @Parameter( property = "format", required=false, defaultValue="true")
    private boolean format;
    
    @Parameter( property = "scriptsAction", required=false, defaultValue="drop-and-create")
    private String scriptsAction;

    /**
     * Describes the entire project.
     */
    @Parameter( property = "project", required=true, defaultValue = "${project}" )
    private MavenProject project;
    
    protected URLClassLoader getClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {
		List<String> elements = project.getTestClasspathElements();
		URL[] urls = new URL[elements.size()];
		for (int i=0; i<elements.size(); i++) {
			String path = elements.get(i);
			URL url = new File(path).toURI().toURL();
			urls[i] = url;
		}
		URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader());
		for (URL url: classLoader.getURLs()) {
			getLog().debug("url=" + url.toString());
		}
		return classLoader;
    }
    
    protected String resolvePath(String path) {
    		path = path.replace("${persistenceUnit}", persistenceUnit);
    		return !path.startsWith("/") ? project.getBasedir() + File.separator + path : path;
    }
    
    protected Map<String, Object> configure() {
    		Map<String, Object> properties = new HashMap<>();
    		properties.put(AvailableSettings.HBM2DDL_SCRIPTS_ACTION, scriptsAction);
    		properties.put(AvailableSettings.HBM2DDL_SCRIPTS_CREATE_TARGET, resolvePath(createPath));
    		properties.put(AvailableSettings.HBM2DDL_SCRIPTS_DROP_TARGET, resolvePath(dropPath));
    		properties.put(AvailableSettings.HBM2DDL_DELIMITER, delimiter);
    		properties.put(AvailableSettings.FORMAT_SQL, new Boolean(format).toString());
    		return properties;
    }
    
    public void execute() throws MojoFailureException {
    		URLClassLoader classLoader = null;
    		try {
    			classLoader = getClassLoader();
    			this.persistenceUnit = findPersistenceUnit(classLoader);
    			getLog().info("Generating database schema for: " + persistenceUnit);
			
			Thread.currentThread().setContextClassLoader(classLoader);
			URL pxml = classLoader.getResource("META-INF/persistence.xml");
			URL hprops = classLoader.getResource("hibernate.properties");
			getLog().debug("META-INF/persistence.xml found= " + (pxml!=null ? pxml : "false"));
			getLog().debug("hibernate.properties found= " + (hprops!=null ? hprops : "false"));
			
			Map<String, Object> properties = configure();
			properties.forEach((k,v) -> getLog().debug(k + "=" + v));

			//hibernate has been appending to existing files
			for (String prop: Arrays.asList(AvailableSettings.HBM2DDL_SCRIPTS_DROP_TARGET, AvailableSettings.HBM2DDL_SCRIPTS_CREATE_TARGET)) {
			    String path = (String)properties.get(prop);
			    if (path!=null && path.toLowerCase().contains("target")) {
			        File f = new File(path);
			        getLog().info("removing existing target file:" + f.getPath());
			        f.delete();
			    }
			}
			
			Persistence.generateSchema(persistenceUnit, properties);
			loadBeforeClosing();			
		} catch (DependencyResolutionRequiredException | MalformedURLException e) {
			throw new MojoFailureException(e.toString());
		} finally {
			if (classLoader!=null) {
				try { classLoader.close(); } catch (IOException e) {}
			}
			File lockFile=new File(project.getBasedir() + File.separator + "target/h2db/ejava.mv.db");
			if (lockFile.exists()) {
			    lockFile.delete();
			}
		}
    }
    
    protected String findPersistenceUnit(ClassLoader clsLoader) throws MojoFailureException {
    		if (persistenceUnit!=null) {
    			return persistenceUnit;
    		}
    		Map<String, Object> properties = new HashMap<>();
    		properties.put(AvailableSettings.CLASSLOADERS, Collections.singletonList(clsLoader));
    		List<ParsedPersistenceXmlDescriptor> units = PersistenceXmlParser.locatePersistenceUnits(properties);
    		if (units.size()==1) {
    			return units.get(0).getName();
    		} else if (units.isEmpty()) {
    			throw new MojoFailureException("no persistenceUnit name specified and none found");
    		} else {
    			StringBuilder names = new StringBuilder();
    			units.forEach(n -> {
    				if (names.length()>0) { names.append(", "); } 
    				names.append(n.getName());
    			});
    			throw new MojoFailureException(String.format("too many persistence units found[%s], specify persistenceUnit name to use", names));    			
    		}
    }

    /**
     * kludge to try to avoid an ugly non-fatal stack trace of missing classes 
     * when plugin shuts down (closing the classloader) and the database attempts
     * to load new classes to complete its thread shutdown.
     * @throws MojoFailureException
     */
    protected void loadBeforeClosing() throws MojoFailureException {
    		for (String cls : new String[] {
    				"org.h2.mvstore.WriteBuffer",
    				"org.h2.mvstore.MVMap$2",
    				"org.h2.mvstore.MVMap$2$1",
    				"org.h2.mvstore.DataUtils$MapEntry",
    				"org.h2.mvstore.Chunk"
    				}) {
    			try {
    				Thread.currentThread().getContextClassLoader().loadClass(cls);
    			} catch (ClassNotFoundException ex) {
    				getLog().info("error pre-loading class[" + cls + "]: "+ ex.toString());
    				//throw new MojoFailureException("error pre-loading class[" + cls + "]: "+ ex.toString());
    			}
    		}
    		
    }

    public void setPersistenceUnit(String puName) {
		this.persistenceUnit = puName;
	}
	public void setProject(MavenProject project) {
		this.project = project;
	}

	public void setCreatePath(String createPath) {
		this.createPath = createPath;
	}

	public void setDropPath(String dropPath) {
		this.dropPath = dropPath;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public void setFormat(boolean format) {
		this.format = format;
	}

	public void setScriptsAction(String scriptsAction) {
		this.scriptsAction = scriptsAction;
	}

}
