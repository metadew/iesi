package io.metadew.iesi.server.rest.builder.script;

import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;

import java.util.Optional;
import java.util.UUID;

public class ScriptLabelBuilder {

    private final String scriptId;
    private final long scriptVersionNumber;
    private final String name;
    private String value;

    public ScriptLabelBuilder(String scriptId, long scriptVersionNumber, String name) {
        this.scriptId = scriptId;
        this.scriptVersionNumber = scriptVersionNumber;
        this.name = name;
    }

    public ScriptLabelBuilder value(String value) {
        this.value = value;
        return this;
    }

    public ScriptLabel build() {
        return new ScriptLabel(new ScriptLabelKey(UUID.randomUUID().toString()), new ScriptKey(scriptId, scriptVersionNumber) , name, getValue().orElse("dummy"));
    }

    private Optional<String> getValue() {
        return Optional.ofNullable(value);
    }



}
