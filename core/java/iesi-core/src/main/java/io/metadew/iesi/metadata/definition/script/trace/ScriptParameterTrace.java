package io.metadew.iesi.metadata.definition.script.trace;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.ScriptParameter;
import io.metadew.iesi.metadata.definition.script.trace.key.ScriptParameterTraceKey;

public class ScriptParameterTrace extends Metadata<ScriptParameterTraceKey> {

    private String scriptParameterValue;


    public ScriptParameterTrace(ScriptParameterTraceKey scriptParameterTraceKey, String scriptParameterValue) {
        super(scriptParameterTraceKey);
        this.scriptParameterValue = scriptParameterValue;
    }

    public ScriptParameterTrace(String runId, Long processId, ScriptParameter scriptParameter) {
        this(new ScriptParameterTraceKey(runId, processId, scriptParameter.getName()), scriptParameter.getValue());
    }

    public String getScriptParameterValue() {
        return scriptParameterValue;
    }

}