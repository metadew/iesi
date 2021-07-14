package io.metadew.iesi.metadata.definition.script;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScriptLabel extends Metadata<ScriptLabelKey> {

    private final ScriptVersionKey scriptVersionKey;
    private final String name;
    private final String value;

    @Builder
    public ScriptLabel(ScriptLabelKey scriptLabelKey, ScriptVersionKey scriptVersionKey, String name, String value) {
        super(scriptLabelKey);
        this.scriptVersionKey = scriptVersionKey;
        this.name = name;
        this.value = value;
    }
}
