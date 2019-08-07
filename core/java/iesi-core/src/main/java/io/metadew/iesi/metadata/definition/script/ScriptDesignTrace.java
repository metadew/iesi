package io.metadew.iesi.metadata.definition.script;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.key.ScriptDesignTraceKey;

public class ScriptDesignTrace extends Metadata<ScriptDesignTraceKey> {

    // RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC
    private Long parentProcessId;
    private String scriptId;
    private String scriptType;
    private String scriptName;
    private String scriptDescription;

    public ScriptDesignTrace(ScriptDesignTraceKey scriptDesignTraceKey, String scriptId, Long parentProcessId, String scriptType, String scriptName, String scriptDescription) {
        super(scriptDesignTraceKey);
        this.scriptId = scriptId;
        this.parentProcessId = parentProcessId;
        this.scriptType = scriptType;
        this.scriptName = scriptName;
        this.scriptDescription = scriptDescription;
    }

    public ScriptDesignTrace(String runId, Long processId, Long parentProcessId, Script script) {
        this(new ScriptDesignTraceKey(runId, processId), script.getId(), parentProcessId, script.getType(), script.getName(), script.getDescription());
    }

    public Long getParentProcessId() {
        return parentProcessId;
    }

    public String getScriptType() {
        return scriptType;
    }

    public String getScriptName() {
        return scriptName;
    }

    public String getScriptDescription() {
        return scriptDescription;
    }

    public String getScriptId() {
        return scriptId;
    }
}