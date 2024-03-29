package io.metadew.iesi.script.execution;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.elasticsearch.filebeat.DelimitedFileBeatElasticSearchConnection;
import io.metadew.iesi.metadata.configuration.action.result.ActionResultConfiguration;
import io.metadew.iesi.metadata.configuration.action.result.ActionResultOutputConfiguration;
import io.metadew.iesi.metadata.configuration.environment.EnvironmentParameterConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultOutputConfiguration;
import io.metadew.iesi.metadata.definition.action.result.ActionResult;
import io.metadew.iesi.metadata.definition.action.result.ActionResultOutput;
import io.metadew.iesi.metadata.definition.action.result.key.ActionResultKey;
import io.metadew.iesi.metadata.definition.action.result.key.ActionResultOutputKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.definition.script.result.ScriptResultElasticSearch;
import io.metadew.iesi.metadata.definition.script.result.ScriptResultOutput;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultOutputKey;
import io.metadew.iesi.metadata.service.action.ActionDesignTraceService;
import io.metadew.iesi.metadata.service.script.ScriptDesignTraceService;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.ThreadContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.UUID;

@Log4j2
public class ExecutionControl {

    private final DelimitedFileBeatElasticSearchConnection elasticSearchConnection;
    private final ActionDesignTraceService actionDesignTraceService = SpringContext.getBean(ActionDesignTraceService.class);
    private final ScriptDesignTraceService scriptDesignTraceService = SpringContext.getBean(ScriptDesignTraceService.class);
    private final Configuration configuration = SpringContext.getBean(Configuration.class);
    private final FrameworkCrypto frameworkCrypto = SpringContext.getBean(FrameworkCrypto.class);

    private ExecutionRuntime executionRuntime;
    private String runId;
    private String envName;
    private boolean actionErrorStop = false;
    private boolean scriptExit = false;


    private Long lastProcessId;

    private static final Marker SCRIPTMARKER = MarkerManager.getMarker("SCRIPT");


    public ExecutionControl(ScriptExecutionInitializationParameters scriptExecutionInitializationParameters) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        initializeRunId();
        initializeExecutionRuntime(runId, scriptExecutionInitializationParameters);
        this.lastProcessId = -1L;
        this.elasticSearchConnection = new DelimitedFileBeatElasticSearchConnection();
    }

    @SuppressWarnings("unchecked")
    private void initializeExecutionRuntime(String runId, ScriptExecutionInitializationParameters scriptExecutionInitializationParameters) throws ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (configuration.getProperty("iesi.script.execution.runtime").isPresent()) {
            Class classRef = Class.forName((String) configuration.getProperty("iesi.script.execution.runtime").get());
            Class[] initParams = {ExecutionControl.class, String.class, ScriptExecutionInitializationParameters.class};
            Constructor constructor = classRef.getConstructor(initParams);
            this.executionRuntime = (ExecutionRuntime) constructor.newInstance(this, runId, scriptExecutionInitializationParameters);
        } else {
            this.executionRuntime = new ExecutionRuntime(this, runId, scriptExecutionInitializationParameters);
        }
    }

    public void setEnvironment(ActionExecution actionExecution, String environmentName) {
        this.envName = environmentName;

        SpringContext.getBean(EnvironmentParameterConfiguration.class)
                .getByEnvironment(new EnvironmentKey(environmentName))
                .forEach(environmentParameter -> executionRuntime.setRuntimeVariable(
                        actionExecution,
                        environmentParameter.getMetadataKey().getParameterName(),
                        environmentParameter.getValue()
                ));
    }

    public void terminate() {
        this.executionRuntime.terminate();
    }

    // Log start
    public void logStart(ScriptExecution scriptExecution) {
        Long parentProcessId = scriptExecution.getParentScriptExecution().map(ScriptExecution::getProcessId).orElse(-1L);
        ScriptResult scriptResult = new ScriptResult(new ScriptResultKey(runId, scriptExecution.getProcessId()),
                parentProcessId,
                scriptExecution.getScript().getMetadataKey().getScriptId(),
                scriptExecution.getScript().getName(),
                scriptExecution.getScript().getVersion().getNumber(),
                scriptExecution.getScript().getSecurityGroupName(),
                envName,
                ScriptRunStatus.RUNNING,
                LocalDateTime.now(),
                null
        );

        SpringContext.getBean(ScriptResultConfiguration.class).insert(scriptResult);
        // Trace the design of the script
        scriptDesignTraceService.trace(scriptExecution);
    }

    public void logStart(ActionExecution actionExecution) {
        actionDesignTraceService.trace(runId, actionExecution.getProcessId(), actionExecution.getAction());
        ActionResult actionResult = new ActionResult(
                runId,
                actionExecution.getProcessId(),
                actionExecution.getAction().getMetadataKey().getActionId(),
                actionExecution.getScriptExecution().getProcessId(),
                actionExecution.getAction().getName(),
                envName,
                ScriptRunStatus.RUNNING,
                LocalDateTime.now(),
                null,
                actionExecution.getAction().getType()
        );
        SpringContext.getBean(ActionResultConfiguration.class).insert(actionResult);
    }

    public void logSkip(ActionExecution actionExecution) {
        ActionResult actionResult = new ActionResult(
                runId,
                actionExecution.getProcessId(),
                actionExecution.getAction().getMetadataKey().getActionId(),
                actionExecution.getScriptExecution().getProcessId(),
                actionExecution.getAction().getName(),
                envName,
                ScriptRunStatus.SKIPPED,
                null,
                null,
                actionExecution.getAction().getType()
        );
        SpringContext.getBean(ActionResultConfiguration.class).insert(actionResult);

        this.logMessage("action.status=" + ScriptRunStatus.SKIPPED.value(), Level.INFO);

    }

