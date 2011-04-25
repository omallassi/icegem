package com.googlecode.icegem.serialization.spring;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.HierarchyRegistry;

import javassist.CannotCompileException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author igolovach
 */
public class AutoSerializableRegistrarBean implements InitializingBean {
    private final static Logger logger = Logger.getLogger(AutoSerializableRegistrarBean.class.getName()); //todo: java.util.logging or other?

    private List<Class<?>> registeredClasses = new ArrayList<Class<?>>();
    private List<String> scanPackages = new ArrayList<String>();

    public void setRegisteredClasses(List<Class<?>> registeredClasses) {
        this.registeredClasses = registeredClasses;
    }

    public void setScanPackages(List<String> scanPackages) {
        this.scanPackages = scanPackages;
    }

    public void afterPropertiesSet() throws Exception {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); //todo: ok?
        registerClasses(classLoader);
    }

    private void registerClasses(ClassLoader classLoader) throws ClassNotFoundException {

        List<Class<?>> classesFromPackages = new ArrayList<Class<?>>();

        for (String pack : scanPackages) {
            logger.info("Scan package " + pack + " for classes marked by @AutoSerializable");
            ClassPathScanningCandidateComponentProvider ppp = new ClassPathScanningCandidateComponentProvider(false);
            ppp.addIncludeFilter(new AnnotationTypeFilter(AutoSerializable.class));
            Set<BeanDefinition> candidateComponents = ppp.findCandidateComponents(pack);
            for (BeanDefinition beanDefinition : candidateComponents) {
                String className = beanDefinition.getBeanClassName();
                final Class<?> clazz = Class.forName(className);
                classesFromPackages.add(clazz);
            }
        }

        classesFromPackages.addAll(registeredClasses);
        logger.info("All classes that will be registered in GemFire: " + registeredClasses);

        try {
            HierarchyRegistry.registerAll(classLoader, classesFromPackages);
        } catch (InvalidClassException e) {
            final String msg = "Some class from list " + classesFromPackages + " is nor serializable. Cause: " + e.getMessage();
            logger.severe(msg);
            throw new RuntimeException(msg, e);
        } catch (CannotCompileException e) {
            final String msg = "Can't compile DataSerializer classes for some classes from list " + classesFromPackages + ". Cause: " + e.getMessage();
            logger.severe(msg);
            throw new RuntimeException(msg, e);
        }
    }
}
