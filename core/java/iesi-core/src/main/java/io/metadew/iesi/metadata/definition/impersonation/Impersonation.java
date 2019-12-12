package io.metadew.iesi.metadata.definition.impersonation;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;

import java.util.ArrayList;
import java.util.List;

public class Impersonation extends Metadata<ImpersonationKey> {

    private String description;
    private List<ImpersonationParameter> parameters = new ArrayList<>();


    public Impersonation(String name, String description, List<ImpersonationParameter> parameters) {
        super(new ImpersonationKey(name));
        this.description = description;
        this.parameters = parameters;
    }

    public Impersonation(ImpersonationKey impersonationKey, String description, List<ImpersonationParameter> parameters) {
        super(impersonationKey);
        this.description = description;
        this.parameters = parameters;
    }

    //Getters and Setters
    public String getName() {
        return getMetadataKey().getName();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ImpersonationParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ImpersonationParameter> parameters) {
        this.parameters = parameters;
    }

}