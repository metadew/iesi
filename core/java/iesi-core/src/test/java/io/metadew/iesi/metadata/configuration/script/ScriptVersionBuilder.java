package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;

import java.time.LocalDateTime;
import java.util.Optional;


public class ScriptVersionBuilder {

    private final String scriptId;
    private final long versionNumber;
    private String description;

    public ScriptVersionBuilder(String scriptId, long versionNumber) {
        this.scriptId = scriptId;
        this.versionNumber = versionNumber;
    }

    public ScriptVersionBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ScriptVersion build() {
        return new ScriptVersion(new ScriptVersionKey(
                new ScriptKey(scriptId, versionNumber)),
                getDescription().orElse("dummy"), null,
                null);
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }



}
