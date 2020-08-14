package io.metadew.iesi.server.rest.executionrequest.script.dto;

import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestParameter;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestParameterKey;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

@Data
@Builder
@RequiredArgsConstructor
public class ScriptExecutionRequestParameterDto {

    private final String name;
    private final String value;

    public ScriptExecutionRequestParameter convertToEntity(ScriptExecutionRequestKey scriptExecutionRequestKey) {
        return new ScriptExecutionRequestParameter(
                new ScriptExecutionRequestParameterKey(DigestUtils.sha256Hex(scriptExecutionRequestKey.getId()+name)),
                scriptExecutionRequestKey, name, value);
    }

}
