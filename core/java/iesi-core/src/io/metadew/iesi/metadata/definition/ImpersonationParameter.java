package io.metadew.iesi.metadata.definition;


public class ImpersonationParameter {

    private String connection;
    private String impersonatedConnection;
    private String description;

    //Constructors
    public ImpersonationParameter() {

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