package io.metadew.iesi.metadata.definition.script.design;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.design.key.ScriptParameterDesignTraceKey;

public class ScriptParameterDesignTrace extends Metadata<ScriptParameterDesignTraceKey> {

    private String scriptParameterValue;

    public ScriptParameterDesignTrace(ScriptParameterDesignTraceKey scriptParameterDesignTraceKey, String scriptParameterValue) {
        super(scriptParameterDesignTraceKey);
        this.scriptParameterValue = scriptParameterValue;
    }

    public ScriptParameterDesignTrace(String runId, Long processId, ScriptParameter scriptParameter) {
        this(new ScriptParameterDesignTraceKey(runId, processId, scriptParameter.getMetadataKey().getParameterName()), scriptParameter.getValue());
    }

    public String getScriptParameterValue() {
        return scriptParameterValue;
    }
}