package io.metadew.iesi.metadata.definition.environment.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class EnvironmentParameterKey extends MetadataKey {

    private EnvironmentKey environmentKey;
    private String parameterName;

    public EnvironmentParameterKey(String environmentName, String environmentParameterName) {
        this.environmentKey = new EnvironmentKey(environmentName);
        this.parameterName = environmentParameterName;
    }

    public EnvironmentParameterKey(EnvironmentKey environmentKey, String environmentParameterName) {
        this.environmentKey = environmentKey;
        this.parameterName = environmentParameterName;
    }

    public EnvironmentKey getEnvironmentKey() {
        return environmentKey;
    }

    public String getParameterName() {
        return parameterName;
    }
}
