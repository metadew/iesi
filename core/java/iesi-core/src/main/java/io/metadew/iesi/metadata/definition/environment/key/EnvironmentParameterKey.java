package io.metadew.iesi.metadata.definition.environment.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class EnvironmentParameterKey extends MetadataKey {

    String environmentName;
    String parameterName;

    public EnvironmentParameterKey(String environmentName, String environmentParameterName) {
        this.environmentName = environmentName;
        this.parameterName = environmentParameterName;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public String getParameterName() {
        return parameterName;
    }
}
