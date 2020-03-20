package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentParameterKey;

public class EnvironmentParameterBuilder {

    private final String parameterName;
    private String name;
    private String value;

    public EnvironmentParameterBuilder(String name, String parameterName) {
        this.name = name;
        this.parameterName = parameterName;
    }

    public EnvironmentParameterBuilder value(String value) {
        this.value = value;
        return this;
    }

    public EnvironmentParameter build() {
        return new EnvironmentParameter(new EnvironmentParameterKey(name, parameterName), value == null ? "dummy" : value);
    }

}
