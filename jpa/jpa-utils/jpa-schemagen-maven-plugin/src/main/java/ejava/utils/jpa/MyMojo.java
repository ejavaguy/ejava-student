package ejava.utils.jpa;


import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.internal.BootstrapServiceRegistryImpl;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.internal.util.ConfigHelper;
//import org.hibernate.internal.util.ClassLoaderHelper;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolverHolder;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.persistence.spi.ProviderUtil;

@Mojo( name = "generate", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES , requiresDependencyResolution = ResolutionScope.TEST, threadSafe=true  )
@Execute(phase=LifecyclePhase.TEST_COMPILE)
public class MyMojo
    extends AbstractMojo
{
	public MyMojo() {
		@SuppressWarnings("unused")
		int i=0;
	}
	
    @Parameter( property = "persistenceUnit", required=true)
    private String persistenceUnit;

    @Parameter( property = "project", required=true, defaultValue = "${project}" )
    private MavenProject project;
    
    @Parameter( property = "plugin", required=true, readonly=true, defaultValue = "${plugin}")
	private PluginDescriptor descriptor;
    
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
    
    public void execute() throws MojoFailureException {
    		URLClassLoader classLoader = null;
    		try {
			classLoader = getClassLoader();
			
			Thread.currentThread().setContextClassLoader(classLoader);
			URL pxml = classLoader.getResource("META-INF/persistence.xml");
			URL hprops = classLoader.getResource("hibernate.properties");
			getLog().info("META-INF/persistence.xml found= " + (pxml!=null) + ", " + pxml);
			getLog().info("hibernate.properties found= " + (hprops!=null) + ", " + hprops);
			
//			new Thread() {
//				public void run() {
					Persistence.generateSchema(persistenceUnit, null);
//				};
//			}.start();
//			try { Thread.sleep(3000); } catch (InterruptedException e) {}
						
		} catch (DependencyResolutionRequiredException | MalformedURLException e) {
			throw new MojoFailureException(e.toString());
		} finally {
			if (classLoader!=null) {
				try { classLoader.close(); } catch (IOException e) {}
			}
		}
    		
		//Persistence.generateSchema(persistenceUnit, null);
    }

	public void setPersistenceUnit(String puName) {
		this.persistenceUnit = puName;
	}
	public void setDescriptor(PluginDescriptor descriptor) {
		this.descriptor = descriptor;
	}
	public void setProject(MavenProject project) {
		this.project = project;
	}
 }
