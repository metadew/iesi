package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EnvironmentBuilder {

    private int numberOfParameters = 0;
    private List<EnvironmentParameter> environmentParameters = new ArrayList<>();
    private String name;
    private String description;

    public EnvironmentBuilder(String name) {
        this.name = name;
    }

    public EnvironmentBuilder description(String description) {
        this.description = description;
        return this;
    }

    public EnvironmentBuilder addEnvironmentParameter(EnvironmentParameter environmentParameter) {
        this.environmentParameters.add(environmentParameter);
        return this;
    }

    public EnvironmentBuilder numberOfParameters(int numberOfParameters) {
        this.numberOfParameters = numberOfParameters;
        return this;
    }

    public Environment build() {
        environmentParameters.addAll(IntStream.range(0, numberOfParameters)
                .boxed()
                .map(i -> new EnvironmentParameterBuilder(name ,"parameter" + i).build())
                .collect(Collectors.toList()));
        return new Environment(new EnvironmentKey(name), description == null ? "dummy" : description, environmentParameters);
    }

}
