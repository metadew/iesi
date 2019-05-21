package io.metadew.iesi.metadata.definition;


public class ActionTypeParameter {

    private String name;
    private String description;
    private String type;
    private String mandatory = "N";
    private String encrypted = "N";
    private String subroutine = "";
    private String impersonate = "N";

    //Constructors
    public ActionTypeParameter() {

    }

    //Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public String getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(String encrypted) {
        this.encrypted = encrypted;
    }

    public String getSubroutine() {
        return subroutine;
    }

    public void setSubroutine(String subroutine) {
        this.subroutine = subroutine;
    }

    public String getImpersonate() {
        return impersonate;
    }

    public void setImpersonate(String impersonate) {
        this.impersonate = impersonate;
    }
}