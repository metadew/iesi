package io.metadew.iesi.server.rest.executionrequest.executor;

import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;

public interface ExecutionRequestExecutor<T extends ExecutionRequest> {

    Class<T> appliesTo();
    void execute(T executionRequest);
}
