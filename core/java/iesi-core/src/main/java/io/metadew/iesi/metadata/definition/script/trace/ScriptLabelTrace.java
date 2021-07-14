package io.metadew.iesi.metadata.definition.script.trace;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.definition.script.trace.key.ScriptLabelTraceKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptLabelTrace extends Metadata<ScriptLabelTraceKey> {

    private final ScriptVersionKey scriptVersionKey;
    private final String name;
    private final String value;

    public ScriptLabelTrace(ScriptLabelTraceKey scriptLabelTraceKey, ScriptVersionKey scriptVersionKey, String name, String value) {
        super(scriptLabelTraceKey);
        this.scriptVersionKey = scriptVersionKey;
        this.name = name;
        this.value = value;
    }
}
