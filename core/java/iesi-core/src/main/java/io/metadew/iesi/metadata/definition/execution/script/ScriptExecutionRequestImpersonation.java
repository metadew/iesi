package io.metadew.iesi.metadata.definition.execution.script;


import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestImpersonationKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScriptExecutionRequestImpersonation extends Metadata<ScriptExecutionRequestImpersonationKey> {

    private final ScriptExecutionRequestKey scriptExecutionRequestKey;
    private final ImpersonationKey impersonationKey;

    @Builder
    public ScriptExecutionRequestImpersonation(ScriptExecutionRequestImpersonationKey scriptExecutionRequestImpersonationKey,
                                               ScriptExecutionRequestKey scriptExecutionRequestKey, ImpersonationKey impersonationKey) {
        super(scriptExecutionRequestImpersonationKey);
        this.scriptExecutionRequestKey = scriptExecutionRequestKey;
        this.impersonationKey = impersonationKey;
    }

}