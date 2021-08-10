package io.metadew.iesi.server.rest.executionrequest.executor;

import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.NonAuthenticatedExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class NonAuthenticatedExecutionRequestExecutor implements ExecutionRequestExecutor<NonAuthenticatedExecutionRequest> {

    private final ExecutionRequestConfiguration executionRequestConfiguration;

    @Autowired
    public NonAuthenticatedExecutionRequestExecutor(ExecutionRequestConfiguration executionRequestConfiguration) {
        this.executionRequestConfiguration = executionRequestConfiguration;
    }

    @Override
    public Class<NonAuthenticatedExecutionRequest> appliesTo() {
        return NonAuthenticatedExecutionRequest.class;
    }

    @Override
    public void execute(NonAuthenticatedExecutionRequest executionRequest) {
        executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.ACCEPTED);
        executionRequestConfiguration.update(executionRequest);

        for (ScriptExecutionRequest scriptExecutionRequest : executionRequest.getScriptExecutionRequests()) {
            //TODO: start the script as a async separate process and follow up
            log.info("Executing " + scriptExecutionRequest.toString());
        }
    }
}
