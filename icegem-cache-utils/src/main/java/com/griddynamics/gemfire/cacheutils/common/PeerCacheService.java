package com.griddynamics.gemfire.cacheutils.common;

import java.util.*;
import java.io.InvalidClassException;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.griddynamics.gemfire.serialization.HierarchyRegistry;
import com.griddynamics.gemfire.serialization.AutoSerializable;
import javassist.CannotCompileException;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connects to distributes system as client in order to perform some tasks on a certain regions
 */
public class PeerCacheService {
    private static final Logger log = LoggerFactory.getLogger(PeerCacheService.class);
	private ClientCache cache;
    private List<String> scanPackages = new ArrayList<String>();
	
	public PeerCacheService(String locatorOption, List<String> scanPackages) throws Exception {
        if (scanPackages != null) {
            this.scanPackages = scanPackages;
            registerSerializers();
        }
        ClientCacheFactory clientCacheFactory =  new ClientCacheFactory();
        String[] locators = locatorOption.split(",");
        for (String locator : locators) {
            String locatorHost = locator.substring(0, locator.indexOf("["));
		    String locatorPort = locator.substring(locator.indexOf("[") + 1, locator.indexOf("]"));
            clientCacheFactory.addPoolLocator(locatorHost, Integer.parseInt(locatorPort));
        }
		this.cache = clientCacheFactory.create();
	}

	public Set<Region<?, ?>> createRegions(Set<String> regionNames) {
		Set<Region<?, ?>> regions = new HashSet<Region<?, ?>>();   
		ClientRegionFactory proxyRegionFactory = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
		for (String regionName : regionNames) {
			Region region = proxyRegionFactory.create(regionName);
			regions.add(region);
		}
		return regions;
	}
	
	public Region<?, ?> createRegion(String regionName) {
		ClientRegionFactory proxyRegionFactory = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
		Region region = proxyRegionFactory.create(regionName);
		return region;
	}

	public void close() {
		cache.close();
	}

    private void registerSerializers() throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); 
        registerClasses(classLoader);
	}

    private void registerClasses(ClassLoader classLoader) throws Exception {

        List<Class<?>> classesFromPackages = new ArrayList<Class<?>>();

        for (String pack : scanPackages) {
            log.info("Scan package " + pack + " for classes marked by @AutoSerializable");
            ClassPathScanningCandidateComponentProvider ppp = new ClassPathScanningCandidateComponentProvider(false);
            ppp.addIncludeFilter(new AnnotationTypeFilter(AutoSerializable.class));
            Set<BeanDefinition> candidateComponents = ppp.findCandidateComponents(pack);
            for (BeanDefinition beanDefinition : candidateComponents) {
                String className = beanDefinition.getBeanClassName();
                final Class<?> clazz = Class.forName(className);
                classesFromPackages.add(clazz);
            }
        }

        try {
            HierarchyRegistry.registerAll(classLoader, classesFromPackages);
        } catch (InvalidClassException e) {
            final String msg = "Some class from list " + classesFromPackages + " is nor serializable. Cause: " + e.getMessage();
            log.error(msg);
            throw new RuntimeException(msg, e);
        } catch (CannotCompileException e) {
            final String msg = "Can't compile DataSerializer classes for some classes from list " + classesFromPackages + ". Cause: " + e.getMessage();
            log.error(msg);
            throw new RuntimeException(msg, e);
        }
    }

}
