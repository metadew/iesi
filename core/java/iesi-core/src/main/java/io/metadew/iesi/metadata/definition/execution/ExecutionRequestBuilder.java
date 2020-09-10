package io.metadew.iesi.metadata.definition.execution;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;

import java.time.LocalDateTime;
import java.util.*;

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
    private Set<ExecutionRequestLabel> executionRequestLabels = new HashSet<>();

    public ExecutionRequestBuilder name(String name) {
        this.name = name;
        return this;
    }
    public ExecutionRequestBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ExecutionRequestBuilder description(String description) {
        this.description = description;
        return this;
    }


    public ExecutionRequestBuilder executionRequestLabels(List<ExecutionRequestLabel> executionRequestLabels) {
        this.executionRequestLabels.addAll(executionRequestLabels);
        return this;
    }

    public ExecutionRequestBuilder executionRequestLabel(ExecutionRequestLabel executionRequestLabel) {
        this.executionRequestLabels.add(executionRequestLabel);
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
                new ExecutionRequestKey(id),
                LocalDateTime.now(),
                name,
                description,
                email,
                scope,
                context,
                ExecutionRequestStatus.NEW,
                getScriptExecutionRequests().orElse(new ArrayList<>()),
                executionRequestLabels);
    }

    private AuthenticatedExecutionRequest buildAuthenticatedExecutionRequest() throws ExecutionRequestBuilderException {
        verifyMandatoryAuthenticationArguments();
        return new AuthenticatedExecutionRequest(
                new ExecutionRequestKey(id),
                LocalDateTime.now(),
                name,
                description,
                email,
                scope,
                context,
                ExecutionRequestStatus.NEW,
                getScriptExecutionRequests().orElse(new ArrayList<>()),
                executionRequestLabels,
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
