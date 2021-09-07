package io.metadew.iesi.server.rest.executionrequest.executor;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.guard.GuardConfiguration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.AuthenticatedExecutionRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@ConditionalOnWebApplication
public class AuthenticatedExecutionRequestExecutor
    extends ExecutionRequestExecutor<AuthenticatedExecutionRequest>
        implements IExecutionRequestExecutor<AuthenticatedExecutionRequest> {

    private final boolean authenticationEnabled;

    @Autowired
    public AuthenticatedExecutionRequestExecutor(ExecutionRequestConfiguration executionRequestConfiguration,
                                                 GuardConfiguration guardConfiguration,
                                                 Configuration iesiProperties) {
        super(executionRequestConfiguration, iesiProperties);
        this.authenticationEnabled = guardConfiguration.getGuardSetting("authenticate")
                .map(s -> s.equalsIgnoreCase("y"))
                .orElseThrow(() -> new RuntimeException("no value set for guard.authenticate"));
    }

    @Override
    public Class<AuthenticatedExecutionRequest> appliesTo() {
        return AuthenticatedExecutionRequest.class;
    }


    public void checkUserAccess(AuthenticatedExecutionRequest authenticatedExecutionRequest) {
    }

}
