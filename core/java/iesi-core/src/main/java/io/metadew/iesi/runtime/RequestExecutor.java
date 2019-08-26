package io.metadew.iesi.runtime;

import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;

public interface RequestExecutor<T extends ExecutionRequest> {

    public Class<T> appliesTo();
    public void execute(T executionRequest);
}
