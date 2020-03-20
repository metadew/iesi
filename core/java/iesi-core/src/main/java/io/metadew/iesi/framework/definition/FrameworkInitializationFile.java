package io.metadew.iesi.framework.definition;

public class FrameworkInitializationFile {

    private String name;

    //Constructors
    public FrameworkInitializationFile() {
        this("");
    }

    public FrameworkInitializationFile(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

}