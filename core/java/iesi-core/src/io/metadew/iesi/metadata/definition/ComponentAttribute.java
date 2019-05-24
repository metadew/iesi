package io.metadew.iesi.metadata.definition;


public class ComponentAttribute {

    private String name;
    private String environment;
    private String value;

    //Constructors
    public ComponentAttribute() {

    }

    public ComponentAttribute(String environment, String name, String value) {
        this.name = name;
        this.environment = environment;
        this.value = value;
    }

    //Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

}