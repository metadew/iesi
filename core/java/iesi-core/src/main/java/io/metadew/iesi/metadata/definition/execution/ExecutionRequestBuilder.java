package io.metadew.iesi.metadata.definition.execution;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.tools.IdentifierTools;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExecutionRequestBuilder {

    private String id;

    private String name;
    private String description;

    private String scope;
    private String context;

    private String space;
    private String user;
    private String password;

    private String email;

    private List<ScriptExecutionRequest> scriptExecutionRequests;

    public ExecutionRequestBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ExecutionRequestBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ExecutionRequestBuilder scriptExecutionRequests(List<ScriptExecutionRequest> scriptExecutionRequests) {
        this.scriptExecutionRequests = scriptExecutionRequests;
        return this;
    }

    public ExecutionRequestBuilder scope(String scope) {
        this.scope = scope;
        return this;
    }

    public ExecutionRequestBuilder context(String context) {
        this.context = context;
        return this;
    }

    public ExecutionRequestBuilder user(String user) {
        this.user = user;
        return this;
    }

    public ExecutionRequestBuilder space(String space) {
        this.space = space;
        return this;
    }

    public ExecutionRequestBuilder password(String password) {
        this.password = password;
        return this;
    }

    public ExecutionRequestBuilder email(String email) {
        this.email = email;
        return this;
    }

    public ExecutionRequest build() throws ExecutionRequestBuilderException {
        verifyMandatoryArguments();
        if (user != null || space != null || password != null) {
            return buildAuthenticatedExecutionRequest();
        } else {
            return buildNonAuthenticatedExecutionRequest();
        }

    }

    private NonAuthenticatedExecutionRequest buildNonAuthenticatedExecutionRequest() {
        return new NonAuthenticatedExecutionRequest(
                new ExecutionRequestKey(IdentifierTools.getExecutionRequestIdentifier()),
                LocalDateTime.now(),
                name,
                description,
                email,
                scope,
                context,
                ExecutionRequestStatus.NEW,
                getScriptExecutionRequests().orElse(new ArrayList<>()));
    }

    private AuthenticatedExecutionRequest buildAuthenticatedExecutionRequest() throws ExecutionRequestBuilderException {
        verifyMandatoryAuthenticationArguments();
        return new AuthenticatedExecutionRequest(
                new ExecutionRequestKey(IdentifierTools.getExecutionRequestIdentifier()),
                LocalDateTime.now(),
                name,
                description,
                email,
                scope,
                context,
                ExecutionRequestStatus.NEW,
                getScriptExecutionRequests().orElse(new ArrayList<>()),
                space,
                user,
                password);
    }

    private void verifyMandatoryAuthenticationArguments() throws ExecutionRequestBuilderException {
        if (user == null || password == null) {
            throw new ExecutionRequestBuilderException();
        }
    }

    private void verifyMandatoryArguments() throws ExecutionRequestBuilderException {
        if (name == null || scope == null || context == null) {
            throw new ExecutionRequestBuilderException("Execution Request must contain name, scope and context");
        }
    }

    private Optional<List<ScriptExecutionRequest>> getScriptExecutionRequests() {
        return Optional.ofNullable(scriptExecutionRequests);
    }

}
