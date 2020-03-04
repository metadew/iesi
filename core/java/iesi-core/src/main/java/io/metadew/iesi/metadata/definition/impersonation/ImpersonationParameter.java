package io.metadew.iesi.metadata.definition.impersonation;


import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationParameterKey;

public class ImpersonationParameter extends Metadata<ImpersonationParameterKey> {

    private String impersonatedConnection;
    private String description;

    //Constructors
    public ImpersonationParameter(String impersonationName, String connection, String impersonatedConnection, String description) {
        super(new ImpersonationParameterKey(impersonationName, connection));
        this.impersonatedConnection = impersonatedConnection;
        this.description = description;
    }

    public ImpersonationParameter(ImpersonationParameterKey impersonationParameterKey, String impersonatedConnection, String description){
        super(impersonationParameterKey);
        this.impersonatedConnection = impersonatedConnection;
        this.description = description;
    }

    //Getters and Setters
    public String getConnection() {
        return getMetadataKey().getParameterName();
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