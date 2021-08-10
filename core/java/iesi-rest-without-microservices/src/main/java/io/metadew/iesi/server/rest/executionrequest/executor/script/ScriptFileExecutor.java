//package io.metadew.iesi.server.rest.executionrequest.executor.script;
//
//import io.metadew.iesi.common.configuration.ScriptRunStatus;
//import io.metadew.iesi.connection.tools.FileTools;
//import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionConfiguration;
//import io.metadew.iesi.metadata.configuration.impersonation.ImpersonationConfiguration;
//import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
//import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestParameter;
//import io.metadew.iesi.metadata.definition.execution.script.ScriptFileExecutionRequest;
//import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
//import io.metadew.iesi.metadata.definition.script.Script;
//import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
//import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
//import io.metadew.iesi.metadata.tools.IdentifierTools;
//import io.metadew.iesi.script.execution.ScriptExecution;
//import io.metadew.iesi.script.execution.ScriptExecutionBuilder;
//import io.metadew.iesi.script.operation.JsonInputOperation;
//import io.metadew.iesi.script.operation.YamlInputOperation;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Service
//public class ScriptFileExecutor implements ScriptExecutor<ScriptFileExecutionRequest> {
//
//    private ScriptFileExecutor() {
//        ScriptResultConfiguration.getInstance();
//    }
//
//    @Override
//    public Class<ScriptFileExecutionRequest> appliesTo() {
//        return ScriptFileExecutionRequest.class;
//    }
//
//    @Override
//    public void execute(ScriptFileExecutionRequest scriptExecutionRequest) {
//        File file = new File(scriptExecutionRequest.getFileName());
//        Script script = null;
//        if (FileTools.getFileExtension(file).equalsIgnoreCase("json")) {
//            JsonInputOperation jsonInputOperation = new JsonInputOperation(scriptExecutionRequest.getFileName());
//            script = jsonInputOperation.getScript()
//                    .orElseThrow(() -> new RuntimeException(jsonInputOperation.getFileName()));
//        } else if (FileTools.getFileExtension(file).equalsIgnoreCase("yml")) {
//            YamlInputOperation yamlInputOperation = new YamlInputOperation(scriptExecutionRequest.getFileName());
//            script = yamlInputOperation.getScript()
//                    .orElseThrow(() -> new RuntimeException(yamlInputOperation.getFileName()));
//        }
//
//        Map<String, String> impersonations = new HashMap<>();
//        scriptExecutionRequest.getImpersonations()
//                .forEach(scriptExecutionRequestImpersonation -> ImpersonationConfiguration.getInstance().get(scriptExecutionRequestImpersonation.getImpersonationKey())
//                        .ifPresent(impersonation -> impersonation.getParameters()
//                                .forEach(impersonationParameter -> impersonations.put(impersonationParameter.getMetadataKey().getParameterName(), impersonationParameter.getImpersonatedConnection()))));
//
//        ScriptExecution scriptExecution = new ScriptExecutionBuilder(true, false)
//                .script(script)
//                .exitOnCompletion(scriptExecutionRequest.isExit())
//                .parameters(scriptExecutionRequest.getParameters().stream()
//                        .collect(Collectors.toMap(ScriptExecutionRequestParameter::getName, ScriptExecutionRequestParameter::getValue)))
//                .impersonations(impersonations)
//                // .actionSelectOperation(new ActionSelectOperation(scriptExecutionRequest.getActionSelect()))
//                .environment(scriptExecutionRequest.getEnvironment())
//                .build();
//
//        io.metadew.iesi.metadata.definition.execution.script.ScriptExecution scriptExecution1 = new io.metadew.iesi.metadata.definition.execution.script.ScriptExecution(new ScriptExecutionKey(IdentifierTools.getScriptExecutionRequestIdentifier()), scriptExecutionRequest.getMetadataKey(), scriptExecution.getExecutionControl().getRunId(), ScriptRunStatus.RUNNING, LocalDateTime.now(), null);
//        ScriptExecutionConfiguration.getInstance().insert(scriptExecution1);
//
//        scriptExecution.execute();
//
//        scriptExecution1.updateScriptRunStatus(ScriptResultConfiguration.getInstance()
//                .get(new ScriptResultKey(scriptExecution1.getRunId(), -1L))
//                .map(ScriptResult::getStatus)
//                .orElseThrow(() -> new RuntimeException("Cannot find result of run id: " + scriptExecution1.getRunId())));
//        scriptExecution1.setEndTimestamp(LocalDateTime.now());
//        ScriptExecutionConfiguration.getInstance().insert(scriptExecution1);
//    }
//}
