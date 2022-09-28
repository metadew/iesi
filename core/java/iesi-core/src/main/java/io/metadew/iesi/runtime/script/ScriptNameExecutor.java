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
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class ScriptNameExecutor implements ScriptExecutor<ScriptNameExecutionRequest> {

    private final ScriptConfiguration scriptConfiguration;
    private final ImpersonationConfiguration impersonationConfiguration;
    private final ScriptExecutionConfiguration scriptExecutionConfiguration;
    private final ScriptResultConfiguration scriptResultConfiguration;

    public ScriptNameExecutor(ScriptConfiguration scriptConfiguration,
                              ImpersonationConfiguration impersonationConfiguration,
                              ScriptExecutionConfiguration scriptExecutionConfiguration,
                              ScriptResultConfiguration scriptResultConfiguration) {
        this.scriptConfiguration = scriptConfiguration;
        this.impersonationConfiguration = impersonationConfiguration;
        this.scriptExecutionConfiguration = scriptExecutionConfiguration;
        this.scriptResultConfiguration = scriptResultConfiguration;
    }

    @Override
    public Class<ScriptNameExecutionRequest> appliesTo() {
        return ScriptNameExecutionRequest.class;
    }

    @Override
    public void execute(ScriptNameExecutionRequest scriptExecutionRequest) {
        Script script = scriptExecutionRequest.getScriptVersion()
                .map(scriptVersion -> scriptConfiguration.get(new ScriptKey(IdentifierTools.getScriptIdentifier(scriptExecutionRequest.getScriptName()), scriptVersion)))
                .orElse(scriptConfiguration.getLatestVersion(scriptExecutionRequest.getScriptName()))
                .orElseThrow(() -> new MetadataDoesNotExistException(new ScriptKey(IdentifierTools.getScriptIdentifier(scriptExecutionRequest.getScriptName()), scriptExecutionRequest.getScriptVersion().orElse(-1L))));

        Map<String, String> impersonations = new HashMap<>();
        scriptExecutionRequest.getImpersonations()
                .forEach(scriptExecutionRequestImpersonation -> impersonationConfiguration.get(scriptExecutionRequestImpersonation.getImpersonationKey())
                        .ifPresent(impersonation -> impersonation.getParameters()
                                .forEach(impersonationParameter -> impersonations.put(impersonationParameter.getMetadataKey().getParameterName(), impersonationParameter.getImpersonatedConnection()))));

        ScriptExecution scriptExecution = new ScriptExecutionBuilder(true, false)
                .script(script)
                .exitOnCompletion(false)
                .parameters(scriptExecutionRequest.getParameters().stream()
                        .collect(Collectors.toMap(ScriptExecutionRequestParameter::getName, ScriptExecutionRequestParameter::getValue)))
                .impersonations(impersonations)
                .environment(scriptExecutionRequest.getEnvironment())
                .build();

        io.metadew.iesi.metadata.definition.execution.script.ScriptExecution scriptExecution1 =
                new io.metadew.iesi.metadata.definition.execution.script.ScriptExecution(new ScriptExecutionKey(IdentifierTools.getScriptExecutionRequestIdentifier()),
                        scriptExecutionRequest.getMetadataKey(), scriptExecution.getExecutionControl().getRunId(),
                        ScriptRunStatus.RUNNING, LocalDateTime.now(), null);
        scriptExecutionConfiguration.insert(scriptExecution1);

        scriptExecution.execute();

        scriptExecution1.updateScriptRunStatus(scriptResultConfiguration.get(new ScriptResultKey(scriptExecution1.getRunId(), -1L))
                .map(ScriptResult::getStatus)
                .orElseThrow(() -> new RuntimeException("Cannot find result of run id: " + scriptExecution1.getRunId())));
        scriptExecution1.setEndTimestamp(LocalDateTime.now());
        scriptExecutionConfiguration.update(scriptExecution1);
    }
}
