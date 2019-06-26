package io.metadew.iesi.connection.java.tools;

import io.metadew.iesi.metadata.definition.*;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public final class JarTools {

    @SuppressWarnings({"resource", "rawtypes", "unchecked"})
    public static JavaArchive getJavaArchiveDefinition(String fileName, URLClassLoader urlClassLoader) {
        JavaArchive javaArchive = new JavaArchive();
        try {
            JarInputStream jarFile = new JarInputStream(new FileInputStream(fileName));
            JarEntry jarEntry;

            List<JavaClass> javaClasses = new ArrayList();
            while (true) {
                jarEntry = jarFile.getNextJarEntry();
                if (jarEntry == null) {
                    break;
                }

                JavaClass javaClass = new JavaClass();
                if ((jarEntry.getName().endsWith(".class"))) {
                    String classFileName = jarEntry.getName().replaceAll("/", "\\.");
                    String className = classFileName.substring(0, classFileName.lastIndexOf('.'));

                    Class aClass = Class.forName(className, true, urlClassLoader);
                    javaClass.setName(className);
                    javaClass.setSimpleName(aClass.getSimpleName());

                    // Fields
                    List<JavaField> javaFields = new ArrayList();
                    for (Field field : aClass.getDeclaredFields()) {
                        JavaField javaField = new JavaField();
                        javaField.setName(field.getName());

                        // Type
                        Class fieldType = field.getType();
                        javaField.setType(fieldType.getSimpleName());
                        javaField.setTypeClass(fieldType.getCanonicalName());

                        javaFields.add(javaField);
                    }
                    javaClass.setFields(javaFields);

                    // Methods
                    List<JavaMethod> javaMethods = new ArrayList();
                    for (Method method : aClass.getDeclaredMethods()) {
                        JavaMethod javaMethod = new JavaMethod();
                        javaMethod.setName(method.getName());

                        // Return Type
                        Class returnType = method.getReturnType();
                        javaMethod.setReturnType(returnType.getSimpleName());
                        javaMethod.setReturnTypeClass(returnType.getCanonicalName());

                        // Parameters
                        // Make sure to compile using -parameters
                        List<JavaParameter> javaParameters = new ArrayList();
                        for (Parameter parameter : method.getParameters()) {
                            JavaParameter javaParameter = new JavaParameter();
                            javaParameter.setName(parameter.getName());

                            // Type
                            Class parameterType = parameter.getType();
                            javaParameter.setType(parameterType.getSimpleName());
                            javaParameter.setTypeClass(parameterType.getCanonicalName());

                            javaParameters.add(javaParameter);
                        }
                        javaMethod.setParameters(javaParameters);
                        javaMethods.add(javaMethod);
                    }
                    javaClass.setMethods(javaMethods);
                }
                javaClasses.add(javaClass);
            }
            javaArchive.setClasses(javaClasses);
        } catch (Exception e) {
            throw new RuntimeException("java.jar.parse.error=" + e.getMessage());
        }
        return javaArchive;
    }

}
