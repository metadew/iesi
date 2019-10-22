package io.metadew.iesi.runtime.script;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.script.exception.ScriptDoesNotExistException;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptNameExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.script.ScriptExecutionBuildException;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.execution.ScriptExecutionBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;

public class ScriptNameExecutor implements ScriptExecutor<ScriptNameExecutionRequest> {
    private final ScriptConfiguration scriptConfiguration;

    private static final Logger LOGGER = LogManager.getLogger();
    private static ScriptNameExecutor INSTANCE;

    public synchronized static ScriptNameExecutor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScriptNameExecutor();
        }
        return INSTANCE;
    }

    private ScriptNameExecutor() {
        scriptConfiguration = new ScriptConfiguration();
    }

    @Override
    public Class<ScriptNameExecutionRequest> appliesTo() {
        return ScriptNameExecutionRequest.class;
    }

    @Override
    public void execute(ScriptNameExecutionRequest scriptExecutionRequest) throws MetadataDoesNotExistException, ScriptExecutionBuildException, MetadataAlreadyExistsException {

        Script script = scriptExecutionRequest.getScriptVersion()
                .map(scriptVersion -> scriptConfiguration.get(scriptExecutionRequest.getScriptName(), scriptVersion))
                .orElse(scriptConfiguration.get(scriptExecutionRequest.getScriptName()))
                .orElseThrow(() -> new ScriptDoesNotExistException(MessageFormat.format("Script {0}:{1} does not exist", scriptExecutionRequest.getScriptName(), scriptExecutionRequest.getScriptVersion().map(Object::toString).orElse("latest"))));

        // TODO: ActionSelection?

        ScriptExecution scriptExecution = new ScriptExecutionBuilder(true, false)
                .script(script)
                .exitOnCompletion(scriptExecutionRequest.isExit())
                .parameters(scriptExecutionRequest.getParameters())
                .impersonations(scriptExecutionRequest.getImpersonations().orElse(new HashMap<>()))
                // .actionSelectOperation(new ActionSelectOperation(scriptExecutionRequest.getActionSelect()))
                .environment(scriptExecutionRequest.getEnvironment())
                .build();

        io.metadew.iesi.metadata.definition.execution.script.ScriptExecution scriptExecution1 = new io.metadew.iesi.metadata.definition.execution.script.ScriptExecution(new ScriptExecutionKey(), scriptExecutionRequest.getMetadataKey(), scriptExecution.getExecutionControl().getRunId(), ScriptExecutionStatus.RUNNING, LocalDateTime.now(), null);
        ScriptExecutionConfiguration.getInstance().insert(scriptExecution1);

        scriptExecution.execute();

        scriptExecution1.updateScriptExecutionStatus(ScriptExecutionStatus.COMPLETED);
        scriptExecution1.setEndTimestamp(LocalDateTime.now());
        ScriptExecutionConfiguration.getInstance().update(scriptExecution1);
    }
}
