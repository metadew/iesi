package io.metadew.iesi.metadata.definition;

import java.util.List;

public class JavaClass {

    private String name;
    private String simpleName;
    private List<JavaField> fields;
    private List<JavaMethod> methods;

    // Constructors
    public JavaClass() {

    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public List<JavaField> getFields() {
        return fields;
    }

    public void setFields(List<JavaField> fields) {
        this.fields = fields;
    }

    public List<JavaMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<JavaMethod> methods) {
        this.methods = methods;
    }


}