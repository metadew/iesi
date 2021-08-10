//package io.metadew.iesi.server.rest.executionrequest.executor.script;
//
//import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionRequestConfiguration;
//import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequest;
//import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestStatus;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import java.text.MessageFormat;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class ScriptExecutorService {
//
//    private final Map<Class<? extends ScriptExecutionRequest>, ScriptExecutor> scriptExecutorMap = new HashMap<>();
//
//    private final List<ScriptExecutor> scriptExecutors;
//
//    private static final Logger LOGGER = LogManager.getLogger();
//
//    public ScriptExecutorService(List<ScriptExecutor> scriptExecutors) {
//        this.scriptExecutors = scriptExecutors;
//    }
//
//    @PostConstruct
//    @SuppressWarnings("unchecked")
//    public void init() {
//        scriptExecutors
//                .forEach(scriptExecutor -> scriptExecutorMap.put(scriptExecutor.appliesTo(), scriptExecutor));
//    }
//
//    @SuppressWarnings("unchecked")
//    public void execute(ScriptExecutionRequest scriptExecutionRequest) {
//        ScriptExecutor scriptExecutor = scriptExecutorMap.get(scriptExecutionRequest.getClass());
//
//        scriptExecutionRequest.setScriptExecutionRequestStatus(ScriptExecutionRequestStatus.SUBMITTED);
//        ScriptExecutionRequestConfiguration.getInstance().update(scriptExecutionRequest);
//
//        if (scriptExecutor == null) {
//            LOGGER.error(MessageFormat.format("No Executor found for request type {0}", scriptExecutionRequest.getClass()));
//            scriptExecutionRequest.setScriptExecutionRequestStatus(ScriptExecutionRequestStatus.DECLINED);
//            ScriptExecutionRequestConfiguration.getInstance().update(scriptExecutionRequest);
//        } else {
//            scriptExecutionRequest.setScriptExecutionRequestStatus(ScriptExecutionRequestStatus.ACCEPTED);
//            ScriptExecutionRequestConfiguration.getInstance().update(scriptExecutionRequest);
//
//            scriptExecutor.execute(scriptExecutionRequest);
//
//            scriptExecutionRequest.setScriptExecutionRequestStatus(ScriptExecutionRequestStatus.COMPLETED);
//            ScriptExecutionRequestConfiguration.getInstance().update(scriptExecutionRequest);
//        }
//    }
//}
