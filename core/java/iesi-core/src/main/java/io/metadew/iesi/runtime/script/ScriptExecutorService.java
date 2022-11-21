package io.metadew.iesi.runtime.script;

import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionRequestConfiguration;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestStatus;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
public class ScriptExecutorService {

    private Map<Class<? extends ScriptExecutionRequest>, ScriptExecutor> scriptExecutorMap;

    private static final Logger LOGGER = LogManager.getLogger();

    private final ScriptFileExecutor scriptFileExecutor;
    private final ScriptNameExecutor scriptNameExecutor;
    private final ScriptExecutionRequestConfiguration scriptExecutionRequestConfiguration;

    public ScriptExecutorService(ScriptFileExecutor scriptFileExecutor,
                                 ScriptNameExecutor scriptNameExecutor,
                                 ScriptExecutionRequestConfiguration scriptExecutionRequestConfiguration) {
        this.scriptFileExecutor = scriptFileExecutor;
        this.scriptNameExecutor = scriptNameExecutor;
        this.scriptExecutionRequestConfiguration = scriptExecutionRequestConfiguration;
    }

    @PostConstruct
    private void postConstruct() {
        scriptExecutorMap = new HashMap<>();
        scriptExecutorMap.put(scriptFileExecutor.appliesTo(), scriptFileExecutor);
        scriptExecutorMap.put(scriptNameExecutor.appliesTo(), scriptNameExecutor);
    }

    @SuppressWarnings("unchecked")
    public void execute(ScriptExecutionRequest scriptExecutionRequest) {
        log.info("Executing " + scriptExecutionRequest);
        ScriptExecutor scriptExecutor = scriptExecutorMap.get(scriptExecutionRequest.getClass());

        scriptExecutionRequest.setScriptExecutionRequestStatus(ScriptExecutionRequestStatus.SUBMITTED);
        scriptExecutionRequestConfiguration.update(scriptExecutionRequest);

        if (scriptExecutor == null) {
            LOGGER.error(MessageFormat.format("No Executor found for request type {0}", scriptExecutionRequest.getClass()));
            scriptExecutionRequest.setScriptExecutionRequestStatus(ScriptExecutionRequestStatus.DECLINED);
            scriptExecutionRequestConfiguration.update(scriptExecutionRequest);
        } else {
            scriptExecutionRequest.setScriptExecutionRequestStatus(ScriptExecutionRequestStatus.ACCEPTED);
            scriptExecutionRequestConfiguration.update(scriptExecutionRequest);

            scriptExecutor.execute(scriptExecutionRequest);
            scriptExecutionRequest.setScriptExecutionRequestStatus(ScriptExecutionRequestStatus.COMPLETED);
            scriptExecutionRequestConfiguration.update(scriptExecutionRequest);
        }

    }
}
