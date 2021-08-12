package io.metadew.iesi.server.rest.executionrequest.executor;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.NonAuthenticatedExecutionRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class NonAuthenticatedExecutionRequestExecutor
extends ExecutionRequestExecutor<NonAuthenticatedExecutionRequest>
        implements IExecutionRequestExecutor<NonAuthenticatedExecutionRequest> {

    @Autowired
    public NonAuthenticatedExecutionRequestExecutor(ExecutionRequestConfiguration executionRequestConfiguration,
                                                    Configuration iesiProperties) {
        super(executionRequestConfiguration, iesiProperties);
    }

    @Override
    public Class<NonAuthenticatedExecutionRequest> appliesTo() {
        return NonAuthenticatedExecutionRequest.class;
    }

    public void checkUserAccess(NonAuthenticatedExecutionRequest nonAuthenticatedExecutionRequest) {
    }

}
