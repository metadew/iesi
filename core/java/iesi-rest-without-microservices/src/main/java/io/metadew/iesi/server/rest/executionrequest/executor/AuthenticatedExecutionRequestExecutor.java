package io.metadew.iesi.server.rest.executionrequest.executor;

import io.metadew.iesi.common.configuration.guard.GuardConfiguration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.AuthenticatedExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class AuthenticatedExecutionRequestExecutor implements ExecutionRequestExecutor<AuthenticatedExecutionRequest> {

    private final boolean authenticationEnabled;
    private final ExecutionRequestConfiguration executionRequestConfiguration;

    @Autowired
    public AuthenticatedExecutionRequestExecutor(ExecutionRequestConfiguration executionRequestConfiguration) {
        this.executionRequestConfiguration = executionRequestConfiguration;
        this.authenticationEnabled = GuardConfiguration.getInstance().getGuardSetting("authenticate")
                .map(s -> s.equalsIgnoreCase("y"))
                .orElseThrow(() -> new RuntimeException("no value set for guard.authenticate"));
    }

    @Override
    public Class<AuthenticatedExecutionRequest> appliesTo() {
        return AuthenticatedExecutionRequest.class;
    }

    @Override
    public void execute(AuthenticatedExecutionRequest executionRequest) {
        if (authenticationEnabled) {
            checkUserAccess(executionRequest);
        } else {
            log.info("authentication.disabled:access automatically granted");
        }
        executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.ACCEPTED);
        executionRequestConfiguration.update(executionRequest);

        for (ScriptExecutionRequest scriptExecutionRequest : executionRequest.getScriptExecutionRequests()) {
            //TODO: start the script as a async separate process and follow up
            log.info("Executing " + scriptExecutionRequest.toString());
        }

    }

    private void checkUserAccess(AuthenticatedExecutionRequest executionRequest) {

    }
}
