package io.metadew.iesi.server.rest.executionrequest.executor;

import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;

@Log4j2
@ConditionalOnWebApplication
abstract class ExecutionRequestExecutor<T extends ExecutionRequest> implements IExecutionRequestExecutor<T> {

}
