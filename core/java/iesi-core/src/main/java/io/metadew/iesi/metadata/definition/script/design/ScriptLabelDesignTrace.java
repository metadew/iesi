package io.metadew.iesi.metadata.definition.script.design;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.design.key.ScriptLabelDesignTraceKey;
import io.metadew.iesi.metadata.definition.script.design.key.ScriptParameterDesignTraceKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptLabelDesignTrace extends Metadata<ScriptLabelDesignTraceKey> {

    private final ScriptKey scriptKey;
    private final String name;
    private final String value;

    public ScriptLabelDesignTrace(ScriptLabelDesignTraceKey scriptLabelDesignTraceKey, ScriptKey scriptKey, String name, String value) {
        super(scriptLabelDesignTraceKey);
        this.scriptKey = scriptKey;
        this.name = name;
        this.value = value;
    }
}
