package io.metadew.iesi.metadata.definition;


import oadd.org.apache.calcite.avatica.com.fasterxml.jackson.annotation.JsonProperty;

public class ImpersonationParameter {

    private String connection;
    private String impersonatedConnection;
    private String description;

    //Constructors
    public ImpersonationParameter() {
    }

    public ImpersonationParameter(String connection, String impersonatedConnection, String description) {
        this.connection = connection;
        this.impersonatedConnection = impersonatedConnection;
        this.description = description;
    }

    //Getters and Setters
    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getImpersonatedConnection() {
        return impersonatedConnection;
    }

    public void setImpersonatedConnection(String impersonatedConnection) {
        this.impersonatedConnection = impersonatedConnection;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}