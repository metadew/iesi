package io.metadew.iesi.metadata.definition.script.trace;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.design.key.ScriptLabelDesignTraceKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.trace.key.ScriptLabelTraceKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptLabelTrace extends Metadata<ScriptLabelTraceKey> {

    private final ScriptKey scriptKey;
    private final String name;
    private final String value;

    public ScriptLabelTrace(ScriptLabelTraceKey scriptLabelTraceKey, ScriptKey scriptKey, String name, String value) {
        super(scriptLabelTraceKey);
        this.scriptKey = scriptKey;
        this.name = name;
        this.value = value;
    }
}
