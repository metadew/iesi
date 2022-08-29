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

    private String userId;
    private String username;

    private String email;
    private boolean debugMode;

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

    public ExecutionRequestBuilder debugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }
    /*public ExecutionRequestBuilder user(String user) {
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
    }*/

    public ExecutionRequestBuilder userId(String userId) {
        this.userId = userId;
        return this;
    }

    public ExecutionRequestBuilder username(String username) {
       this.username = username;
       return this;
    }

    public ExecutionRequestBuilder email(String email) {
        this.email = email;
        return this;
    }

    public ExecutionRequest build() throws ExecutionRequestBuilderException {
        verifyMandatoryArguments();
        if (userId != null || username != null) {
            return buildAuthenticatedExecutionRequest();
        } else {
            return buildNonAuthenticatedExecutionRequest();
        }

    }

    private NonAuthenticatedExecutionRequest buildNonAuthenticatedExecutionRequest() {
//        if (securityGroupName == null) {
//            securityGroupName = "iesi";
//        }
//        SecurityGroup securityGroup = SecurityGroupConfiguration.getInstance().getByName(securityGroupName)
//                .orElseThrow(() -> new RuntimeException(String.format("Cannot find security group %s ", securityGroupName)));

        return new NonAuthenticatedExecutionRequest(
                new ExecutionRequestKey(id),
                // securityGroup.getMetadataKey(),
                // securityGroup.getName(),
                LocalDateTime.now(),
                name,
                description,
                email,
                scope,
                context,
                debugMode,
                ExecutionRequestStatus.NEW,
                getScriptExecutionRequests().orElse(new ArrayList<>()),
                executionRequestLabels);
    }

    private AuthenticatedExecutionRequest buildAuthenticatedExecutionRequest() throws ExecutionRequestBuilderException {
        verifyMandatoryAuthenticationArguments();
//        if (securityGroupName == null) {
//            securityGroupName = "iesi";
//        }
//        SecurityGroup securityGroup = SecurityGroupConfiguration.getInstance().getByName(securityGroupName)
//                .orElseThrow(() -> new RuntimeException(String.format("Cannot find security group %s ", securityGroupName)));

        return new AuthenticatedExecutionRequest(
                new ExecutionRequestKey(id),
                // securityGroup.getMetadataKey(),
                // securityGroup.getName(),
                LocalDateTime.now(),
                name,
                description,
                email,
                scope,
                context,
                debugMode,
                ExecutionRequestStatus.NEW,
                getScriptExecutionRequests().orElse(new ArrayList<>()),
                executionRequestLabels,
                userId,
                username
                );
    }

    private void verifyMandatoryAuthenticationArguments() throws ExecutionRequestBuilderException {
        if (userId == null || username == null) {
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
