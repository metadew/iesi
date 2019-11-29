package io.metadew.iesi.metadata.definition.environment;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;

import java.util.ArrayList;
import java.util.List;

public class Environment extends Metadata<EnvironmentKey> {

    private String description;
    private List<EnvironmentParameter> parameters;

    public Environment(String name, String description, List<EnvironmentParameter> parameters) {
        super(new EnvironmentKey(name));
        this.description = description;
        this.parameters = parameters;
    }

    public Environment(EnvironmentKey environmentKey,String description, List<EnvironmentParameter> parameters) {
        super(environmentKey);
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

    public List<EnvironmentParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<EnvironmentParameter> parameters) {
        this.parameters = parameters;
    }

	public boolean isEmpty() {
		return (getName()== null || getName().isEmpty()) ;
	}

}