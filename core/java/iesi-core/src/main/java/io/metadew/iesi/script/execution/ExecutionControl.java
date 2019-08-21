package io.metadew.iesi.script.execution;

import io.metadew.iesi.common.text.TextTools;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkStatus;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.framework.execution.FrameworkRuntime;
import io.metadew.iesi.framework.execution.IESIMessage;
import io.metadew.iesi.metadata.backup.BackupExecution;
import io.metadew.iesi.metadata.configuration.action.result.ActionResultConfiguration;
import io.metadew.iesi.metadata.configuration.action.result.ActionResultOutputConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultOutputConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.exception.ScriptResultDoesNotExistException;
import io.metadew.iesi.metadata.definition.action.result.ActionResult;
import io.metadew.iesi.metadata.definition.action.result.ActionResultOutput;
import io.metadew.iesi.metadata.definition.action.result.key.ActionResultKey;
import io.metadew.iesi.metadata.definition.action.result.key.ActionResultOutputKey;
import io.metadew.iesi.metadata.definition.script.ScriptLog;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.definition.script.result.ScriptResultOutput;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultOutputKey;
import io.metadew.iesi.metadata.execution.MetadataControl;
import io.metadew.iesi.metadata.restore.RestoreExecution;
import io.metadew.iesi.metadata.service.script.ScriptDesignTraceService;
import org.apache.logging.log4j.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExecutionControl {

    private final ScriptResultConfiguration scriptResultConfiguration;
    private final ActionResultConfiguration actionResultConfiguration;
    private final ActionResultOutputConfiguration actionResultOutputConfiguration;
    private final ScriptResultOutputConfiguration scriptResultOutputConfiguration;
    private ExecutionRuntime executionRuntime;
    private ExecutionLog executionLog;
    private ExecutionTrace executionTrace;
    private ScriptLog scriptLog;
    private String runId;
    private String envName;
    private List<Long> processIdList;
    private boolean actionErrorStop = false;
    private boolean scriptExit = false;
    private ScriptDesignTraceService scriptDesignTraceService;

    private static final Marker SCRIPTMARKER = MarkerManager.getMarker("SCRIPT");
    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public ExecutionControl() throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        this.scriptResultConfiguration = new ScriptResultConfiguration();
        this.actionResultConfiguration = new ActionResultConfiguration();
        this.actionResultOutputConfiguration = new ActionResultOutputConfiguration();
        this.scriptResultOutputConfiguration = new ScriptResultOutputConfiguration();
        this.scriptDesignTraceService = new ScriptDesignTraceService();
        this.executionLog = new ExecutionLog();
        this.executionTrace = new ExecutionTrace();
        setRunId(FrameworkRuntime.getInstance().getFrameworkRunId());
        initializeExecutionRuntime(runId);
        this.processIdList = new ArrayList<>();
        this.processIdList.add(-1L);
    }


    private void initializeExecutionRuntime(String runId) throws ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (FrameworkSettingConfiguration.getInstance().getSettingPath("script.execution.runtime").isPresent() &&
                !FrameworkControl.getInstance().getProperty(FrameworkSettingConfiguration.getInstance().getSettingPath("script.execution.runtime").get()).isEmpty()) {
            Class classRef = Class.forName(FrameworkControl.getInstance().getProperty(FrameworkSettingConfiguration.getInstance().getSettingPath("script.execution.runtime").get()));
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
        executionRuntime.setRuntimeVariablesFromList(actionExecution, MetadataControl.getInstance()
                .getConnectivityMetadataRepository()
                .executeQuery("select env_par_nm, env_par_val from "
                        + MetadataControl.getInstance().getConnectivityMetadataRepository().getTableNameByLabel("EnvironmentParameters")
                        + " where env_nm = '" + this.getEnvName() + "' order by env_par_nm asc, env_par_val asc", "writer"));
    }

    public void terminate() {
        this.executionRuntime.terminate();
    }

    // Log start
    public void logStart(ScriptExecution scriptExecution) {
        try {
            Long parentProcessId = scriptExecution.getParentScriptExecution().map(ScriptExecution::getProcessId).orElse(0L);
            ScriptResult scriptResult = new ScriptResult(new ScriptResultKey(runId, scriptExecution.getProcessId()),
                    parentProcessId,
                    scriptExecution.getScript().getId(),
                    scriptExecution.getScript().getName(),
                    scriptExecution.getScript().getVersion().getNumber(),
                    envName,
                    "ACTIVE",
                    LocalDateTime.now(),
                    null
            );
            scriptResultConfiguration.insert(scriptResult);


            this.scriptLog = new ScriptLog(runId, scriptExecution.getProcessId(), parentProcessId, scriptExecution.getScript().getId(),
                    scriptExecution.getScript().getVersion().getNumber(), envName, "ACTIVE", LocalDateTime.now(), null);
            this.executionLog.setLog(scriptLog);

            // Trace the design of the script
            scriptDesignTraceService.trace(scriptExecution);
        } catch (MetadataAlreadyExistsException | SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.info("stacktrace=" + stackTrace.toString());
        }
    }

    public void logStart(ActionExecution actionExecution) {
        try {
            ActionResult actionResult = new ActionResult(
                    runId,
                    actionExecution.getProcessId(),
                    actionExecution.getAction().getId(),
                    actionExecution.getScriptExecution().getProcessId(),
                    actionExecution.getAction().getName(),
                    envName,
                    "ACTIVE",
                    LocalDateTime.now(),
                    null
            );
            actionResultConfiguration.insert(actionResult);
        } catch (MetadataAlreadyExistsException | SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.info("stacktrace=" + stackTrace.toString());
        }
    }

    public void logSkip(ActionExecution actionExecution) {
        try {
            ActionResult actionResult = new ActionResult(
                    runId,
                    actionExecution.getProcessId(),
                    actionExecution.getAction().getId(),
                    actionExecution.getScriptExecution().getProcessId(),
                    actionExecution.getAction().getName(),
                    envName,
                    "SKIPPED",
                    null,
                    null
            );
            actionResultConfiguration.insert(actionResult);

            this.logMessage(actionExecution, "action.status=" + FrameworkStatus.SKIPPED.value(), Level.INFO);
        } catch (MetadataAlreadyExistsException | SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.info("stacktrace=" + stackTrace.toString());
        }

    }

    public void logStart(BackupExecution backupExecution) {
        setRunId(FrameworkRuntime.getInstance().getFrameworkRunId());
    }

    public void logStart(RestoreExecution restoreExecution) {
        setRunId(FrameworkRuntime.getInstance().getFrameworkRunId());
    }

    public Long getNewProcessId() {
        Long processId = FrameworkRuntime.getInstance().getNextProcessId();
        logMessage(new IESIMessage("exec.processid=" + processId), Level.TRACE);
        return processId;
    }

    public String logEnd(ScriptExecution scriptExecution) {
        try {
            ScriptResult scriptResult = scriptResultConfiguration.get(new ScriptResultKey(runId, scriptExecution.getProcessId()))
                    .orElseThrow(() -> new ScriptResultDoesNotExistException(MessageFormat.format("ScriptResult {0} does not exist, cannot log ending of execution", new ScriptResultKey(runId, scriptExecution.getProcessId()).toString())));

            String status = getStatus(scriptExecution);
            scriptResult.setStatus(status);
            scriptResult.setEndTimestamp(LocalDateTime.now());
            scriptResultConfiguration.update(scriptResult);

            // Clear processing variables
            // Only is the script is a root script, this will be cleaned
            // In other scripts, the processing variables are still valid

            scriptLog.setEnd(LocalDateTime.now());
            scriptLog.setStatus(status);
            executionLog.setLog(this.getScriptLog());

            // return
            return status;

        } catch (SQLException | MetadataDoesNotExistException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.info("stacktrace=" + stackTrace.toString());
            return FrameworkStatus.UNKOWN.value();
        }
    }

    public void logEnd(ActionExecution actionExecution, ScriptExecution scriptExecution) {
        try {
            ActionResult actionResult = actionResultConfiguration.get(new ActionResultKey(runId, scriptExecution.getProcessId(), actionExecution.getAction().getId()))
                    .orElseThrow(() -> new ScriptResultDoesNotExistException(MessageFormat.format("ActionResult {0} does not exist, cannot log ending of execution", new ScriptResultKey(runId, scriptExecution.getProcessId()).toString())));

            String status = getStatus(actionExecution, scriptExecution);
            actionResult.setStatus(status);
            actionResult.setEndTimestamp(LocalDateTime.now());
            actionResultConfiguration.update(actionResult);

        } catch (SQLException | MetadataDoesNotExistException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.info("stacktrace=" + stackTrace.toString());
        }
    }

    public void logEnd(BackupExecution backupExecution) {

    }

    public void logEnd(RestoreExecution restoreExecution) {

    }

    private String getStatus(ActionExecution actionExecution, ScriptExecution scriptExecution) {
        String status;

        if (actionExecution.getActionControl().getExecutionMetrics().getSkipCount() == 0) {

            if (actionExecution.getActionControl().getExecutionMetrics().getErrorCount() > 0) {
                status = FrameworkStatus.ERROR.value();
                scriptExecution.getExecutionMetrics().increaseErrorCount(1);
            } else if (actionExecution.getActionControl().getExecutionMetrics().getWarningCount() > 0) {
                status = FrameworkStatus.WARNING.value();
                scriptExecution.getExecutionMetrics().increaseWarningCount(1);
            } else {
                status = FrameworkStatus.SUCCESS.value();
                scriptExecution.getExecutionMetrics().increaseSuccessCount(1);
            }
        } else {
            status = FrameworkStatus.SKIPPED.value();
            scriptExecution.getExecutionMetrics().increaseSkipCount(1);
        }

        logMessage(actionExecution, "action.status=" + status, Level.INFO);

        return status;

    }

    private String getStatus(ScriptExecution scriptExecution) {
        String status;

        if (actionErrorStop) {
            status = FrameworkStatus.STOPPED.value();
        } else if (scriptExit) {
            status = FrameworkStatus.STOPPED.value();
            // TODO: get status from input parameters in action
        } else if (scriptExecution.getExecutionMetrics().getSuccessCount() == 0
                && scriptExecution.getExecutionMetrics().getWarningCount() == 0
                && scriptExecution.getExecutionMetrics().getErrorCount() > 0) {
            status = FrameworkStatus.ERROR.value();
        } else if (scriptExecution.getExecutionMetrics().getSuccessCount() > 0
                && scriptExecution.getExecutionMetrics().getWarningCount() == 0
                && scriptExecution.getExecutionMetrics().getErrorCount() == 0) {
            status = FrameworkStatus.SUCCESS.value();
        } else {
            status = FrameworkStatus.WARNING.value();
        }

        logMessage(scriptExecution, "script.status=" + status, Level.INFO);

        String output = scriptExecution.getExecutionControl().getExecutionRuntime().resolveVariables("#output#");

        logMessage(scriptExecution, "script.output=" + output, Level.INFO);

        return status;
    }

    public void logExecutionOutput(ScriptExecution scriptExecution, String outputName, int outputValue) {
        logExecutionOutput(scriptExecution, outputName, Integer.toString(outputValue));
    }

    public void logExecutionOutput(ScriptExecution scriptExecution, String outputName, long outputValue) {
        logExecutionOutput(scriptExecution, outputName, Long.toString(outputValue));
    }

    public void logExecutionOutput(ScriptExecution scriptExecution, String outputName, String outputValue) {
        // Redact any encrypted values
        outputValue = FrameworkCrypto.getInstance().redact(outputValue);
        outputValue = TextTools.shortenTextForDatabase(outputValue, 2000);
        try {
            ScriptResultOutput scriptResultOutput = new ScriptResultOutput(new ScriptResultOutputKey(runId, scriptExecution.getProcessId(), outputName), scriptExecution.getScript().getId(), outputValue);
            scriptResultOutputConfiguration.insert(scriptResultOutput);
        } catch (MetadataAlreadyExistsException | SQLException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.info("stacktrace=" + stackTrace.toString());
        }
    }

    public void logExecutionOutput(ActionExecution actionExecution, String outputName, int outputValue) {
        logExecutionOutput(actionExecution, outputName, Integer.toString(outputValue));
    }

    public void logExecutionOutput(ActionExecution actionExecution, String outputName, long outputValue) {
        logExecutionOutput(actionExecution, outputName, Long.toString(outputValue));
    }

    public void logExecutionOutput(ActionExecution actionExecution, String outputName, String outputValue) {
        try {
            // Redact any encrypted values
            outputValue = FrameworkCrypto.getInstance().redact(outputValue);
            // TODO: why shorten?
            outputValue = TextTools.shortenTextForDatabase(outputValue, 2000);

            ActionResultOutput actionResultOutput = new ActionResultOutput(
                    new ActionResultOutputKey(runId, actionExecution.getProcessId(), actionExecution.getAction().getId(), outputName),
                    outputValue);
            actionResultOutputConfiguration.insert(actionResultOutput);

        } catch (SQLException | MetadataAlreadyExistsException e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            LOGGER.warn("exception=" + e.getMessage());
            LOGGER.info("stacktrace=" + stackTrace.toString());
        }
    }

    // Log message
    public void logMessage(ActionExecution actionExecution, String message, Level level) {
//        if (!actionExecution.getScriptExecution().isRootScript() && level == Level.INFO || level == Level.ALL) {
//            // do not display non-root info on info level
//            // redirect to debug level
//            level = Level.DEBUG;
//        }
        LOGGER.log(level, SCRIPTMARKER, message);
    }

    public void logMessage(IESIMessage message, Level level) {
        LOGGER.log(level, SCRIPTMARKER, message);
    }

    public void logMessage(ScriptExecution scriptExecution, String message, Level level) {
//        if (!scriptExecution.isRootScript() && level == Level.INFO || level == Level.ALL) {
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

    public void setRunId(String runId) {
        this.runId = runId;
        ThreadContext.put("runId", runId);
        logMessage(new IESIMessage("exec.runid=" + runId), Level.INFO);
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public boolean isActionErrorStop() {
        return actionErrorStop;
    }

    public void setActionErrorStop(boolean actionErrorStop) {
        this.actionErrorStop = actionErrorStop;
    }

    public ExecutionRuntime getExecutionRuntime() {
        return executionRuntime;
    }

    public void setExecutionRuntime(ExecutionRuntime executionRuntime) {
        this.executionRuntime = executionRuntime;
    }

    public List<Long> getProcessIdList() {
        return processIdList;
    }

    public void setProcessIdList(List<Long> processIdList) {
        this.processIdList = processIdList;
    }

    public ExecutionTrace getExecutionTrace() {
        return executionTrace;
    }

    public void setExecutionTrace(ExecutionTrace executionTrace) {
        this.executionTrace = executionTrace;
    }

    public ScriptLog getScriptLog() {
        return scriptLog;
    }

    public void setScriptLog(ScriptLog scriptLog) {
        this.scriptLog = scriptLog;
    }

    public ExecutionLog getExecutionLog() {
        return executionLog;
    }

    public void setExecutionLog(ExecutionLog executionLog) {
        this.executionLog = executionLog;
    }

    public boolean isScriptExit() {
        return scriptExit;
    }

    public void setScriptExit(boolean scriptExit) {
        this.scriptExit = scriptExit;
    }
}