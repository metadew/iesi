package io.metadew.iesi.script.action;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeService;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.framework.configuration.FrameworkStatus;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.script.ScriptExecutionBuildException;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.execution.ScriptExecutionBuilder;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FwkExecuteScript {

    private final Pattern keyValuePattern = Pattern.compile("\\s*(?<parameter>.+)\\s*=\\s*(?<value>.+)\\s*");

    private ActionExecution actionExecution;
    private ScriptExecution scriptExecution;
    private ExecutionControl executionControl;
    // Parameters
    private ActionParameterOperation scriptName;
    private ActionParameterOperation scriptVersion;
    private ActionParameterOperation environmentName;
    private ActionParameterOperation paramList;
    private ActionParameterOperation paramFile;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private DataTypeService dataTypeService;
    private static final Logger LOGGER = LogManager.getLogger();

    public FwkExecuteScript(ExecutionControl executionControl, ScriptExecution scriptExecution,
                            ActionExecution actionExecution) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setScriptExecution(scriptExecution);
        this.setActionParameterOperationMap(new HashMap<>());
        this.dataTypeService = new DataTypeService(executionControl.getExecutionRuntime());
    }

    public void prepare() {
        // Reset Parameters
        this.setScriptName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "script"));
        this.setScriptVersion(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "version"));
        this.setEnvironmentName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "environment"));
        this.setParamList(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "paramList"));
        this.setParamFile(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "paramFile"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("script")) {
                this.getScriptName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("version")) {
                this.getScriptVersion().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("environment")) {
                this.getEnvironmentName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("paramlist")) {
                this.getParamList().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("paramfile")) {
                this.getParamFile().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("script", this.getScriptName());
        this.getActionParameterOperationMap().put("version", this.getScriptVersion());
        this.getActionParameterOperationMap().put("environment", this.getEnvironmentName());
        this.getActionParameterOperationMap().put("paramList", this.getParamList());
        this.getActionParameterOperationMap().put("paramFile", this.getParamFile());
    }

    public boolean execute() {
        try {
            String scriptName = convertScriptName(getScriptName().getValue());
            Optional<Long> scriptVersion = convertScriptVersion(getScriptVersion().getValue());
            Optional<String> environmentName = convertEnvironmentName(getEnvironmentName().getValue());
            // TODO: see setParameterList for nicer version
            Optional<String> parameterList = convertParameterList2(getParamList().getValue());
            Optional<String> parameterFileName = convertParameterFileName(getParamFile().getValue());
            return executeScript(scriptName, scriptVersion, environmentName, parameterList, parameterFileName);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }
    }

    private Optional<String> convertParameterList2(DataType parameterList) {
        if (parameterList == null) {
            return Optional.empty();
        }
        if (parameterList instanceof Text) {
            return Optional.of(parameterList.toString());
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for parameterList",
                    parameterList.getClass()));
            return Optional.empty();
        }
    }

    private boolean executeScript(String scriptName, Optional<Long> scriptVersion, Optional<String> environmentName, Optional<String> parameterList, Optional<String> parameterFileName) throws ScriptExecutionBuildException {
        // Check on Running a script in a loop
        if (this.getScriptExecution().getScript().getName().equalsIgnoreCase(scriptName)) {
            throw new RuntimeException("Not allowed to run the script recursively");
        }

        ScriptConfiguration scriptConfiguration = new ScriptConfiguration();
        // Script script = scriptConfiguration.get(this.getScriptName().getValue());
        Script script = scriptVersion
                .map(version -> scriptConfiguration.get(scriptName, version)
                        .orElseThrow(() -> new RuntimeException(MessageFormat.format("No implementation for script {0}-{1} found", scriptName, version))))
                .orElse(scriptConfiguration.get(scriptName)
                        .orElseThrow(() -> new RuntimeException(MessageFormat.format("No implementation for script {0} found", scriptName))));

        ScriptExecution subScriptScriptExecution = new ScriptExecutionBuilder(false, false)
                .script(script)
                .executionControl(executionControl)
                .parentScriptExecution(this.scriptExecution)
                .exitOnCompletion(false)
                .paramFile(parameterFileName.orElse(""))
                .paramList(parameterList.orElse(""))
                .build();

        subScriptScriptExecution.execute();

        if (subScriptScriptExecution.getResult().equalsIgnoreCase(FrameworkStatus.SUCCESS.value())) {
            this.getActionExecution().getActionControl().increaseSuccessCount();
        } else if (subScriptScriptExecution.getResult()
                .equalsIgnoreCase(FrameworkStatus.WARNING.value())) {
            this.getActionExecution().getActionControl().increaseWarningCount();
        } else if (subScriptScriptExecution.getResult()
                .equalsIgnoreCase(FrameworkStatus.ERROR.value())) {
            this.getActionExecution().getActionControl().increaseErrorCount();
        } else {
            this.getActionExecution().getActionControl().increaseErrorCount();
        }

        return true;
    }

    private Map<String, String> convertParameterEntry(DataType parameterEntry) {
        Map<String, String> parameterMap = new HashMap<>();
        if (parameterEntry instanceof Text) {
            Matcher matcher = keyValuePattern.matcher(parameterEntry.toString());
            if (matcher.find()) {
                parameterMap.put(matcher.group("parameter"), matcher.group("value"));
            } else {
                LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " parameter entry ''{0}'' does not follow correct syntax",
                        parameterEntry));
            }
            return parameterMap;
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for parameter entry",
                    parameterEntry.getClass()));
            return parameterMap;
        }
    }

    @SuppressWarnings("unused")
    private Optional<Map<String, String>> convertParameterList(DataType list) {
        if (list == null) {
            return Optional.empty();
        }
        Map<String, String> parameterMap = new HashMap<>();
        if (list instanceof Text) {
            Arrays.stream(list.toString().split(","))
                    .forEach(parameterEntry -> parameterMap.putAll(convertParameterEntry(dataTypeService.resolve(parameterEntry))));
            return Optional.of(parameterMap);
        } else if (list instanceof Array) {
            for (DataType parameterEntry : ((Array) list).getList()) {
                parameterMap.putAll(convertParameterEntry(parameterEntry));
            }
            return Optional.of(parameterMap);
        } else {
            LOGGER.warn(MessageFormat.format("fwk.setParameterList does not accept {0} as type for list",
                    list.getClass()));
            return Optional.empty();
        }
    }

    private Optional<Long> convertScriptVersion(DataType scriptVersion) {
        if (scriptVersion == null) {
            return Optional.empty();
        }
        if (scriptVersion instanceof Text) {
            return Optional.of(Long.parseLong(scriptVersion.toString()));
        } else {
            LOGGER.warn(MessageFormat.format("fwk.executeScript does not accept {0} as type for script name",
                    scriptVersion.getClass()));
            return Optional.empty();
        }
    }

    private Optional<String> convertParameterFileName(DataType parameterFileName) {
        if (parameterFileName == null) {
            return Optional.empty();
        }
        if (parameterFileName instanceof Text) {
            return Optional.of(parameterFileName.toString());
        } else {
            LOGGER.warn(MessageFormat.format("fwk.executeScript does not accept {0} as type for parameter file name",
                    parameterFileName.getClass()));
            return Optional.empty();
        }
    }

    private Optional<String> convertEnvironmentName(DataType environmentName) {
        if (environmentName == null) {
            return Optional.empty();
        }
        // TODO: if null get current Environment, here or in execute(...)
        if (environmentName instanceof Text) {
            return Optional.of(environmentName.toString());
        } else {
            LOGGER.warn(MessageFormat.format("fwk.executeScript does not accept {0} as type for environment name",
                    environmentName.getClass()));
            return Optional.of(environmentName.toString());
        }
    }

    private String convertScriptName(DataType scriptName) {
        if (scriptName instanceof Text) {
            return scriptName.toString();
        } else {
            LOGGER.warn(MessageFormat.format("fwk.executeScript does not accept {0} as type for script name",
                    scriptName.getClass()));
            return scriptName.toString();
        }
    }

    // Getters and Setters
    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

    public ScriptExecution getScriptExecution() {
        return scriptExecution;
    }

    public void setScriptExecution(ScriptExecution scriptExecution) {
        this.scriptExecution = scriptExecution;
    }

    public ActionParameterOperation getScriptName() {
        return scriptName;
    }

    public void setScriptName(ActionParameterOperation scriptName) {
        this.scriptName = scriptName;
    }

    public ActionParameterOperation getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(ActionParameterOperation environmentName) {
        this.environmentName = environmentName;
    }

    public ActionParameterOperation getParamList() {
        return paramList;
    }

    public void setParamList(ActionParameterOperation paramList) {
        this.paramList = paramList;
    }

    public ActionParameterOperation getParamFile() {
        return paramFile;
    }

    public void setParamFile(ActionParameterOperation paramFile) {
        this.paramFile = paramFile;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

    public ActionParameterOperation getScriptVersion() {
        return scriptVersion;
    }

    public void setScriptVersion(ActionParameterOperation scriptVersion) {
        this.scriptVersion = scriptVersion;
    }
}