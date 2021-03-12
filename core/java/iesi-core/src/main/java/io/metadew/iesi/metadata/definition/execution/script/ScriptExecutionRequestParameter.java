package io.metadew.iesi.metadata.definition.execution.script;


import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestParameterKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScriptExecutionRequestParameter extends Metadata<ScriptExecutionRequestParameterKey> {

    private final ScriptExecutionRequestKey scriptExecutionRequestKey;
    private final String name;
    private String value;

    @Builder
    public ScriptExecutionRequestParameter(ScriptExecutionRequestParameterKey scriptExecutionRequestParameterKey,
                                           ScriptExecutionRequestKey scriptExecutionRequestKey, String name, String value) {
        super(scriptExecutionRequestParameterKey);
        this.scriptExecutionRequestKey = scriptExecutionRequestKey;
        this.name = name;
        this.value = value;
    }

}