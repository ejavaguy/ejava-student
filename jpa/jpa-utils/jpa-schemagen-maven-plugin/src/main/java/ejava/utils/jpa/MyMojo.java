package ejava.utils.jpa;


import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.hibernate.cfg.AvailableSettings;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Persistence;

/**
 * This plugin will generate SQL schema for a specified persistence unit. It is targeted/tuned to 
 * have the features desired for use with the ejava course examples. Thus it inserts hibernate-specific
 * extensions to enable pretty-printing and line terminators. Use this as an example of how you could
 * create something more general purpose if you would like.
 */
@Mojo( name = "generate", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES , requiresDependencyResolution = ResolutionScope.TEST, threadSafe=true  )
@Execute(phase=LifecyclePhase.TEST_COMPILE)
public class MyMojo extends AbstractMojo {
	
	/**
	 * The name of the persistence unit from within META-INF/persistence.xml
	 */
    @Parameter( property = "persistenceUnit", required=true)
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
			
			Thread.currentThread().setContextClassLoader(classLoader);
			URL pxml = classLoader.getResource("META-INF/persistence.xml");
			URL hprops = classLoader.getResource("hibernate.properties");
			getLog().info("META-INF/persistence.xml found= " + (pxml!=null) + ", " + pxml);
			getLog().info("hibernate.properties found= " + (hprops!=null) + ", " + hprops);
			
			Map<String, Object> properties = configure();
			properties.forEach((k,v) -> getLog().info(k + "=" + v));
			Persistence.generateSchema(persistenceUnit, properties);
		} catch (DependencyResolutionRequiredException | MalformedURLException e) {
			throw new MojoFailureException(e.toString());
		} finally {
			if (classLoader!=null) {
				try { classLoader.close(); } catch (IOException e) {}
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
