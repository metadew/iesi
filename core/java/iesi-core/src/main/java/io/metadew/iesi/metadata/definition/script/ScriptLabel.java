package io.metadew.iesi.metadata.definition.script;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptLabel extends Metadata<ScriptLabelKey> {

    private final ScriptKey scriptKey;
    private final String name;
    private final String value;

    public ScriptLabel(ScriptLabelKey scriptLabelKey, ScriptKey scriptKey, String name, String value) {
        super(scriptLabelKey);
        this.scriptKey = scriptKey;
        this.name = name;
        this.value = value;
    }
}
