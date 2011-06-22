package com.googlecode.icegem.serialization.codegen;

import java.util.List;

/**
 * @author igolovach
 */

public class CodeGenUtils {
    public static String TAB = "    ";

    public static String tab(String str) {
        return tab(1, str);
    }

    public static String tab(int tabCount, String str) {
        String[] arr = str.split("\n");
        StringBuilder result = new StringBuilder();
        for (String s : arr) {
            for (int i = 1; i <= tabCount; i++) {
                result.append(TAB);
            }
            result.append(s).append("\n");
        }
        if (!str.endsWith("\n")) {
            return result.substring(0, result.length() - 1);
        }
        return result.toString();
    }

    public static String firstLetterToUpperCase(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public static String firstLetterToLowerCase(String fieldName) {
        return fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
    }

    /**
     * <p> Integer[][].class -> "Integer[][]"
     * <p> int[][].class -> "int[][]"
     * <p> Naive: Integer[][].class.getName() -> "[[Ljava.lang.Integer;"
     * <p> Naive: int[][].class.getName() -> "[[LI;"
     */
    public static String className(Class<?> clazz) { //todo: rename
        //todo: is that correct algorithm?
        final String naiveName = clazz.getName();
        if (!clazz.isArray()) { //todo: what about enum
            return naiveName;
        } else {
            int count = 0;
            String ending = "";
            while (clazz.isArray()) {
                count++;
                ending += "[]";
                clazz = clazz.getComponentType();
            }
            if (clazz.isPrimitive()) {
                String primitiveClassName;
                if (clazz == boolean.class) {
                    primitiveClassName = "boolean";
                } else if (clazz == byte.class) {
                    primitiveClassName = "byte";
                } else if (clazz == char.class) {
                    primitiveClassName = "char";
                } else if (clazz == short.class) {
                    primitiveClassName = "short";
                } else if (clazz == int.class) {
                    primitiveClassName = "int";
                } else if (clazz == long.class) {
                    primitiveClassName = "long";
                } else if (clazz == float.class) {
                    primitiveClassName = "float";
                } else if (clazz == double.class) {
                    primitiveClassName = "double";
                } else {
                    throw new RuntimeException("Never here! - You try to generate code for Void[]...[]: clazz = " + clazz);
                }
                return primitiveClassName + ending;
            } else {
                return naiveName.substring(count + 1, naiveName.length() - 1) + ending;
            }
        }
    }

    /**
     * Returns a hash code of class model for specified bean version based on fields that this model contains.
     * Class model is defined by an ordered list of class's fields and represented as a string.
     * This string is formed by appending a field's type and name to it.
     *
     * Note: Use XClass.getSerialisedSortedFields() method for providing a constant order of the fields for this method.
     *
     * @param classFields an ordered list of class's fields.
     * @param beanVersion version of the bean
     * @return int
     */
    public static int getClassModelHashCodeBasedOnClassFields(List<XField> classFields, int beanVersion) {
        StringBuilder builder = new StringBuilder();
        for (XField field : classFields) {
            if (beanVersion == -1 || beanVersion >= field.getFieldVersion()) {
                builder.append(field.getType()).append(field.getName());
            }
        }
        return builder.toString().hashCode();
    }
}
