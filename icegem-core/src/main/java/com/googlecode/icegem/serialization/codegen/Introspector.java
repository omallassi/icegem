package com.googlecode.icegem.serialization.codegen;

import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.FieldVersion;
import com.googlecode.icegem.serialization.Transient;

import java.io.InvalidClassException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * Util
 *
 * @author igolovach
 */

public class Introspector { //todo: move to CodeGenUtils

    // -------------------------- PUBLIC

    public static void checkClassIsSerialized(final Class<?> clazz) throws InvalidClassException {
        // class
        checkClassIsAnnotationMarked(clazz);
        checkClassIsPublic(clazz);
        checkClassIsNotNested(clazz);
        // constructor
        Constructor<?> constructor = checkConstructorNoArg(clazz);
        checkConstructorPublic(constructor);
        checkConstructorWithoutExceptions(constructor);
        checkParentConstructor(clazz);
        // getters
        Map<String, Method> gettersMap = getPotentialGetters(clazz);
        for (Map.Entry<String, Method> getterEntry : gettersMap.entrySet()) {
            Method getter = getterEntry.getValue();
            checkGetterPublic(getter);
            checkGetterNoArg(getter);
            checkGetterWithoutExceptions(getter);
            // todo: check getter not parametrized
            Method setter = getSetterForGetter(clazz, getter);
            checkSetterPublic(setter);
            checkSetterOneArg(setter);
            checkSetterReturnVoid(setter);
            checkSetterWithoutExceptions(setter);
            checkGetterSetterTheSameType(getter, setter);
            // todo: check setter not parametrized
        }

        // todo: check returned by getter classes?
    }

    public static List<XField> getFields(final Class<?> clazz) {
        try {
            checkClassIsSerialized(clazz);
        } catch (InvalidClassException e) {
            //todo: or InvalidClassException?
            throw new RuntimeException("Method call getFields(...) for not serialized class " + clazz.getName() + " is incorrect", e);
        }
        Map<String, Method> gettersMap = getPotentialGetters(clazz);
        final ArrayList<XField> result = new ArrayList<XField>();
        for (Map.Entry<String, Method> entry : gettersMap.entrySet()) {
            final Method method = entry.getValue();
            List annotations = getFieldAnnotation(clazz, entry.getKey());
            int fieldVersion = 1;
            for (Object annotation: annotations)
                if (annotation instanceof FieldVersion) {
                    fieldVersion = ((FieldVersion) annotation).since();
                    if (fieldVersion < 1) {
                        String fieldName = CodeGenUtils.firstLetterToLowerCase((method.getName().startsWith("is") ?
                                method.getName().substring(2) : method.getName().substring(3)));
                        throw new RuntimeException("Since value of annotation @FieldVersion must be positive, current since value = "
                                + fieldVersion + " (field '" + fieldName + "', class '"  + clazz.getName() + "')");
                    }
                    break;
                }
            boolean isBoolean = false;
            if (method.getName().startsWith("is")) {
                isBoolean = true;
            }
            result.add(new XField(entry.getKey(), method.getReturnType(), method.getDeclaringClass(), annotations, fieldVersion, isBoolean));
        }
        return result;
    }

