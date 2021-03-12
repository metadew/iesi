package io.metadew.iesi.metadata.definition.script.design;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.design.key.ScriptDesignTraceKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScriptDesignTrace extends Metadata<ScriptDesignTraceKey> {

    private Long parentProcessId;
    private String scriptId;
    private String scriptName;
    private String scriptDescription;
    private String securityGroupName;

    @Builder
    public ScriptDesignTrace(ScriptDesignTraceKey scriptDesignTraceKey, String scriptId, Long parentProcessId, String scriptName, String scriptDescription, String securityGroupName) {
        super(scriptDesignTraceKey);
        this.scriptId = scriptId;
        this.parentProcessId = parentProcessId;
        this.scriptName = scriptName;
        this.scriptDescription = scriptDescription;
        this.securityGroupName = securityGroupName;
    }

    public ScriptDesignTrace(String runId, Long processId, Long parentProcessId, Script script) {
        this(new ScriptDesignTraceKey(runId, processId), script.getMetadataKey().getScriptId(), parentProcessId, script.getName(), script.getDescription(), script.getSecurityGroupName());
    }

}