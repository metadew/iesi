package io.metadew.iesi.server.rest.scriptExecutionDto;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.metadata.definition.action.design.ActionDesignTrace;
import io.metadew.iesi.metadata.definition.action.design.ActionParameterDesignTrace;
import io.metadew.iesi.metadata.definition.action.design.key.ActionDesignTraceKey;
import io.metadew.iesi.metadata.definition.action.design.key.ActionParameterDesignTraceKey;
import io.metadew.iesi.metadata.definition.action.result.ActionResult;
import io.metadew.iesi.metadata.definition.action.result.ActionResultOutput;
import io.metadew.iesi.metadata.definition.action.result.key.ActionResultKey;
import io.metadew.iesi.metadata.definition.action.result.key.ActionResultOutputKey;
import io.metadew.iesi.metadata.definition.action.trace.ActionParameterTrace;
import io.metadew.iesi.metadata.definition.action.trace.ActionTrace;
import io.metadew.iesi.metadata.definition.action.trace.key.ActionParameterTraceKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecution;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.script.design.ScriptLabelDesignTrace;
import io.metadew.iesi.metadata.definition.script.design.key.ScriptLabelDesignTraceKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.definition.script.result.ScriptResultOutput;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultOutputKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.executionrequest.ExecutionRequestBuilder;
import io.metadew.iesi.server.rest.scriptExecutionDto.dto.OutputDto;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ScriptExecutionBuilder {
    // ""ScriptResults", "ScriptResultOutputs"
    // "ScriptExecutions"
    // "ScriptExecutionRequestParameters""ScriptExecutionRequests""ExecutionRequests""ExecutionRequestLabels"
    // "ActionResults" "ActionResultOutputs"

    // "ScriptLabelDesignTraces"
    // "ActionDesignTraces"
    // "ActionParameterDesignTraces""ActionParameterTraces"

    public static Map<String, Object> generateExecutionRequest(int executionRequestIndex, LocalDateTime requestTimestamp,
                                                               int labelCount, int scriptExecutionRequestCount,
                                                               String scriptName, Long scriptVersion, String scriptSecurityGroup,
                                                               String environment, int scriptExecutionRequestImpersonationCount,
                                                               int scriptExecutionRequestParameterCount,
                                                               Long processId, Long parentProcessId,
                                                               int scriptResultOutputCount, int actionResultCount) {
        LocalDateTime startTimestamp = LocalDateTime.now();
        Map<String, Object> infoMap = new HashMap<>(ExecutionRequestBuilder.generateExecutionRequest(
                executionRequestIndex,
                requestTimestamp,
                labelCount,
                scriptExecutionRequestCount,
                scriptName,
                scriptVersion,
                scriptSecurityGroup,
                environment,
                scriptExecutionRequestImpersonationCount,
                scriptExecutionRequestParameterCount));
        UUID runId = UUID.randomUUID();
        infoMap.put("runId", UUID.randomUUID());

        ScriptExecution scriptExecution = ScriptExecution.builder()
                .scriptExecutionKey(new ScriptExecutionKey(UUID.randomUUID().toString()))
                .scriptRunStatus(ScriptRunStatus.RUNNING)
                .startTimestamp(startTimestamp)
                .endTimestamp(startTimestamp.plus(1L, ChronoUnit.MILLIS))
                .scriptExecutionRequestKey(new ScriptExecutionRequestKey(infoMap.get("scriptExecutionRequest10UUID").toString()))
                .runId(runId.toString())
                .build();
        infoMap.put("scriptExecution", scriptExecution);

        ScriptResult scriptResult = ScriptResult.builder()
                .scriptResultKey(new ScriptResultKey(runId.toString(), processId))
                .parentProcessId(parentProcessId)
                .scriptName(scriptName)
                .scriptVersion(scriptVersion)
                .securityGroupName(scriptSecurityGroup)
                .environment(environment)
                .startTimestamp(startTimestamp)
                .endTimestamp(startTimestamp.plus(1L, ChronoUnit.MILLIS))
                .status(ScriptRunStatus.RUNNING)
                .scriptId(IdentifierTools.getScriptIdentifier(scriptName))
                .build();
        infoMap.put("scriptResult", scriptResult);

        IntStream.range(0, scriptResultOutputCount).boxed()
                .forEach(scriptResultOutputIndex -> {
                    ScriptResultOutput scriptResultOutput = ScriptResultOutput.builder()
                            .scriptResultOutputKey(new ScriptResultOutputKey(runId.toString(), processId, String.format("output%d", scriptResultOutputIndex)))
                            .scriptId(IdentifierTools.getScriptIdentifier(scriptName))
                            .value(String.format("outputValue%d", scriptResultOutputIndex))
                            .build();
                    infoMap.put(String.format("scriptResultOutput%d", scriptResultOutputIndex), scriptResultOutput);
                });

        IntStream.range(0, actionResultCount).boxed()
                .forEach(actionResultIndex -> {
                    ActionResult actionResult = ActionResult.builder()
                            .actionResultKey(new ActionResultKey(
                                    runId.toString(),
                                    processId + actionResultIndex + 1,
                                    String.format("action%d", 1)))
                            .environment(environment)
                            .startTimestamp(startTimestamp)
                            .endTimestamp(startTimestamp.plus(1L, ChronoUnit.MILLIS))
                            .status(ScriptRunStatus.RUNNING)
                            .scriptProcessId(processId)
                            .actionName(String.format("action%d", 1))
                            .build();
                    infoMap.put(String.format("actionResult%d", actionResultIndex), actionResult);
                });

        IntStream.range(0, actionResultCount).boxed()
                .forEach(actionResultIndex -> {
                    ActionResultOutput actionResultOutput = ActionResultOutput.builder()
                            .actionResultOutputKey(new ActionResultOutputKey(
                                    runId.toString(),
                                    processId + actionResultIndex + 1,
                                    String.format("action%d", actionResultIndex),
                                    String.format("actionOutput%d", 1)))
                            .value(String.format("actionOutputValue%d", 1))
                            .build();
                    infoMap.put(String.format("actionResultOutput%d", actionResultIndex), actionResultOutput);
                });

        IntStream.range(0, actionResultCount).boxed()
                .forEach(actionResultIndex -> {
                    ActionDesignTrace actionDesignTrace = ActionDesignTrace.builder()
                            .actionDesignTraceKey(new ActionDesignTraceKey(
                                    runId.toString(),
                                    processId + actionResultIndex + 1,
                                    String.format("action%d", actionResultIndex)))
                            .name(String.format("action%d", actionResultIndex))
                            .component("component")
                            .condition("condition")
                            .description("descriptiuon")
                            .errorExpected("N")
                            .errorStop("N")
                            .iteration("iteration")
                            .number((long) actionResultCount)
                            .retries(0)
                            .type("type")
                            .build();
                    infoMap.put(String.format("actionDesignTrace%d", actionResultIndex), actionDesignTrace);
                });

        IntStream.range(0, actionResultCount).boxed()
                .forEach(actionResultIndex -> {
                    ActionParameterDesignTrace actionParameterDesignTrace = ActionParameterDesignTrace.builder()
                            .actionParameterDesignTraceKey(new ActionParameterDesignTraceKey(
                                    runId.toString(),
                                    processId + actionResultIndex + 1,
                                    String.format("action%d", actionResultIndex),
                                    String.format("parameter%d", actionResultIndex)))
                            .value(String.format("value%d", actionResultIndex))
                            .build();
                    infoMap.put(String.format("actionParameterDesignTrace%d", actionResultIndex), actionParameterDesignTrace);
                });

        IntStream.range(0, actionResultCount).boxed()
                .forEach(actionResultIndex -> {
                    ActionParameterTrace actionParameterTrace = ActionParameterTrace.builder()
                            .metadataKey(new ActionParameterTraceKey(
                                    runId.toString(),
                                    processId + actionResultIndex + 1,
                                    String.format("action%d", actionResultIndex),
                                    String.format("parameter%d", actionResultIndex)))
                            .value(String.format("value%d", actionResultIndex))
                            .build();
                    infoMap.put(String.format("actionParameterTrace%d", actionResultIndex), actionParameterTrace);
                });

        ScriptLabelDesignTrace scriptLabelDesignTrace = ScriptLabelDesignTrace.builder()
                .scriptLabelDesignTraceKey(new ScriptLabelDesignTraceKey(
                        runId.toString(),
                        processId,
                        new ScriptLabelKey(UUID.randomUUID().toString())))
                .scriptKey(new ScriptKey(IdentifierTools.getScriptIdentifier(scriptName), scriptVersion))
                .name("label")
                .value("value")
                .build();
        infoMap.put("scriptLabelDesignTrace", scriptLabelDesignTrace);

        ScriptExecutionDto.builder()
                .status(ScriptRunStatus.RUNNING)
                .scriptId(IdentifierTools.getScriptIdentifier(scriptName))
                .processId(0L)
                .parentProcessId(-1L)
                .startTimestamp(startTimestamp)
                .endTimestamp(startTimestamp.plus(1L, ChronoUnit.MILLIS))
                .environment(environment)
                .runId(runId.toString())
                .scriptName(scriptName)
                .scriptVersion(scriptVersion)
                .output(
                        IntStream.range(0, scriptResultOutputCount).boxed()
                                .map(scriptResultOutputIndex -> OutputDto.builder()
                                        .name(String.format("output%d", scriptResultOutputIndex))
                                        .value(String.format("outputValue%d", scriptResultOutputIndex))
                                        .build())
                                .collect(Collectors.toList()))
                .inputParameters(new ArrayList<>())
                .executionLabels(new ArrayList<>())
                .designLabels(new ArrayList<>())
                .actions(new ArrayList<>())
                .build();

        return infoMap;
    }

    static void printCachedRowSet(CachedRowSet cachedRowSet) throws SQLException {
        int columnsNumber = cachedRowSet.getMetaData().getColumnCount();
        for (int i = 1; i <= columnsNumber; i++) {
            if (i > 1) System.out.print(",  ");
            String columnValue = cachedRowSet.getString(i);
            System.out.print(columnValue + " " + cachedRowSet.getMetaData().getColumnName(i));
        }
        System.out.println("");
    }
}
