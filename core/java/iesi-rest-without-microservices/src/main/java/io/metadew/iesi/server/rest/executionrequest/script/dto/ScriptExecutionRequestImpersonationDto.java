package io.metadew.iesi.server.rest.executionrequest.script.dto;

import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestImpersonation;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestImpersonationKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

@Data
public class ScriptExecutionRequestImpersonationDto {

    private final String name;

    public ScriptExecutionRequestImpersonation convertToEntity(ScriptExecutionRequestKey scriptExecutionRequestKey) {
        return new ScriptExecutionRequestImpersonation(
                new ScriptExecutionRequestImpersonationKey(DigestUtils.sha256Hex(scriptExecutionRequestKey.getId()+name)),
                scriptExecutionRequestKey, new ImpersonationKey(name));
    }

}
