package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.metadata.configuration.action.ActionBuilder;
import io.metadew.iesi.metadata.configuration.script.ScriptParameterBuilder;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentParameterKey;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
