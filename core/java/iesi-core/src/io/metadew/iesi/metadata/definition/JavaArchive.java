package io.metadew.iesi.metadata.definition;

import java.util.List;

public class JavaArchive {

    private String name;
    private String path;
    private List<JavaClass> classes;

    // Constructors
    public JavaArchive() {

    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<JavaClass> getClasses() {
        return classes;
    }

    public void setClasses(List<JavaClass> classes) {
        this.classes = classes;
    }


}