package io.metadew.iesi.server.rest.resource.script_execution_request.dto;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.*;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.server.rest.resource.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ScriptExecutionRequestDto extends Dto {

    private String scriptExecutionRequestId;
    private String executionRequestId;
    private List<Long> actionSelect;
    private boolean exit;
    private String impersonation;
    private String environment;
    private Map<String, String> impersonations;
    private Map<String, String> parameters;
    private ScriptExecutionRequestStatus scriptExecutionRequestStatus;
    private String scriptName;
    private Long scriptVersion;


    public ScriptExecutionRequest convertToEntity() {
        return new ScriptNameExecutionRequest(new ScriptExecutionRequestKey(scriptExecutionRequestId), new ExecutionRequestKey(executionRequestId),
                scriptName, scriptVersion, environment, actionSelect, exit, impersonation, impersonations, parameters, scriptExecutionRequestStatus);
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
