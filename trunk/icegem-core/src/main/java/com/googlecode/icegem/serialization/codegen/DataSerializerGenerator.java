package com.googlecode.icegem.serialization.codegen;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.BeanVersion;
import com.googlecode.icegem.serialization.Configuration;

import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Class that generate for class XYZ.class special serializer:
 * class XYZDataSerializer extends DataSerializer {...}
 * by Javassist lib.
 * </pre>
 *
 * @author igolovach
 */

public class DataSerializerGenerator {

    private static Logger logger = LoggerFactory.getLogger(DataSerializerGenerator.class);

    /**
     * All DataSerializer-s will extend this class.
     */
    public static final String PARENT_CLASS = "com.gemstone.gemfire.DataSerializer";

    private static final Map<Integer, String> dataSerializerID2ClassNameMap = new HashMap<Integer, String>();
    private static CodeGenerationListener listener;

    private static ClassPool newClassPool() {
        ClassPool result = new ClassPool(null); // arg - parent ClassPool
        result.appendClassPath(new ClassClassPath(java.lang.Object.class)); // its equivalent of appendSystemPath();
        result.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader())); //todo: ok?

        return result;
    }

      private static ClassPool newClassPool(ClassLoader loader) {
        ClassPool result = new ClassPool(null); // arg - parent ClassPool
        result.appendClassPath(new ClassClassPath(java.lang.Object.class)); // its equivalent of appendSystemPath();
        result.appendClassPath(new LoaderClassPath(loader)); //todo: ok?

        return result;
    }

    public static synchronized List<Class<?>> generateDataSerializerClasses(ClassLoader classLoader, Class<?>... classArray) throws CannotCompileException, InvalidClassException {
        return generateDataSerializerClasses(classLoader, Arrays.asList(classArray), null);
    }

    public static synchronized List<Class<?>> generateDataSerializerClasses(ClassLoader classLoader, List<Class<?>> classList) throws CannotCompileException, InvalidClassException {
        return generateDataSerializerClasses(classLoader, classList, null);
    }

    /**
     * 2-stage compilation scheme of group of classes:
     * <p> hack from http://www.csg.is.titech.ac.jp/~chiba/javassist/tutorial/tutorial2.html#add
     * <p> order of return of serializer-classes in response corresponds to order of arg-classes
     */
    public static synchronized List<Class<?>> generateDataSerializerClasses(ClassLoader classLoader, List<Class<?>> classList, String outputDir) throws CannotCompileException, InvalidClassException {
        checkClassesValid(classList);

        List<CtClass> dataSerializerClassList = new ArrayList<CtClass>();

        // create new ClassPool for every method call
        // 1) low memory consumption - any caches with internal data structures of created classes
        // 2) any collision between methods called serially - all crated classes are forgotten
        // 3) any references on created classes from this lib
        ClassPool classPool = newClassPool(classLoader);

        // #1: create dataSerializers with stubs toData/fromData
        for (Class<?> clazz : classList) {
            // create class
            CtClass cc = createClass(classPool, clazz);
            dataSerializerClassList.add(cc);
            //add statis Register
            addStaticRegister(clazz, cc);
            // add methods
            addMethodGetId(clazz, cc);
            addMethodGetSupportedClasses(clazz, cc);
            // add stub-methods toData/fromData
            addMethodToDataStub(clazz, cc);
            addMethodFromDataStub(clazz, cc);
            // we need this for Javassist do some magic with CtClass
            try {
                cc.toBytecode();
            } catch (IOException e) {
                throw new CannotCompileException("Error during end of compilation phase #1 (call CtClass.toBytecode() for some Javassist-magic with CtClass) for " + cc.getName(), e);
            } catch (CannotCompileException e) {
                throw new CannotCompileException("Error during end of compilation phase #1 (call CtClass.toBytecode() for some Javassist-magic with CtClass) for " + cc.getName(), e);
            }
        }

        List<Class<?>> result = new ArrayList<Class<?>>();
        // #2: change stubs toData/fromData in dataSerializers -> real methods
        for (int k = 0; k < classList.size(); k++) {
            Class<?> clazz = classList.get(k);
            CtClass cc = dataSerializerClassList.get(k);
            // make changing methods real: defrost CtClass
            cc.defrost();
            // change stub toData/fromData -> real methods
            addMethodToData(clazz, cc);
            addMethodFromData(clazz, cc);
            // generate result
            final Class resultClass;
            try {
                resultClass = cc.toClass(classLoader, null); // ProtectionDomain == null
                logger.info("compiled data serializer for class: {}; id: {}; version: {}",
                    new Object[]{clazz, clazz.getAnnotation(AutoSerializable.class).dataSerializerID()
                    , clazz.getAnnotation(BeanVersion.class).value()});
                if (outputDir != null && outputDir.length() > 0) {
                    try {
                        cc.writeFile(outputDir);
                    } catch (IOException e) {
                        throw new RuntimeException("couldn't save DataSerializer for class " + clazz.getName(), e);
                    }
                }
            } catch (CannotCompileException e) {
                throw new CannotCompileException("Error during end of compilation phase #2 (call CtClass.toClass()) for " + cc.getName() + ". Probably you second time try generate and load DataSerializer class " + cc.getName() + " for class " + clazz.getName(), e);
            }

            // dump code to listener
            if (listener != null) {
                listener.generated(clazz.getName(), cc.getName(), new ClassProcessor().process(new XClass(clazz)));
            }

            result.add(resultClass);
        }

        return result;
    }

    //todo: what with thread-safeting?

    public static synchronized void registerCodeGenerationListener(CodeGenerationListener l) {
        listener = l;
    }

    // ------------------------ PRIVATE

    private static void checkClassesValid(List<Class<?>> classList) throws InvalidClassException {
        // check classes is valid for generating DataSerializer, if not - throw NotSerializableException with detailed reason
        for (Class<?> clazz : classList) {
            Introspector.checkClassIsSerialized(clazz);
        }

        // check classes do not contain duplicated @AutoSerializable.dataSerializerID
        checkDataSerializerIDIsUnique(classList);
    }

    private static void checkDataSerializerIDIsUnique(List<Class<?>> classList) throws InvalidClassException {
        for (Class<?> clazz : classList) {
            AutoSerializable annotation = clazz.getAnnotation(AutoSerializable.class);
            int dataSerializerID = annotation.dataSerializerID();
            if (dataSerializerID2ClassNameMap.containsKey(dataSerializerID)) {
                throw new InvalidClassException("Classes " + dataSerializerID2ClassNameMap.get(dataSerializerID) + " and " + clazz.getName() + " contain duplicated value of @AutoSerializable.dataSerializerID: " + dataSerializerID); // todo: right ex type?
            }
            dataSerializerID2ClassNameMap.put(dataSerializerID, clazz.getName());
        }
    }

    private static String createDataSerializerClassNameForClass(Class<?> clazz) {
        String dataSerializerPackage = Configuration.getCurrent().getDataSerializerPackage();
        return dataSerializerPackage + "." + clazz.getName() + "DataSerializer";
    }

    private static CtClass createClass(ClassPool classPool, Class<?> baseClass) throws CannotCompileException {
        final String newSerializerClassName = createDataSerializerClassNameForClass(baseClass);
        final CtClass parentClass;
        try {
            parentClass = classPool.get(PARENT_CLASS);
        } catch (NotFoundException e) {
            throw new CannotCompileException("There is no " + DataSerializerGenerator.PARENT_CLASS + " in classpath of context ClassLoader for " + baseClass.getName(), e); //todo: correlation with line: classPool.insertClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        }

        try {
            return classPool.makeClass(newSerializerClassName, parentClass);
        } catch (RuntimeException e) { // javadoc:makeClass(): if the existing class is frozen.
            throw new CannotCompileException("There is some internal error in our code (probably class " + newSerializerClassName + " exists and frozen) for " + baseClass.getName(), e);
        }
    }

     private static void addStaticRegister(Class<?> baseClass, CtClass cc) throws CannotCompileException{
        final String src = "com.gemstone.gemfire.DataSerializer.register(" + cc.getName() +".class);";
        CtConstructor staticConstructor;
        try {
            staticConstructor = cc.makeClassInitializer();
            staticConstructor.insertBefore(src);
        } catch (CannotCompileException e) {
            throw new CannotCompileException(formatMsg("Cann't add static block for class ", src, baseClass, cc), e);
        }
    }

    private static void addMethodGetId(Class<?> baseClass, CtClass cc) throws CannotCompileException {
        XClass xClass = new XClass(baseClass);
        final String src = new MethodGetIdProcessor().process(xClass);
        CtMethod methodGetId;
        try {
            methodGetId = CtNewMethod.make(src, cc, null, null);
        } catch (CannotCompileException e) {
            throw new CannotCompileException(formatMsg("Can't compile method '.getId()'\n", src, baseClass, cc), e);
        }
        try {
            cc.addMethod(methodGetId);
        } catch (CannotCompileException e) {
            throw new CannotCompileException(formatMsg("Can compile but can't add compiled method '.getId()'\n", src, baseClass, cc), e);
        }
    }

    private static void addMethodGetSupportedClasses(Class<?> baseClass, CtClass cc) throws CannotCompileException {
        XClass xClass = new XClass(baseClass);
        final String src = new MethodGetSupportedClassesProcessor().process(xClass);
        CtMethod methodGetSupportedClasses;
        try {
            methodGetSupportedClasses = CtNewMethod.make(src, cc, null, null);
        } catch (CannotCompileException e) {
            throw new CannotCompileException(formatMsg("Can't compile method '.getSupportedClasses()'\n", src, baseClass, cc), e);
        }
        try {
            cc.addMethod(methodGetSupportedClasses);
        } catch (CannotCompileException e) {
            throw new CannotCompileException(formatMsg("Can compile but can't add compiled method '.getSupportedClasses()'\n", src, baseClass, cc), e);
        }
    }

    private static void addMethodToDataStub(Class<?> baseClass, CtClass cc) throws CannotCompileException {
        XClass xClass = new XClass(baseClass);
        final String src = new MethodToDataStubProcessor().process(xClass);
        CtMethod methodToData;
        try {
            methodToData = CtNewMethod.make(src, cc, null, null);
        } catch (CannotCompileException e) {
            throw new CannotCompileException(formatMsg("Can't compile stub method '.toData()' (compilation phase #1)\n", src, baseClass, cc), e);
        }
        try {
            cc.addMethod(methodToData);
        } catch (CannotCompileException e) {
            throw new CannotCompileException(formatMsg("Can compile stub but can't add compiled method '.toData()' (compilation phase #1)\n", src, baseClass, cc), e);
        }
    }

    private static void addMethodFromDataStub(Class<?> baseClass, CtClass cc) throws CannotCompileException {
        XClass xClass = new XClass(baseClass);
        final String src = new MethodFromDataStubProcessor().process(xClass);
        CtMethod methodFromData;
        try {
            methodFromData = CtNewMethod.make(src, cc, null, null);
        } catch (CannotCompileException e) {
            throw new CannotCompileException(formatMsg("Can't compile stub method '.fromData(...)' (compilation phase #1)\n", src, baseClass, cc), e);
        }
        try {
            cc.addMethod(methodFromData);
        } catch (CannotCompileException e) {
            throw new CannotCompileException(formatMsg("Can compile stub but can't add compiled method '.fromData(...)' (compilation phase #1)\n", src, baseClass, cc), e);
        }
    }

    private static void addMethodToData(Class<?> baseClass, CtClass cc) throws CannotCompileException {
        XClass xClass = new XClass(baseClass);
        final String src = new MethodToDataProcessor().process(xClass);
        // compile version #2
        CtMethod methodToData;
        try {
            methodToData = CtNewMethod.make(src, cc, null, null);
        } catch (CannotCompileException e) {
            throw new CannotCompileException(formatMsg("Can't compile method '.toData()' (compilation phase #2)\n", src, baseClass, cc), e);
        }
        // find version #1
        CtMethod removedStubMethod;
        try {
            removedStubMethod = cc.getDeclaredMethod("toData");
        } catch (NotFoundException e) {
            throw new CannotCompileException(formatMsg("Can't find stub method '.toData()' (from compilation phase #1)\n", src, baseClass, cc), e);
        }
        // remove version #1
        try {
            cc.removeMethod(removedStubMethod);
        } catch (NotFoundException e) {
            throw new CannotCompileException(formatMsg("Can find but can't remove stub method '.toData()' (from compilation phase #1)\n", src, baseClass, cc), e);
        }
        // add version #2
        try {
            cc.addMethod(methodToData);
        } catch (CannotCompileException e) {
            throw new CannotCompileException(formatMsg("Can compile new, find old, remove old but can't add new compiled method '.toData()' (compilation phase #2)\n", src, baseClass, cc), e);
        }
    }

    private static void addMethodFromData(Class<?> baseClass, CtClass cc) throws CannotCompileException {
        XClass xClass = new XClass(baseClass);
        final String src = new MethodFromDataProcessor().process(xClass);
        // compile version #2
        CtMethod methodFromData;
        try {
            methodFromData = CtNewMethod.make(src, cc, null, null);
        } catch (CannotCompileException e) {
            throw new CannotCompileException(formatMsg("Can't compile method '.fromData(...)' (compilation phase #2)\n", src, baseClass, cc), e);
        }
        // find version #1
        CtMethod removedStubMethod;
        try {
            removedStubMethod = cc.getDeclaredMethod("fromData");
        } catch (NotFoundException e) {
            throw new CannotCompileException(formatMsg("Can't find stub method '.fromData(...)' (from compilation phase #1)\n", src, baseClass, cc), e);
        }
        // remove version #1
        try {
            cc.removeMethod(removedStubMethod);
        } catch (NotFoundException e) {
            throw new CannotCompileException(formatMsg("Can find but can't remove stub method '.fromData(...)' (from compilation phase #1)\n", src, baseClass, cc), e);
        }
        // add version #2
        try {
            cc.addMethod(methodFromData);
        } catch (CannotCompileException e) {
            throw new CannotCompileException(formatMsg("Can compile new, find old, remove old but can't add new compiled method '.fromData(...)' (compilation phase #2)\n", src, baseClass, cc), e);
        }
    }

    private static String formatMsg(String headerMsg, String methodSrc, Class<?> baseClass, CtClass cc) {
        return headerMsg + "\n"
                + "source:\n"
                + methodSrc + "\n"
                + "method generated for DataSerializer for class: " + baseClass + "\n"
                + "partially created class of DataSerializer: " + cc + "\n";
    }
}