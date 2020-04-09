package io.metadew.iesi.server.rest.resource.script_execution_request.dto;

import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestParameter;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestParameterKey;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

@Data
public class ScriptExecutionRequestParameterDto {

    private final String name;
    private final String value;

    public ScriptExecutionRequestParameter convertToEntity(ScriptExecutionRequestKey scriptExecutionRequestKey) {
        return new ScriptExecutionRequestParameter(
                new ScriptExecutionRequestParameterKey(DigestUtils.sha256Hex(scriptExecutionRequestKey.getId()+name)),
                scriptExecutionRequestKey, name, value);
    }

}
