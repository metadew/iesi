package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptParameterKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;

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
        return new ScriptParameter(new ScriptParameterKey(new ScriptVersionKey(new ScriptKey(scriptId), scriptVersionNumber, "NA"), parameterName), getValue().orElse("dummy"));
    }

    private Optional<String> getValue() {
        return Optional.ofNullable(value);
    }



}
