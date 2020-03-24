package io.metadew.iesi.script.execution;

import io.metadew.iesi.common.text.TextTools;
import io.metadew.iesi.connection.elasticsearch.filebeat.DelimitedFileBeatElasticSearchConnection;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.configuration.action.result.ActionResultConfiguration;
import io.metadew.iesi.metadata.configuration.action.result.ActionResultOutputConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultOutputConfiguration;
import io.metadew.iesi.metadata.definition.action.result.ActionResult;
import io.metadew.iesi.metadata.definition.action.result.ActionResultOutput;
import io.metadew.iesi.metadata.definition.action.result.key.ActionResultKey;
import io.metadew.iesi.metadata.definition.action.result.key.ActionResultOutputKey;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.definition.script.result.ScriptResultElasticSearch;
import io.metadew.iesi.metadata.definition.script.result.ScriptResultOutput;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultOutputKey;
import io.metadew.iesi.metadata.service.action.ActionDesignTraceService;
import io.metadew.iesi.metadata.service.script.ScriptDesignTraceService;
import org.apache.logging.log4j.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class ExecutionControl {

    private final DelimitedFileBeatElasticSearchConnection elasticSearchConnection;
    private final ActionDesignTraceService actionDesignTraceService;
    private ExecutionRuntime executionRuntime;
    private ExecutionTrace executionTrace;
    private String runId;
    private String envName;
    private boolean actionErrorStop = false;
    private boolean scriptExit = false;
    private ScriptDesignTraceService scriptDesignTraceService;

    private Long lastProcessId;

    private static final Marker SCRIPTMARKER = MarkerManager.getMarker("SCRIPT");

    private static final Logger LOGGER = LogManager.getLogger();
    // Constructors

    public ExecutionControl() throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        this.scriptDesignTraceService = new ScriptDesignTraceService();
        this.actionDesignTraceService = new ActionDesignTraceService();
        this.executionTrace = new ExecutionTrace();
        initializeRunId();
        initializeExecutionRuntime(runId);
        this.lastProcessId = -1L;
        this.elasticSearchConnection = new DelimitedFileBeatElasticSearchConnection();
    }

    @SuppressWarnings("unchecked")
    private void initializeExecutionRuntime(String runId) throws ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (Configuration.getInstance().getProperty("script.execution.runtime").isPresent()) {
            Class classRef = Class.forName((String) Configuration.getInstance().getProperty("script.execution.runtime").get());
            Class[] initParams = {ExecutionControl.class, String.class};
            Constructor constructor = classRef.getConstructor(initParams);
            this.executionRuntime = (ExecutionRuntime) constructor.newInstance(this, runId);
        } else {
            this.executionRuntime = new ExecutionRuntime(this, runId);
        }
    }

    public void setEnvironment(ActionExecution actionExecution, String environmentName) {
        this.envName = environmentName;

        // Set environment variables
        executionRuntime.setRuntimeVariablesFromList(actionExecution, MetadataRepositoryConfiguration.getInstance()
                .getConnectivityMetadataRepository()
                .executeQuery("select env_par_nm, env_par_val from "
                        + MetadataRepositoryConfiguration.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("EnvironmentParameters")
                        + " where env_nm = " + SQLTools.GetStringForSQL(this.envName) + " order by env_par_nm asc, env_par_val asc", "reader"));
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
                envName,
                "ACTIVE",
                LocalDateTime.now(),
                null
        );
        ScriptResultConfiguration.getInstance().insert(scriptResult);


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
                "ACTIVE",
                LocalDateTime.now(),
                null
        );
        ActionResultConfiguration.getInstance().insert(actionResult);
    }

    public void logSkip(ActionExecution actionExecution) {
        ActionResult actionResult = new ActionResult(
                runId,
                actionExecution.getProcessId(),
                actionExecution.getAction().getMetadataKey().getActionId(),
                actionExecution.getScriptExecution().getProcessId(),
                actionExecution.getAction().getName(),
                envName,
                "SKIPPED",
                null,
                null
        );
        ActionResultConfiguration.getInstance().insert(actionResult);

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

    public String logEnd(ScriptExecution scriptExecution) {
        ScriptResult scriptResult = ScriptResultConfiguration.getInstance().get(new ScriptResultKey(runId, scriptExecution.getProcessId()))
                .orElseThrow(() -> new MetadataDoesNotExistException(new ScriptResultKey(runId, scriptExecution.getProcessId())));

        String status = getStatus(scriptExecution);
        scriptResult.setStatus(status);
        scriptResult.setEndTimestamp(LocalDateTime.now());
        ScriptResultConfiguration.getInstance().update(scriptResult);

        // Clear processing variables
        // Only is the script is a root script, this will be cleaned
        // In other scripts, the processing variables are still valid

        elasticSearchConnection.ingest(new ScriptResultElasticSearch(scriptResult));

        return status;

    }

    public void logEnd(ActionExecution actionExecution, ScriptExecution scriptExecution) {
        ActionResult actionResult = ActionResultConfiguration.getInstance().get(new ActionResultKey(runId, actionExecution.getProcessId(), actionExecution.getAction().getMetadataKey().getActionId()))
                .orElseThrow(() -> new MetadataDoesNotExistException(new ScriptResultKey(runId, scriptExecution.getProcessId())));

        String status = getStatus(actionExecution, scriptExecution);
        actionResult.setStatus(status);
        actionResult.setEndTimestamp(LocalDateTime.now());
        ActionResultConfiguration.getInstance().update(actionResult);

    }

    private String getStatus(ActionExecution actionExecution, ScriptExecution scriptExecution) {
        String status;

        if (actionExecution.getActionControl().getExecutionMetrics().getSkipCount() == 0) {
            if (actionExecution.getActionControl().getExecutionMetrics().getErrorCount() > 0) {
                status = ScriptRunStatus.ERROR.value();
                scriptExecution.getExecutionMetrics().increaseErrorCount(1);
            } else if (actionExecution.getActionControl().getExecutionMetrics().getWarningCount() > 0) {
                status = ScriptRunStatus.WARNING.value();
                scriptExecution.getExecutionMetrics().increaseWarningCount(1);
            } else {
                status = ScriptRunStatus.SUCCESS.value();
                scriptExecution.getExecutionMetrics().increaseSuccessCount(1);
            }
        } else {
            status = ScriptRunStatus.SKIPPED.value();
            scriptExecution.getExecutionMetrics().increaseSkipCount(1);
        }

        logMessage("action.status=" + status, Level.INFO);

        return status;

    }

    private String getStatus(ScriptExecution scriptExecution) {
        String status;

        if (actionErrorStop) {
            status = ScriptRunStatus.STOPPED.value();
        } else if (scriptExit) {
            status = ScriptRunStatus.STOPPED.value();
        } else if (scriptExecution.getExecutionMetrics().getSuccessCount() == 0
                && scriptExecution.getExecutionMetrics().getWarningCount() == 0
                && scriptExecution.getExecutionMetrics().getErrorCount() > 0) {
            status = ScriptRunStatus.ERROR.value();
        } else if (scriptExecution.getExecutionMetrics().getSuccessCount() > 0
                && scriptExecution.getExecutionMetrics().getWarningCount() == 0
                && scriptExecution.getExecutionMetrics().getErrorCount() == 0) {
            status = ScriptRunStatus.SUCCESS.value();
        } else {
            status = ScriptRunStatus.WARNING.value();
        }

        logMessage("script.status=" + status, Level.INFO);

        String output = scriptExecution.getExecutionControl().getExecutionRuntime().resolveVariables("#output#");
        if (output != null && !output.isEmpty()) {
            //logMessage(scriptExecution, "script.output=" + output, Level.INFO);
            logExecutionOutput(scriptExecution, "output", output);
        }
        return status;
    }

    public void logExecutionOutput(ScriptExecution scriptExecution, String outputName, String outputValue) {
        // Redact any encrypted values
        outputValue = FrameworkCrypto.getInstance().redact(outputValue);
        outputValue = TextTools.shortenTextForDatabase(outputValue, 2000);
        logMessage("script.output=" + outputName + ":" + outputValue, Level.INFO);
        ScriptResultOutput scriptResultOutput = new ScriptResultOutput(new ScriptResultOutputKey(runId, scriptExecution.getProcessId(), outputName), scriptExecution.getScript().getMetadataKey().getScriptId(), outputValue);
        ScriptResultOutputConfiguration.getInstance().insert(scriptResultOutput);
    }

    public void logExecutionOutput(ActionExecution actionExecution, String outputName, String outputValue) {
        // Redact any encrypted values
        outputValue = FrameworkCrypto.getInstance().redact(outputValue);
        // TODO: why shorten?
        outputValue = TextTools.shortenTextForDatabase(outputValue, 2000);

        ActionResultOutput actionResultOutput = new ActionResultOutput(
                new ActionResultOutputKey(runId, actionExecution.getProcessId(), actionExecution.getAction().getMetadataKey().getActionId(), outputName),
                outputValue);
        ActionResultOutputConfiguration.getInstance().insert(actionResultOutput);

    }

    // Log message
    public void logMessage(String message, Level level) {
//        if (!actionExecution.getScriptExecution().isRootScript() && level == Level.INFO || level == Level.ALL) {
//            // do not display non-root info on info level
//            // redirect to debug level
//            level = Level.DEBUG;
//        }
        LOGGER.log(level, SCRIPTMARKER, message);
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
        LOGGER.info("exec.runid=" + runId);
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

    public ExecutionTrace getExecutionTrace() {
        return executionTrace;
    }

    public Long getLastProcessId() {
        return lastProcessId;
    }

    public void setScriptExit(boolean scriptExit) {
        this.scriptExit = scriptExit;
    }
}