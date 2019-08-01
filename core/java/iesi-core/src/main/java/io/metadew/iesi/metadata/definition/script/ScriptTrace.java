package io.metadew.iesi.metadata.definition.script;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.key.ScriptTraceKey;

public class ScriptTrace extends Metadata<ScriptTraceKey> {

    // RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC
    private Long parentProcessId;
    private String scriptId;
    private String scriptType;
    private String scriptName;
    private String scriptDescription;

    public ScriptTrace(ScriptTraceKey scriptTraceKey, String scriptId, Long parentProcessId, String scriptType, String scriptName, String scriptDescription) {
        super(scriptTraceKey);
        this.scriptId = scriptId;
        this.parentProcessId = parentProcessId;
        this.scriptType = scriptType;
        this.scriptName = scriptName;
        this.scriptDescription = scriptDescription;
    }

    public ScriptTrace(String runId, Long processId, Long parentProcessId, Script script) {
        this(new ScriptTraceKey(runId, processId), script.getId(), parentProcessId, script.getType(), script.getName(), script.getDescription());
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