package io.metadew.iesi.metadata.definition.script.trace;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.trace.key.ScriptTraceKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScriptTrace extends Metadata<ScriptTraceKey> {

    // RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC
    private Long parentProcessId;
    private String scriptId;
    private String scriptName;
    private String scriptDescription;
    private String securityGroupName;

    @Builder
    public ScriptTrace(ScriptTraceKey scriptTraceKey, String scriptId, Long parentProcessId, String scriptName, String scriptDescription, String securityGroupName) {
        super(scriptTraceKey);
        this.scriptId = scriptId;
        this.parentProcessId = parentProcessId;
        this.scriptName = scriptName;
        this.scriptDescription = scriptDescription;
        this.securityGroupName = securityGroupName;
    }

    public ScriptTrace(String runId, Long processId, Long parentProcessId, Script script) {
        this(new ScriptTraceKey(runId, processId), script.getMetadataKey().getScriptId(), parentProcessId, script.getName(), script.getDescription(), script.getSecurityGroupName());
    }

}