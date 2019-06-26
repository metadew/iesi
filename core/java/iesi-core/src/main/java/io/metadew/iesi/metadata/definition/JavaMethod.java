package io.metadew.iesi.metadata.definition;

import java.util.List;

public class JavaMethod {

    private String name;
    private String returnType;
    private String returnTypeClass;
    private List<JavaParameter> parameters;

    // Constructors
    public JavaMethod() {

    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JavaParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<JavaParameter> parameters) {
        this.parameters = parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getReturnTypeClass() {
        return returnTypeClass;
    }

    public void setReturnTypeClass(String returnTypeClass) {
        this.returnTypeClass = returnTypeClass;
    }


}