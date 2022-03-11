package io.metadew.iesi.server.rest.executionrequest.executor;

import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;

import java.util.concurrent.CompletableFuture;

public interface IExecutionRequestExecutorService {

    CompletableFuture<Boolean> execute(ExecutionRequest executionRequest);

}
