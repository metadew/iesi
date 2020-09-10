package io.metadew.iesi.server.rest.executionrequest.dto;

import io.metadew.iesi.metadata.definition.execution.ExecutionRequestLabel;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestLabelKey;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

@Data
@Builder
@RequiredArgsConstructor
public class ExecutionRequestLabelDto {

    private final String name;
    private final String value;

    public ExecutionRequestLabel convertToEntity(ExecutionRequestKey executionRequestKey) {
        return new ExecutionRequestLabel(new ExecutionRequestLabelKey(DigestUtils.sha256Hex(executionRequestKey.getId()+name)), executionRequestKey, name, value);
    }

}
