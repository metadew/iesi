package io.metadew.iesi.server.rest.executionrequest.executor;

import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.runtime.script.ScriptExecutorService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Log4j2
@ConditionalOnWebApplication
@Service
@Profile("dev")
class DevelopmentWorkerAgentExecutionRequestExecutor extends ExecutionRequestExecutor<ExecutionRequest> {

    private final ExecutionRequestConfiguration executionRequestConfiguration;
    private final ScriptExecutorService scriptExecutorService;

    @Autowired
    DevelopmentWorkerAgentExecutionRequestExecutor(ExecutionRequestConfiguration executionRequestConfiguration,
                                                   ScriptExecutorService scriptExecutorService) {
        this.executionRequestConfiguration = executionRequestConfiguration;
        this.scriptExecutorService = scriptExecutorService;
    }

    @Override
    public Class<ExecutionRequest> appliesTo() {
        return ExecutionRequest.class;
    }

    @Override
    public void execute(ExecutionRequest executionRequest) {
        executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.ACCEPTED);
        executionRequestConfiguration.update(executionRequest);

        for (ScriptExecutionRequest scriptExecutionRequest : executionRequest.getScriptExecutionRequests()) {
            log.info("Executing " + scriptExecutionRequest.toString());
            scriptExecutorService.execute(scriptExecutionRequest);
        }
    }

}
