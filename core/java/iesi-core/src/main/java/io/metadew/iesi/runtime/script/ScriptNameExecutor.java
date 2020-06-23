package io.metadew.iesi.runtime.script;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionConfiguration;
import io.metadew.iesi.metadata.configuration.impersonation.ImpersonationConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestParameter;
import io.metadew.iesi.metadata.definition.execution.script.ScriptNameExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.execution.ScriptExecutionBuilder;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
public class ScriptNameExecutor implements ScriptExecutor<ScriptNameExecutionRequest> {

    private static ScriptNameExecutor INSTANCE;

    public synchronized static ScriptNameExecutor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptNameExecutor();
        }
        return INSTANCE;
    }

    private ScriptNameExecutor() {
    }

    @Override
    public Class<ScriptNameExecutionRequest> appliesTo() {
        return ScriptNameExecutionRequest.class;
    }

    @Override
    public void execute(ScriptNameExecutionRequest scriptExecutionRequest) {

        Script script = scriptExecutionRequest.getScriptVersion()
                .map(scriptVersion -> ScriptConfiguration.getInstance().get(new ScriptKey(IdentifierTools.getScriptIdentifier(scriptExecutionRequest.getScriptName()), scriptVersion)))
                .orElse(ScriptConfiguration.getInstance().getLatestVersion(scriptExecutionRequest.getScriptName()))
                .orElseThrow(() -> new MetadataDoesNotExistException(new ScriptKey(IdentifierTools.getScriptIdentifier(scriptExecutionRequest.getScriptName()), scriptExecutionRequest.getScriptVersion().orElse(-1L))));

        Map<String, String> impersonations = new HashMap<>();
        scriptExecutionRequest.getImpersonations()
                .forEach(scriptExecutionRequestImpersonation -> ImpersonationConfiguration.getInstance().get(scriptExecutionRequestImpersonation.getImpersonationKey())
                        .ifPresent(impersonation -> impersonation.getParameters()
                                .forEach(impersonationParameter -> impersonations.put(impersonationParameter.getMetadataKey().getParameterName(), impersonationParameter.getImpersonatedConnection()))));

        ScriptExecution scriptExecution = new ScriptExecutionBuilder(true, false)
                .script(script)
                .exitOnCompletion(scriptExecutionRequest.isExit())
                .parameters(scriptExecutionRequest.getParameters().stream()
                        .collect(Collectors.toMap(ScriptExecutionRequestParameter::getName, ScriptExecutionRequestParameter::getValue)))
                .impersonations(impersonations)
                .environment(scriptExecutionRequest.getEnvironment())
                .build();

        io.metadew.iesi.metadata.definition.execution.script.ScriptExecution scriptExecution1 =
                new io.metadew.iesi.metadata.definition.execution.script.ScriptExecution(new ScriptExecutionKey(IdentifierTools.getScriptExecutionRequestIdentifier()),
                        scriptExecutionRequest.getMetadataKey(), scriptExecution.getExecutionControl().getRunId(),
                        ScriptRunStatus.RUNNING, LocalDateTime.now(), null);
        ScriptExecutionConfiguration.getInstance().insert(scriptExecution1);
        scriptExecution.execute();
        scriptExecution1.updateScriptRunStatus(ScriptResultConfiguration.getInstance().get(new ScriptResultKey(scriptExecution1.getRunId(), -1L))
                .map(ScriptResult::getStatus)
                .orElseThrow(() -> new RuntimeException("Cannot find result of run id: " + scriptExecution1.getRunId())));
        scriptExecution1.setEndTimestamp(LocalDateTime.now());
        ScriptExecutionConfiguration.getInstance().update(scriptExecution1);
    }
}
