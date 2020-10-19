package io.metadew.iesi.metadata.definition.environment;


import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentParameterKey;
import lombok.Builder;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class EnvironmentParameter extends Metadata<EnvironmentParameterKey> {
    private String value;

    //Constructors
    @Builder
    public EnvironmentParameter(EnvironmentParameterKey environmentParameterKey, String value) {
        super(environmentParameterKey);
        this.value = value;
    }

    public EnvironmentParameter(String environmentName, String environmentParameterName, String value) {
        super(new EnvironmentParameterKey(new EnvironmentKey(environmentName), environmentParameterName));
        this.value = value;
    }



    //Getters and Setters
    public String getName() {
        return getMetadataKey().getParameterName();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}