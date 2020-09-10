package io.metadew.iesi.metadata.definition.execution;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestLabelKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExecutionRequestLabel extends Metadata<ExecutionRequestLabelKey> {

    private final ExecutionRequestKey executionRequestKey;
    private final String name;
    private final String value;

    @Builder
    public ExecutionRequestLabel(ExecutionRequestLabelKey metadataKey, ExecutionRequestKey executionRequestKey, String name, String value) {
        super(metadataKey);
        this.executionRequestKey = executionRequestKey;
        this.name = name;
        this.value = value;
    }
}
