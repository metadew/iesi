package io.metadew.iesi.server.rest.executionrequest;

import io.metadew.iesi.metadata.definition.execution.AuthenticatedExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestLabel;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestLabelKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestImpersonation;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestParameter;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptNameExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestImpersonationKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestParameterKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.dto.ExecutionRequestLabelDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestImpersonationDto;
import io.metadew.iesi.server.rest.executionrequest.script.dto.ScriptExecutionRequestParameterDto;
import io.metadew.iesi.server.rest.script.dto.ScriptDto;
import io.metadew.iesi.server.rest.script.dto.version.ScriptVersionDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ExecutionRequestBuilder {

    public static Map<String, Object> generateExecutionRequest(int executionRequestIndex, LocalDateTime requestTimestamp,
                                                               int labelCount, int scriptExecutionRequestCount,
                                                               String scriptName, Long scriptVersion, String scriptSecurityGroup,
                                                               String environment, int scriptExecutionRequestImpersonationCount,
                                                               int scriptExecutionRequestParameterCount) {
        Map<String, Object> info = new HashMap<>();
        UUID executionRequestId = UUID.randomUUID();
        info.put("executionRequestUUID", executionRequestId);
        ExecutionRequest executionRequest = AuthenticatedExecutionRequest.builder()
                .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                .requestTimestamp(requestTimestamp)
                .context("context")
                .email("email")
                .scope("scope")
                .name("name")
                .description("description")
                .executionRequestLabels(
                        IntStream.range(0, labelCount).boxed()
                                .map(labelIndex -> {
                                    UUID executionRequestLabelUUID = UUID.randomUUID();
                                    info.put(String.format("label%d%dUUID", executionRequestIndex, labelIndex), executionRequestLabelUUID);
                                    ExecutionRequestLabel executionRequestLabel = ExecutionRequestLabel.builder()
                                            .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                            .metadataKey(new ExecutionRequestLabelKey(executionRequestLabelUUID.toString()))
                                            .name(String.format("label%s", labelIndex))
                                            .value(String.format("value%s", labelIndex))
                                            .build();
                                    info.put(String.format("label%d%d", executionRequestIndex, labelIndex), executionRequestLabel);
                                    return executionRequestLabel;
                                })
                                .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(
                        IntStream.range(0, scriptExecutionRequestCount).boxed()
                                .map(scriptExecutionRequestIndex -> {
                                    UUID scriptExecutionRequestUUID = UUID.randomUUID();
                                    info.put(String.format("scriptExecutionRequest%d%dUUID", executionRequestIndex, scriptExecutionRequestIndex), scriptExecutionRequestUUID);
                                    ScriptNameExecutionRequest scriptNameExecutionRequest = ScriptNameExecutionRequest.builder()
                                            .environment(environment)
                                            .exit(false)
                                            .executionRequestKey(new ExecutionRequestKey(executionRequestId.toString()))
                                            .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestUUID.toString()))
                                            .impersonations(
                                                    IntStream.range(0, scriptExecutionRequestImpersonationCount).boxed()
                                                            .map(scriptExecutionRequestImpersonationIndex -> {
                                                                UUID scriptExecutionRequestImpersonationUUID = UUID.randomUUID();
                                                                info.put(String.format("scriptExecutionRequestImpersonation%d%d%dUUID", executionRequestIndex, scriptExecutionRequestIndex, scriptExecutionRequestImpersonationIndex), scriptExecutionRequestImpersonationUUID);
                                                                ScriptExecutionRequestImpersonation scriptExecutionRequestImpersonation = ScriptExecutionRequestImpersonation.builder()
                                                                        .impersonationKey(new ImpersonationKey("impersonation"))
                                                                        .scriptExecutionRequestImpersonationKey(new ScriptExecutionRequestImpersonationKey(scriptExecutionRequestImpersonationUUID.toString()))
                                                                        .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestUUID.toString()))
                                                                        .build();
                                                                info.put(String.format("scriptExecutionRequestImpersonation%d%d%d", executionRequestIndex, scriptExecutionRequestIndex, scriptExecutionRequestImpersonationIndex), scriptExecutionRequestImpersonation);
                                                                return scriptExecutionRequestImpersonation;
                                                            })
                                                            .collect(Collectors.toSet()))
                                            .scriptName(scriptName)
                                            .scriptVersion(scriptVersion)
                                            .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                            .parameters(
                                                    IntStream.range(0, scriptExecutionRequestParameterCount).boxed()
                                                            .map(scriptExecutionRequestParameterIndex -> {
                                                                UUID scriptExecutionRequestParameterUUID = UUID.randomUUID();
                                                                info.put(String.format("scriptExecutionRequestParameter%d%d%dUUID", executionRequestIndex, scriptExecutionRequestIndex, scriptExecutionRequestParameterIndex), scriptExecutionRequestParameterUUID);
                                                                ScriptExecutionRequestParameter scriptExecutionRequestParameter = ScriptExecutionRequestParameter.builder()
                                                                        .scriptExecutionRequestKey(new ScriptExecutionRequestKey(scriptExecutionRequestUUID.toString()))
                                                                        .scriptExecutionRequestParameterKey(new ScriptExecutionRequestParameterKey(scriptExecutionRequestParameterUUID.toString()))
                                                                        .name(String.format("param%d%d%d", executionRequestIndex, scriptExecutionRequestIndex, scriptExecutionRequestParameterIndex))
                                                                        .value(String.format("value%d%d%d", executionRequestIndex, scriptExecutionRequestIndex, scriptExecutionRequestParameterIndex))
                                                                        .build();
                                                                info.put(String.format("scriptExecutionRequestParameter%d%d%d", executionRequestIndex, scriptExecutionRequestIndex, scriptExecutionRequestParameterIndex), scriptExecutionRequestParameter);
                                                                return scriptExecutionRequestParameter;
                                                            })
                                                            .collect(Collectors.toSet()))
                                            .build();
                                    info.put(String.format("scriptExecutionRequest%d%d", executionRequestIndex, scriptExecutionRequestIndex), scriptNameExecutionRequest);
                                    return scriptNameExecutionRequest;
                                }).collect(Collectors.toList()))
                .userID("userId")
                .username("username")
                .build();
        info.put("executionRequest", executionRequest);

        Script script = Script.builder()
                .scriptKey(new ScriptKey(IdentifierTools.getScriptIdentifier(scriptName), scriptVersion))
                .description("description")
                .actions(new ArrayList<>())
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(IdentifierTools.getScriptIdentifier(scriptName), scriptVersion))
                                .scriptLabelKey(new ScriptLabelKey(UUID.randomUUID().toString()))
                                .name("label1")
                                .value("value1")
                                .build()
                ).collect(Collectors.toList()))
                .parameters(new ArrayList<>())
                .name(scriptName)
                .version(new ScriptVersion(scriptName, scriptVersion, "description"))
                .securityGroupKey(new SecurityGroupKey(UUID.randomUUID()))
                .securityGroupName(scriptSecurityGroup)
                .createdBy("username")
                .createdAt(requestTimestamp.toString())
                .build();
        info.put("script", script);

        ExecutionRequestDto executionRequestDto = ExecutionRequestDto.builder()
                .requestTimestamp(requestTimestamp)
                .executionRequestId(executionRequestId.toString())
                .context("context")
                .scope("scope")
                .description("description")
                .email("email")
                .name("name")
                .userId("userId")
                .username("username")
                .executionRequestLabels(IntStream.range(0, labelCount).boxed()
                        .map(labelIndex -> {
                                    ExecutionRequestLabelDto executionRequestLabelDto = ExecutionRequestLabelDto.builder()
                                            .name(String.format("label%s", labelIndex))
                                            .value(String.format("value%s", labelIndex))
                                            .build();
                                    info.put(String.format("labelDto%d%d", executionRequestIndex, labelIndex), executionRequestLabelDto);
                                    return executionRequestLabelDto;
                                }
                        )
                        .collect(Collectors.toSet()))
                .executionRequestStatus(ExecutionRequestStatus.NEW)
                .scriptExecutionRequests(
                        IntStream.range(0, scriptExecutionRequestCount).boxed()
                                .map(scriptExecutionRequestIndex -> {
                                            ScriptExecutionRequestDto scriptExecutionRequestDto = ScriptExecutionRequestDto.builder()
                                                    .environment(environment)
                                                    .exit(false)
                                                    .executionRequestId(executionRequestId.toString())
                                                    .scriptExecutionRequestId(info.get(String.format("scriptExecutionRequest%d%dUUID", executionRequestIndex, scriptExecutionRequestIndex)).toString())
                                                    .scriptName(scriptName)
                                                    .scriptVersion(scriptVersion)
                                                    .securityGroupName(scriptSecurityGroup)
                                                    .scriptExecutionRequestStatus(ScriptExecutionRequestStatus.NEW)
                                                    .impersonations(
                                                            IntStream.range(0, scriptExecutionRequestImpersonationCount).boxed()
                                                                    .map(scriptExecutionRequestImpersonationIndex -> {
                                                                        ScriptExecutionRequestImpersonationDto scriptExecutionRequestImpersonationDto = ScriptExecutionRequestImpersonationDto.builder()
                                                                                .name("impersonation")
                                                                                .build();
                                                                        info.put(String.format("scriptExecutionRequestImpersonationDto%d%d%d", executionRequestIndex, scriptExecutionRequestIndex, scriptExecutionRequestImpersonationIndex), scriptExecutionRequestImpersonationDto);
                                                                        return scriptExecutionRequestImpersonationDto;
                                                                    })
                                                                    .collect(Collectors.toSet()))
                                                    .parameters(
                                                            IntStream.range(0, scriptExecutionRequestParameterCount).boxed()
                                                                    .map(scriptExecutionRequestParameterIndex -> {
                                                                        ScriptExecutionRequestParameterDto scriptExecutionRequestParameterDto = ScriptExecutionRequestParameterDto.builder()
                                                                                .name(String.format("param%d%d%d", executionRequestIndex, scriptExecutionRequestIndex, scriptExecutionRequestParameterIndex))
                                                                                .value(String.format("value%d%d%d", executionRequestIndex, scriptExecutionRequestIndex, scriptExecutionRequestParameterIndex))
                                                                                .build();
                                                                        info.put(String.format("scriptExecutionRequestParameterDto%d%d%d", executionRequestIndex, scriptExecutionRequestIndex, scriptExecutionRequestParameterIndex), scriptExecutionRequestParameterDto);
                                                                        return scriptExecutionRequestParameterDto;
                                                                    })
                                                                    .collect(Collectors.toSet()))
                                                    .build();
                                            info.put(String.format("scriptExecutionRequestDto%d%d", executionRequestIndex, scriptExecutionRequestIndex), scriptExecutionRequestDto);
                                            return scriptExecutionRequestDto;
                                        }

                                ).collect(Collectors.toSet()))
                .build();
        info.put("executionRequestDto", executionRequestDto);

        ScriptDto scriptDto = ScriptDto.builder()
                .description("description")
                .actions(new HashSet<>())
                .labels(new HashSet<>())
                .parameters(new HashSet<>())
                .name(scriptName)
                .version(new ScriptVersionDto(scriptVersion, "description"))
                .securityGroupName(scriptSecurityGroup)
                .build();
        info.put("scriptDto", scriptDto);

        return info;
    }
}
