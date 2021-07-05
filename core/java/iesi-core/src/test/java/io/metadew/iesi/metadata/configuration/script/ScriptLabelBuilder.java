package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;

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
        // needs to be a predictable key (hash) to ensure they can be loaded from filesystem
        return new ScriptLabel(new ScriptLabelKey(DigestUtils.sha256Hex(scriptId+ scriptVersionNumber +name)), new ScriptVersionKey(new ScriptKey(scriptId), scriptVersionNumber, "NA") , name, getValue().orElse("dummy"));
    }

    private Optional<String> getValue() {
        return Optional.ofNullable(value);
    }



}
