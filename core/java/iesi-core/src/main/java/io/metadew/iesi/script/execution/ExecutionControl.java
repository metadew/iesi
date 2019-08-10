package io.metadew.iesi.script.execution;

import io.metadew.iesi.common.text.TextTools;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.configuration.FrameworkStatus;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.IESIMessage;
import io.metadew.iesi.metadata.backup.BackupExecution;
import io.metadew.iesi.metadata.definition.script.ScriptLog;
import io.metadew.iesi.metadata.execution.MetadataControl;
import io.metadew.iesi.metadata.restore.RestoreExecution;
import io.metadew.iesi.metadata.service.script.ScriptDesignTraceService;
import org.apache.logging.log4j.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExecutionControl {

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
        this.scriptDesignTraceService = new ScriptDesignTraceService();
        this.executionLog = new ExecutionLog();
        this.executionTrace = new ExecutionTrace();
        setRunId(FrameworkExecution.getInstance().getFrameworkRuntime().getRunId());
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
        this.getExecutionRuntime().setRuntimeVariablesFromList(actionExecution, MetadataControl.getInstance()
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
        Long parentProcessId = scriptExecution.getParentScriptExecution().map(ScriptExecution::getProcessId).orElse(0L);

        // TODO:

        // Insert into result area
        String query = "INSERT INTO "
                + MetadataControl.getInstance().getResultMetadataRepository().getTableNameByLabel("ScriptResults")
                + " (RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, ST_NM, STRT_TMS, END_TMS) VALUES ("
                + SQLTools.GetStringForSQL(this.runId) + ","
                + SQLTools.GetStringForSQL(scriptExecution.getProcessId()) + ","
                + SQLTools.GetStringForSQL(parentProcessId) + ","
                + SQLTools.GetStringForSQL(scriptExecution.getScript().getId()) + ","
                + SQLTools.GetStringForSQL(scriptExecution.getScript().getName()) + ","
                + SQLTools.GetStringForSQL(scriptExecution.getScript().getVersion().getNumber()) + ","
                + SQLTools.GetStringForSQL(this.envName) + ","
                + SQLTools.GetStringForSQL("ACTIVE") + ","
                + SQLTools.GetStringForSQL(LocalDateTime.now()) + ","
                + "null);";

        MetadataControl.getInstance().getResultMetadataRepository().executeUpdate(query);

        this.scriptLog = new ScriptLog(runId, scriptExecution.getProcessId(), parentProcessId, scriptExecution.getScript().getId(),
                scriptExecution.getScript().getVersion().getNumber(), envName, "ACTIVE", LocalDateTime.now(), null);
        this.executionLog.setLog(scriptLog);

        // Trace the design of the script
        scriptDesignTraceService.trace(scriptExecution);
        // ScriptTraceConfiguration scriptTraceConfiguration = new ScriptTraceConfiguration(frameworkExecution.getFrameworkInstance());
        // MetadataControl.getInstance().getResultMetadataRepository().executeBatch(scriptTraceConfiguration.getInsertStatement(scriptExecution));
    }

    public void logStart(ActionExecution actionExecution) {
        String query = "INSERT INTO "
                + MetadataControl.getInstance().getResultMetadataRepository().getTableNameByLabel("ActionResults")
                + " (RUN_ID, PRC_ID, SCRIPT_PRC_ID, ACTION_ID, ACTION_NM, ENV_NM, ST_NM, STRT_TMS, END_TMS) VALUES ("
                + SQLTools.GetStringForSQL(this.getRunId()) + ","
                + SQLTools.GetStringForSQL(actionExecution.getProcessId()) + ","
                + SQLTools.GetStringForSQL(actionExecution.getScriptExecution().getProcessId()) + ","
                + SQLTools.GetStringForSQL(actionExecution.getAction().getId()) + ","
                + SQLTools.GetStringForSQL(actionExecution.getAction().getName()) + ","
                + SQLTools.GetStringForSQL(this.getEnvName()) + ","
                + SQLTools.GetStringForSQL("ACTIVE") + ","
                + SQLTools.GetStringForSQL(LocalDateTime.now())
                + ",null);";

        MetadataControl.getInstance().getResultMetadataRepository().executeUpdate(query);
    }

    public void logSkip(ActionExecution actionExecution) {
        String query = "INSERT INTO "
                + MetadataControl.getInstance().getResultMetadataRepository().getTableNameByLabel("ActionResults")
                + " (RUN_ID, PRC_ID, ACTION_ID, ACTION_NM, ENV_NM, ST_NM, STRT_TMS, END_TMS) VALUES ("
                + SQLTools.GetStringForSQL(this.getRunId()) + ","
                + SQLTools.GetStringForSQL(actionExecution.getProcessId()) + ","
                + SQLTools.GetStringForSQL(actionExecution.getAction().getId()) + ","
                + SQLTools.GetStringForSQL(actionExecution.getAction().getName()) + ","
                + SQLTools.GetStringForSQL(this.getEnvName()) + ","
                + SQLTools.GetStringForSQL("SKIPPED") + ","
                + MetadataControl.getInstance().getResultMetadataRepository().getRepository().getDatabases().values().stream().findFirst().get().getSystemTimestampExpression() + ","
                + MetadataControl.getInstance().getResultMetadataRepository().getRepository().getDatabases().values().stream().findFirst().get().getSystemTimestampExpression() + ");";

        MetadataControl.getInstance().getResultMetadataRepository().executeUpdate(query);

        String status = FrameworkStatus.SKIPPED.value();

        this.logMessage(actionExecution, "action.status=" + status, Level.INFO);
    }

    public void logStart(BackupExecution backupExecution) {
        setRunId(FrameworkExecution.getInstance().getFrameworkRuntime().getRunId());
    }

    public void logStart(RestoreExecution restoreExecution) {
        setRunId(FrameworkExecution.getInstance().getFrameworkRuntime().getRunId());
    }

    public Long getNewProcessId() {
        Long processId = FrameworkExecution.getInstance().getFrameworkRuntime().getNextProcessId();
        logMessage(new IESIMessage("exec.processid=" + processId), Level.TRACE);
        return processId;
    }

    public String logEnd(ScriptExecution scriptExecution) {
        String status = getStatus(scriptExecution);
        String query = "update "
                + MetadataControl.getInstance().getResultMetadataRepository().getTableNameByLabel("ScriptResults")
                + " set ST_NM = '" + status + "', END_TMS = "
                + SQLTools.GetStringForSQL(LocalDateTime.now())
                + " where RUN_ID = '" + this.getRunId() + "' and PRC_ID = " + scriptExecution.getProcessId() + ";";

        MetadataControl.getInstance().getResultMetadataRepository().executeUpdate(query);

        // Clear processing variables
        // Only is the script is a root script, this will be cleaned
        // In other scripts, the processing variables are still valid

        this.getScriptLog().setEnd(LocalDateTime.now());
        this.getScriptLog().setStatus(status);
        this.getExecutionLog().setLog(this.getScriptLog());

        // return
        return status;
    }

    public void logEnd(ActionExecution actionExecution, ScriptExecution scriptExecution) {
        String status = this.getStatus(actionExecution, scriptExecution);
        String query = "update "
                + MetadataControl.getInstance().getResultMetadataRepository().getTableNameByLabel("ActionResults")
                + " set ST_NM = '" + status + "', END_TMS = "
                + MetadataControl.getInstance().getResultMetadataRepository().getRepository().getDatabases().values().stream().findFirst().get().getSystemTimestampExpression()
                + " where RUN_ID = '" + this.getRunId() + "' and PRC_ID = " + actionExecution.getProcessId() + ";";

        MetadataControl.getInstance().getResultMetadataRepository().executeUpdate(query);
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

        String query = "INSERT INTO "
                + MetadataControl.getInstance().getResultMetadataRepository().getTableNameByLabel("ScriptResultOutputs")
                + " (RUN_ID, PRC_ID, SCRIPT_ID, OUT_NM, OUT_VAL) VALUES ("
                + SQLTools.GetStringForSQL(runId) + ","
                + SQLTools.GetStringForSQL(scriptExecution.getProcessId()) + ","
                + SQLTools.GetStringForSQL(scriptExecution.getScript().getId()) + ","
                + SQLTools.GetStringForSQL(outputName) + ","
                + SQLTools.GetStringForSQL(outputValue) + ");";

        MetadataControl.getInstance().getResultMetadataRepository().executeUpdate(query);
    }

    public void logExecutionOutput(ActionExecution actionExecution, String outputName, int outputValue) {
        logExecutionOutput(actionExecution, outputName, Integer.toString(outputValue));
    }

    public void logExecutionOutput(ActionExecution actionExecution, String outputName, long outputValue) {
        logExecutionOutput(actionExecution, outputName, Long.toString(outputValue));
    }

    public void logExecutionOutput(ActionExecution actionExecution, String outputName, String outputValue) {
        // Redact any encrypted values
        outputValue = FrameworkCrypto.getInstance().redact(outputValue);
        outputValue = TextTools.shortenTextForDatabase(outputValue, 2000);

        String query = "INSERT INTO "
                + MetadataControl.getInstance().getResultMetadataRepository().getTableNameByLabel("ActionResultOutputs")
                + " (RUN_ID, PRC_ID, ACTION_ID, OUT_NM, OUT_VAL) VALUES ("
                + SQLTools.GetStringForSQL(this.getRunId()) + ","
                + SQLTools.GetStringForSQL(actionExecution.getProcessId()) + ","
                + SQLTools.GetStringForSQL(actionExecution.getAction().getId()) + ","
                + SQLTools.GetStringForSQL(outputName) + ","
                + SQLTools.GetStringForSQL(outputValue) + ");";

        MetadataControl.getInstance().getResultMetadataRepository().executeUpdate(query);
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