//    public void logStart(BackupExecution backupExecution) {
//        initializeRunId();
//    }


//    public Long getNewProcessId() {
//        Long processId = FrameworkRuntime.getInstance().getNextProcessId();
//        logMessage(new IESIMessage("exec.processid=" + processId), Level.TRACE);
//        return processId;
//    }

    public Long getNextProcessId() {
        lastProcessId = lastProcessId + 1;
        return lastProcessId;
    }

    public Long getProcessId() {
        return lastProcessId;
    }

    public ScriptRunStatus logEnd(ScriptExecution scriptExecution) {
        ScriptResult scriptResult = SpringContext.getBean(ScriptResultConfiguration.class).get(new ScriptResultKey(runId, scriptExecution.getProcessId()))
                .orElseThrow(() -> new MetadataDoesNotExistException(new ScriptResultKey(runId, scriptExecution.getProcessId())));
        ScriptRunStatus status = getStatus(scriptExecution);
        logMessage("script.status=" + status.value(), Level.INFO);
        scriptResult.setStatus(status);
        scriptResult.setEndTimestamp(LocalDateTime.now());
        SpringContext.getBean(ScriptResultConfiguration.class).update(scriptResult);

        String output = scriptExecution.getExecutionControl().getExecutionRuntime().resolveVariables("#output#");
        if (output != null && !output.isEmpty()) {
            try {
                // catch error if 'output' is already outputted. To be deleted in future
                logExecutionOutput(scriptExecution, "output", output);
                logMessage("script.output=" + output, Level.INFO);
            } catch (Exception ignore) {
            }
        }

        elasticSearchConnection.ingest(new ScriptResultElasticSearch(scriptResult));

        return status;

    }

    public void logEnd(ActionExecution actionExecution, ScriptExecution scriptExecution) {
        ActionResult actionResult = SpringContext.getBean(ActionResultConfiguration.class).get(new ActionResultKey(runId, actionExecution.getProcessId(), actionExecution.getAction().getMetadataKey().getActionId()))
                .orElseThrow(() -> new MetadataDoesNotExistException(new ScriptResultKey(runId, scriptExecution.getProcessId())));

        ScriptRunStatus status = getStatus(actionExecution, scriptExecution);
        actionResult.setStatus(status);
        actionResult.setEndTimestamp(LocalDateTime.now());
        SpringContext.getBean(ActionResultConfiguration.class).update(actionResult);

    }

    private ScriptRunStatus getStatus(ActionExecution actionExecution, ScriptExecution scriptExecution) {
        ScriptRunStatus status;

        if (actionExecution.getActionControl().getExecutionMetrics().getSkipCount() == 0) {
            if (actionExecution.getActionControl().getExecutionMetrics().getErrorCount() > 0) {
                status = ScriptRunStatus.ERROR;
                scriptExecution.getExecutionMetrics().increaseErrorCount(1);
            } else if (actionExecution.getActionControl().getExecutionMetrics().getWarningCount() > 0) {
                status = ScriptRunStatus.WARNING;
                scriptExecution.getExecutionMetrics().increaseWarningCount(1);
            } else {
                status = ScriptRunStatus.SUCCESS;
                scriptExecution.getExecutionMetrics().increaseSuccessCount(1);
            }
        } else {
            status = ScriptRunStatus.SKIPPED;
            scriptExecution.getExecutionMetrics().increaseSkipCount(1);
        }

        logMessage("action.status=" + status.value(), Level.INFO);

        return status;

    }

    private ScriptRunStatus getStatus(ScriptExecution scriptExecution) {
        ScriptRunStatus status;

        if (actionErrorStop) {
            status = ScriptRunStatus.STOPPED;
        } else if (scriptExit) {
            status = ScriptRunStatus.STOPPED;
        } else if (scriptExecution.getExecutionMetrics().getSuccessCount() == 0
                && scriptExecution.getExecutionMetrics().getWarningCount() == 0
                && scriptExecution.getExecutionMetrics().getErrorCount() > 0) {
            status = ScriptRunStatus.ERROR;
        } else if (scriptExecution.getExecutionMetrics().getSuccessCount() > 0
                && scriptExecution.getExecutionMetrics().getWarningCount() == 0
                && scriptExecution.getExecutionMetrics().getErrorCount() == 0) {
            status = ScriptRunStatus.SUCCESS;
        } else {
            status = ScriptRunStatus.WARNING;
        }
        return status;
    }

    public void logExecutionOutput(ScriptExecution scriptExecution, String outputName, String outputValue) {
        // Redact any encrypted values
        outputValue = frameworkCrypto.redact(outputValue);
        ScriptResultOutput scriptResultOutput = new ScriptResultOutput(new ScriptResultOutputKey(runId, scriptExecution.getProcessId(), outputName), scriptExecution.getScript().getMetadataKey().getScriptId(), outputValue);

        SpringContext.getBean(ScriptResultOutputConfiguration.class).insert(scriptResultOutput);
    }

    public void logExecutionOutput(ActionExecution actionExecution, String outputName, String outputValue) {
        ActionResultOutput actionResultOutput = new ActionResultOutput(
                new ActionResultOutputKey(runId, actionExecution.getProcessId(), actionExecution.getAction().getMetadataKey().getActionId(), outputName),
                outputValue);
        SpringContext.getBean(ActionResultOutputConfiguration.class).insert(actionResultOutput);
    }

    // Log message
    public void logMessage(String message, Level level) {
        log.log(level, SCRIPTMARKER, message);
    }


    public void endExecution() {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("script.launcher.end");
        System.exit(0);
    }

    public String getRunId() {
        return runId;
    }

    public void initializeRunId() {
        this.runId = UUID.randomUUID().toString();
        ThreadContext.put("runId", runId);
        log.info("exec.runid=" + runId);
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public void setActionErrorStop(boolean actionErrorStop) {
        this.actionErrorStop = actionErrorStop;
    }

    public ExecutionRuntime getExecutionRuntime() {
        return executionRuntime;
    }

    public Long getLastProcessId() {
        return lastProcessId;
    }

    public void setScriptExit(boolean scriptExit) {
        this.scriptExit = scriptExit;
    }
}