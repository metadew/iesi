package io.metadew.iesi.common.reflection;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

public final class ReflectionTools {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Class[] getClasses(String packageName) {
        Class[] classesA = null;
        try {
            ArrayList classes = new ArrayList();
            File directory = null;
            try {
                URL[] urlClassLoader = ((URLClassLoader) Thread.currentThread().getContextClassLoader()).getURLs();
                for (URL url : urlClassLoader) {
                    File file = new File(url.getPath());
                    System.out.println(url.getPath());
                    for (File classpathFile : file.listFiles()) {
                        System.out.println(classpathFile.getName());
                    }
                }
                directory = new File(
                        Thread.currentThread().getContextClassLoader().getResource(packageName.replace('.', '/')).getFile());
            } catch (NullPointerException x) {
                throw new ClassNotFoundException(packageName + " is not a valid package");
            }
            if (directory.exists()) {
                String[] files = directory.list();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].endsWith(".class")) {
                        classes.add(Class.forName(packageName + '.' + files[i].substring(0, files[i].length() - 6)));
                    }
                }
            } else {
                throw new ClassNotFoundException(packageName + " is not a valid package");
            }
            classesA = new Class[classes.size()];
            classes.toArray(classesA);

        } catch (Exception e) {

        }
        return classesA;

    }

}