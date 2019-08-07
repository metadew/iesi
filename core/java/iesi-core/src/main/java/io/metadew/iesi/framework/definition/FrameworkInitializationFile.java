package io.metadew.iesi.framework.definition;

public class FrameworkInitializationFile {

    private String name = "";

    //Constructors
    public FrameworkInitializationFile() {

    }

    public FrameworkInitializationFile(String name) {
        this.name = name;
    }

    //Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}