    private static List getFieldAnnotation(Class cc, String fieldName) {
        do {
            for(int i = 0; i < cc.getDeclaredFields().length; i++)
                if (fieldName.equals(cc.getDeclaredFields()[i].getName())) {
                    try {
                        return Arrays.asList(cc.getDeclaredField(fieldName).getDeclaredAnnotations());
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
            cc = cc.getSuperclass();
        } while(cc != Object.class);
        return Collections.emptyList();
    }

    // -------------------------- PROTECTED

    /**
     * Checks if parent class (if exists) is annotated as AutoSerializable, or have parameterless public constructor .
     * @param clazz
     * @throws java.io.InvalidClassException
     */
    protected static void checkParentConstructor(Class<?> clazz) throws InvalidClassException {
        if (clazz != Object.class) {
        Class<?> parentClazz = clazz.getSuperclass();
            try {
                checkConstructorNoArg(parentClazz);
            } catch (InvalidClassException e) {
                checkClassIsAnnotationMarked(parentClazz);
            }
        }
    }

    protected static void checkGetterSetterTheSameType(Method getter, Method setter) throws InvalidClassException {
        Class<?> getterType = getter.getReturnType();
        Class<?> setterType = setter.getParameterTypes()[0];
        if (getterType != setterType) {
            throw new InvalidClassException("Setter " + setter.getName() + " in class " + setter.getDeclaringClass().getName() +
                    " return " + setter.getReturnType().getName() +
                    " but getter " + getter.getName() + " in class " + getter.getDeclaringClass().getName() +
                    " has as parameter type " + getterType);
        }
    }

    protected static void checkSetterReturnVoid(Method setter) throws InvalidClassException {
        if (setter.getReturnType() != void.class) { //todo: or Void?
            throw new InvalidClassException("Setter " + setter.getName() + " in class " + setter.getDeclaringClass().getName() + " return not void but " + setter.getReturnType().getName());
        }
    }

    private static Method getSetterForGetter(Class<?> clazz, Method getter) throws InvalidClassException {
    	String fieldName = "";
    	String name = getter.getName();
		if(name.startsWith("get")){
    		fieldName = name.substring(3, 4).toUpperCase() + name.substring(4);
    	} else if (name.startsWith("is")){
    		fieldName = name.substring(2, 3).toUpperCase() + name.substring(3);
    	}
        final String setterName = "set" + fieldName;
        Class<?> param = getter.getReturnType();
        try {
            return clazz.getMethod(setterName, param);
        } catch (NoSuchMethodException e) {
            throw new InvalidClassException("There is no public setter with param type " + param.getName() + " for getter " + name + " in class " + clazz.getName());
        }
    }

    protected static void checkSetterWithoutExceptions(Method setter) throws InvalidClassException {
        final Class<?>[] exceptions = setter.getExceptionTypes();
        if (exceptions.length != 0) {
            throw new InvalidClassException("Setter " + setter.getName() + " in class " + setter.getDeclaringClass().getName() + " throws exceptions " + asList(exceptions));
        }
    }

    protected static void checkGetterWithoutExceptions(Method getter) throws InvalidClassException {
        final Class<?>[] exceptions = getter.getExceptionTypes();
        if (exceptions.length != 0) {
            throw new InvalidClassException("Getter " + getter.getName() + " in class " + getter.getDeclaringClass().getName() + " throws exceptions " + asList(exceptions));
        }
    }

    /**
     * 1) move hierarchy up
     * 2) look for method with name
     * 3) check @TransientGetter
     *
     * @param clazz
     * @return
     */
    protected static Map<String, Method> getPotentialGetters(Class<?> clazz) { //todo: clazz.getDeclaredMethods() + hierarchy up OR clazz.getMethods()?
        Map<String, Method> result = new HashMap<String, Method>();

        if (clazz != Object.class) {
            Method[] methodArr = clazz.getMethods();
            for (Method method : methodArr) {
                if (isNameSuitableForGetter(method)) {
                    if (method.getDeclaringClass() != Object.class && !method.isBridge()) { //remove Object.getClass()
                        final Annotation[] annArr = method.getDeclaredAnnotations();
                        boolean find = false;
                        for (Annotation ann : annArr) {
                            if (ann.annotationType() == Transient.class) { //todo: ann.annotationType() or getClass()?
                                find = true;
                                break;
                            }
                        }
                        if (!find) {
                            String fieldName;
                            if (method.getReturnType() == Boolean.TYPE && method.getName().startsWith("is"))
                                fieldName = method.getName().substring(2, 3).toLowerCase() + method.getName().substring(3);
                            else
                                fieldName = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
                            result.put(fieldName, method);
                        }
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }

        return result;
    }

    private static boolean isNameSuitableForGetter(Method method) {
    	String propFirstChar = "";
        if(method.getName().startsWith("get")
                && method.getName().length() > 3){
            propFirstChar = method.getName().substring(3, 4);
        } else if(method.getReturnType() == Boolean.TYPE &&
        		method.getName().startsWith("is") &&
        		method.getName().length() > 2){
                propFirstChar = method.getName().substring(2, 3);
        }
        return propFirstChar.length()!=0 && propFirstChar.toUpperCase().equals(propFirstChar);
    }

    protected static void checkSetterPublic(Method setter) throws InvalidClassException {
        if (!Modifier.isPublic(setter.getModifiers())) {
            throw new InvalidClassException("Setter " + setter.getName() + "in class " + setter.getDeclaringClass() + " is not public but " + Modifier.toString(setter.getModifiers()));
        }
    }

    protected static void checkSetterOneArg(Method setter) throws InvalidClassException {
        Class[] types = setter.getParameterTypes();
        if (types.length != 1) {
            throw new InvalidClassException("Setter " + setter.getName() + " in class " + setter.getDeclaringClass() + " do not have 1 arg but" + types.length + ": " + asList(types));
        }
    }

    protected static void checkGetterPublic(Method getter) throws InvalidClassException {
        if (!Modifier.isPublic(getter.getModifiers())) {
            throw new InvalidClassException("Getter " + getter.getName() + " in class " + getter.getDeclaringClass() + " is not public but " + Modifier.toString(getter.getModifiers()));
        }
    }

    protected static void checkGetterNoArg(Method getter) throws InvalidClassException {
        Class[] types = getter.getParameterTypes();
//        todo: or getter.getTypeParameters() ?
        if (types.length != 0) {
            throw new InvalidClassException("Getter " + getter.getName() + " in class " + getter.getDeclaringClass() + " have arg " + asList(types));
        }
    }

    protected static void checkClassIsAnnotationMarked(final Class<?> clazz) throws InvalidClassException {
        {if (clazz.getAnnotation(AutoSerializable.class) == null)
            throw new InvalidClassException("Class " + clazz.getName() + " do not contains annotation @" + AutoSerializable.class.getSimpleName());
        }
    }

    protected static void checkClassIsPublic(final Class<?> clazz) throws InvalidClassException {
        if (!Modifier.isPublic(clazz.getModifiers())) {
            throw new InvalidClassException("Class " + clazz.getName() + " is not public but " + Modifier.toString(clazz.getModifiers()));
        }
    }

    protected static void checkClassIsNotNested(final Class<?> clazz) throws InvalidClassException {
        // There are five kinds of classes (or interfaces):
        // a) Top level classes
        // b) Nested classes (static member classes)
        // c) Inner classes (non-static member classes)
        // d) Local classes (named classes declared within a method)
        // e) Anonymous classes

        if (clazz.getEnclosingClass() != null) {
            throw new InvalidClassException("Class " + clazz.getName() + " is not top_level and has enclosing class " + clazz.getEnclosingClass().getName());
        }
    }

    protected static Constructor<?> checkConstructorNoArg(final Class<?> clazz) throws InvalidClassException {
        try {
            return clazz.getDeclaredConstructor(new Class[0]);
        } catch (NoSuchMethodException e) {
            throw new InvalidClassException("Class " + clazz.getName() + " haven't got public no-arg constructor");
        }
    }

    protected static void checkConstructorPublic(final Constructor<?> constructor) throws InvalidClassException {
        if (!Modifier.isPublic(constructor.getModifiers())) {
            throw new InvalidClassException("No-arg constructor of class " + constructor.getDeclaringClass().getName() + " is not public");
        }
    }

    protected static void checkConstructorWithoutExceptions(final Constructor<?> constructor) throws InvalidClassException {
        final Class<?>[] exceptions = constructor.getExceptionTypes();
        if (exceptions.length != 0) {
            throw new InvalidClassException("No-arg constructor of class " + constructor.getDeclaringClass().getName() + " is throws exceptions " + asList(exceptions));
        }
    }
}

//todo:remove
//class FFF {
//    public static void main(String[] args) {
//        System.out.println(Arrays.asList(A.class.getMethods()));
//    }
//}
//
//class A {
//    protected int get() {return 1;}
//}
//
//class AA extends A {
//    public int get() {return 2;}
//}