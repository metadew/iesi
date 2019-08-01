package io.metadew.iesi.metadata.definition.script;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionTraceKey;

public class ScriptVersionTrace extends Metadata<ScriptVersionTraceKey> {

    // RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC
    private Long scriptVersionNumber;
    private String scriptVersionDescription;

    public ScriptVersionTrace(ScriptVersionTraceKey scriptVersionTraceKey, Long scriptVersionNumber, String scriptVersionDescription) {
        super(scriptVersionTraceKey);
        this.scriptVersionNumber = scriptVersionNumber;
        this.scriptVersionDescription = scriptVersionDescription;
    }

    public ScriptVersionTrace(String runId, Long processId, ScriptVersion scriptVersion) {
        this(new ScriptVersionTraceKey(runId, processId), scriptVersion.getNumber(), scriptVersion.getDescription());
    }

    public Long getScriptVersionNumber() {
        return scriptVersionNumber;
    }

    public String getScriptVersionDescription() {
        return scriptVersionDescription;
    }
}