package io.metadew.iesi.server.rest.resource.script_execution_request.dto;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.*;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.server.rest.resource.Dto;
import lombok.*;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class ScriptExecutionRequestDto extends Dto {

    private String scriptExecutionRequestId;
    private String executionRequestId;
    private boolean exit;
    private String impersonation;
    private String environment;
    private Map<String, String> impersonations;
    private Map<String, String> parameters;
    private ScriptExecutionRequestStatus scriptExecutionRequestStatus;
    private String scriptName;
    private Long scriptVersion;

    public ScriptExecutionRequestDto(String scriptExecutionRequestId, String executionRequestId, boolean exit,
                                     String impersonation, String environment, Map<String, String> impersonations,
                                     Map<String, String> parameters, ScriptExecutionRequestStatus scriptExecutionRequestStatus,
                                     String scriptName, Long scriptVersion) {
        this.scriptExecutionRequestId = scriptExecutionRequestId;
        this.executionRequestId = executionRequestId;
        this.exit = exit;
        this.impersonation = impersonation;
        this.environment = environment;
        this.impersonations = impersonations;
        this.parameters = parameters;
        this.scriptExecutionRequestStatus = scriptExecutionRequestStatus;
        this.scriptName = scriptName;
        this.scriptVersion = scriptVersion;
    }

    public ScriptExecutionRequest convertToEntity() {
        return new ScriptNameExecutionRequest(new ScriptExecutionRequestKey(scriptExecutionRequestId),
                new ExecutionRequestKey(executionRequestId), scriptName, scriptVersion, environment, exit, impersonation, impersonations, parameters, scriptExecutionRequestStatus);
    }

    public ScriptExecutionRequest convertToNewEntity(String executionRequestId) throws ScriptExecutionRequestBuilderException {
        return new ScriptExecutionRequestBuilder()
                .mode("script")
                .executionRequestKey(new ExecutionRequestKey(executionRequestId))
                .environment(environment)
                .exit(exit)
                .impersonation(impersonation)
                .impersonations(impersonations)
                .parameters(parameters)
                .scriptName(scriptName)
                .scriptVersion(scriptVersion)
                .build();
    }
}
