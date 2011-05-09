package com.googlecode.icegem.mojo;


import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.HierarchyRegistry;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Goal which gen DataSerializers for @AutoSerializable objects
 *
 * @goal generate
 * @phase process-classes
 * @requiresDependencyResolution
 * @requiresProject
 */
public class DataSerializerGenMojo extends AbstractMojo {
    private final static Logger logger = Logger.getLogger(DataSerializerGenMojo.class.getName()); //todo: java.util.logging or other?
    /**
     * Location of the output dir for DataSerializer.
     *
     * @parameter default-value="target/classes"
     * @required
     */
    private String outputDirectory;

    /**
     * @parameter default-value="target/classes"
     * @required
     */
     private String classLocation;

    /**
     * Project classpath.
     *
     * @parameter default-value="${project.compileClasspathElements}"
     * @required
     * @readonly
     */
    private List<String> projectClasspathElements;

    /**
     * The source directories containing the sources .
     *
     * @parameter default-value="${project.compileSourceRoots}"
     * @required
     * @readonly
     */
    private List<String> compileSourceRoots;

    /**
     * path to packages where data model exists
     *
     * @parameter
     * @required
     */
    private List<String> scanPackages;


    /**
     * @parameter expression="${project}"
     * @required
     */
    protected MavenProject project;

    public void execute() throws MojoExecutionException {
        ClassLoader mojoLoader = null;
        try {
            URL[] urls = new URL[project.getCompileClasspathElements().size()];
            for(int i = 0; i < project.getCompileClasspathElements().size(); i++)
                urls[i] = new File(projectClasspathElements.get(i)).toURI().toURL();
            mojoLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Class<?>> classesFromPackages = new ArrayList<Class<?>>();
        List<Class<?>> registeredClasses = new ArrayList<Class<?>>();

        for (String pack : scanPackages) {
            String packPath = classLocation + "/" + pack.replaceAll("\\.", "/");
            File currentDir = new File(packPath);
            if (!currentDir.exists()) {
                logger.log(Level.WARNING, packPath + " doesn't exist");
                continue;
            }
            File[] files = currentDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".class");
                }
            });
            for (File f : files) {
                try {
                    String className = pack + "." + f.getName().replaceFirst(".class", "");
                    Class<?> clazz = mojoLoader.loadClass(className);
                    if (clazz.getAnnotation(AutoSerializable.class) != null) {
                        classesFromPackages.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        classesFromPackages.addAll(registeredClasses);
        logger.info("to register: " + classesFromPackages);

        try {
            HierarchyRegistry.registerAll(mojoLoader, classesFromPackages, outputDirectory);             //todo: replaced classLoader with cl
        } catch (Exception e) {
            final String msg = "Some class from list " + classesFromPackages + " is nor serializable. Cause: " + e.getMessage();
            throw new RuntimeException(msg, e);
        }
    }
}
