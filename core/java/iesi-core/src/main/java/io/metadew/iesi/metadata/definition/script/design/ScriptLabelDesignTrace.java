package io.metadew.iesi.metadata.definition.script.design;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.design.key.ScriptLabelDesignTraceKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptLabelDesignTrace extends Metadata<ScriptLabelDesignTraceKey> {

    private final ScriptVersionKey scriptVersionKey;
    private final String name;
    private final String value;

    @Builder
    public ScriptLabelDesignTrace(ScriptLabelDesignTraceKey scriptLabelDesignTraceKey, ScriptVersionKey scriptVersionKey, String name, String value) {
        super(scriptLabelDesignTraceKey);
        this.scriptVersionKey = scriptVersionKey;
        this.name = name;
        this.value = value;
    }
}
