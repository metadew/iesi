package io.metadew.iesi.runtime;

import io.metadew.iesi.common.configuration.guard.GuardConfiguration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.AuthenticatedExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.runtime.script.ScriptExecutorService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AuthenticatedExecutionRequestExecutor implements ExecutionRequestExecutor<AuthenticatedExecutionRequest> {

    private final Boolean authenticationEnabled;

    private static AuthenticatedExecutionRequestExecutor INSTANCE;

    public synchronized static AuthenticatedExecutionRequestExecutor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AuthenticatedExecutionRequestExecutor();
        }
        return INSTANCE;
    }

    private AuthenticatedExecutionRequestExecutor() {
        this.authenticationEnabled = GuardConfiguration.getInstance().getGuardSetting("authenticate")
                .map(s -> s.equalsIgnoreCase("y"))
                .orElseThrow(() -> new RuntimeException("no value set for guard.authenticate"));
    }

    @Override
    public Class<AuthenticatedExecutionRequest> appliesTo() {
        return AuthenticatedExecutionRequest.class;
    }

    @Override
    public void execute(AuthenticatedExecutionRequest executionRequest) throws Exception {
        if (authenticationEnabled) {
            checkUserAccess(executionRequest);
        } else {
            log.info("authentication.disabled:access automatically granted");
        }
        executionRequest.setExecutionRequestStatus(ExecutionRequestStatus.ACCEPTED);
        ExecutionRequestConfiguration.getInstance().update(executionRequest);

        for (ScriptExecutionRequest scriptExecutionRequest : executionRequest.getScriptExecutionRequests()) {
            ScriptExecutorService.getInstance().execute(scriptExecutionRequest);
        }

    }

    private void checkUserAccess(AuthenticatedExecutionRequest executionRequest) {

    }
}
