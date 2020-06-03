package io.metadew.iesi.server.rest.builder.script;

import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;

import java.util.Optional;

public class ScriptParameterBuilder {

    private final String scriptId;
    private final long scriptVersionNumber;
    private final String parameterName;
    private String value;

    public ScriptParameterBuilder(String scriptId, long scriptVersionNumber, String actionParameterName) {
        this.scriptId = scriptId;
        this.scriptVersionNumber = scriptVersionNumber;
        this.parameterName = actionParameterName;
    }

    public ScriptParameterBuilder value(String value) {
        this.value = value;
        return this;
    }

    public ScriptParameter build() {
        return new ScriptParameter(new ScriptParameterKey(new ScriptKey(scriptId, scriptVersionNumber), parameterName), getValue().orElse("dummy"));
    }

    private Optional<String> getValue() {
        return Optional.ofNullable(value);
    }



}
