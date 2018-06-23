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
import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.internal.util.ClassLoaderHelper;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;
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
    
    public void execute() throws MojoFailureException {
    		getLog().warn("Here!");
    		URLClassLoader classLoader = null;
    		try {
    			List<String> elements = project.getTestClasspathElements();
    			getLog().info("elements.size=" + elements.size());
    			URL[] urls = new URL[elements.size()];
			for (int i=0; i<elements.size(); i++) {
				String path = elements.get(i);
				//getLog().info("cp element: " + path);
				URL url = new File(path).toURI().toURL();
				//getLog().info("url: " + url);
      			//descriptor.getClassRealm().addURL(url);
				urls[i] = url;
			}
			classLoader = new URLClassLoader(urls, getClass().getClassLoader());
			for (URL url: classLoader.getURLs()) {
				getLog().info("url=" + url.toString());
			}
			
			getLog().info("META-INF/persistence.xml found= " + (classLoader.findResource("META-INF/persistence.xml")!=null));
			getLog().info("hibernate.properties found= " + (classLoader.findResource("hibernate.properties")!=null));

			Map<String, Object> props = new HashMap<>();
			props.put(AvailableSettings.CLASSLOADERS, Collections.singletonList(classLoader));
			List<ParsedPersistenceXmlDescriptor> pus = PersistenceXmlParser.locatePersistenceUnits(props);
			getLog().info("found " + pus.size() + " persistence units");
			for (ParsedPersistenceXmlDescriptor x: pus) {
				getLog().debug(x.getName() + ", classLoader: " + x.getClassLoader() + ", root: " + x.getPersistenceUnitRootUrl());				
			}
			
			final ClassLoader floader = classLoader;
			ClassLoaderHelper.overridenClassLoader = floader;
			boolean generated = new HibernatePersistenceProvider() {
				protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilderOrNull(String persistenceUnitName, Map properties, ClassLoader providedClassLoader) {
					return super.getEntityManagerFactoryBuilderOrNull(persistenceUnitName, properties, floader);
				}
			}.generateSchema(persistenceUnit, props);
			if (!generated) {
				throw new MojoFailureException("provider did not generate schema");
			}
			
//			Class<?> p = classLoader.loadClass("javax.persistence.Persistence");
//			Method m = p.getDeclaredMethod("generateSchema", String.class, Map.class);
//			getLog().info("urlLoader: " + classLoader);
//			getLog().info("defaultLoader: " + getClass().getClassLoader());
//			getLog().info("urlLoader.parent: " + classLoader.getParent());
//			getLog().info(p.getName() + " was loaded by " + p.getClassLoader());
//			m.invoke(null, persistenceUnit, null);
		} catch (DependencyResolutionRequiredException | MalformedURLException e) {
			throw new MojoFailureException(e.toString());
//		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
//			throw new MojoFailureException(e.toString());
//		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//			throw new MojoFailureException(e.toString());
		} catch (IOException e) {
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
