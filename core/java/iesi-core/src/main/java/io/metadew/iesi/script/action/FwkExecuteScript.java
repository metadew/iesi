package io.metadew.iesi.script.action;

import io.metadew.iesi.datatypes.Array;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeResolver;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.configuration.FrameworkStatus;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.Level;

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

    private ActionExecution actionExecution;

    private ScriptExecution scriptExecution;

    private FrameworkExecution frameworkExecution;

    private ExecutionControl executionControl;
    private final Pattern keyValuePattern = Pattern.compile("\\s*(?<parameter>.+)\\s*=\\s*(?<value>.+)\\s*");
    // Parameters
    private ActionParameterOperation scriptName;

    private ActionParameterOperation scriptVersion;

    private ActionParameterOperation environmentName;

    private ActionParameterOperation paramList;

    private ActionParameterOperation paramFile;

    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public FwkExecuteScript() {

    }

    public FwkExecuteScript(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution,
                            ActionExecution actionExecution) {
        this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
    }

    public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ScriptExecution scriptExecution,
                     ActionExecution actionExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setScriptExecution(scriptExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {
        // Reset Parameters
        this.setScriptName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "script"));
        this.setScriptVersion(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "version"));
        this.setEnvironmentName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "environment"));
        this.setParamList(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "paramList"));
        this.setParamFile(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
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
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for parameterList",
                    parameterList.getClass()), Level.WARN);
            return Optional.empty();
        }
    }

    private boolean executeScript(String scriptName, Optional<Long> scriptVersion, Optional<String> environmentName, Optional<String> parameterList, Optional<String> parameterFileName) {
        // Check on Running a script in a loop
        if (this.getScriptExecution().getScript().getName().equalsIgnoreCase(scriptName)) {
            throw new RuntimeException("Not allowed to run the script recursively");
        }

        try {
            ScriptConfiguration scriptConfiguration = new ScriptConfiguration(this.getFrameworkExecution().getFrameworkInstance());
            // Script script = scriptConfiguration.getScript(this.getScriptName().getValue());
            Script script = scriptVersion
                    .map(version -> scriptConfiguration.getScript(scriptName, version))
                    .orElse(scriptConfiguration.getScript(scriptName)).get();

            ScriptExecution scriptExecution = new ScriptExecution(this.getFrameworkExecution(), script);
            scriptExecution.initializeAsNonRootExecution(this.getExecutionControl(), this.getScriptExecution());

            parameterFileName.ifPresent(scriptExecution::setParamFile);
            // TODO: do it nicer
            parameterList.ifPresent(scriptExecution::setParamList);
            scriptExecution.execute();

            if (scriptExecution.getResult().equalsIgnoreCase(FrameworkStatus.SUCCESS.value())) {
                this.getActionExecution().getActionControl().increaseSuccessCount();
            } else if (scriptExecution.getResult()
                    .equalsIgnoreCase(FrameworkStatus.WARNING.value())) {
                this.getActionExecution().getActionControl().increaseSuccessCount();
            } else if (scriptExecution.getResult()
                    .equalsIgnoreCase(FrameworkStatus.ERROR.value())) {
                this.getActionExecution().getActionControl().increaseErrorCount();
            } else {
                this.getActionExecution().getActionControl().increaseErrorCount();
            }

        } catch (Exception e) {
            throw new RuntimeException("Issue setting runtime variables: " + e, e);
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
                this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " parameter entry ''{0}'' does not follow correct syntax",
                        parameterEntry), Level.WARN);
            }
            return parameterMap;
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for parameter entry",
                    parameterEntry.getClass()), Level.WARN);
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
                    .forEach(parameterEntry -> parameterMap.putAll(convertParameterEntry(DataTypeResolver.resolveToDataType(parameterEntry, frameworkExecution.getFrameworkConfiguration().getFolderConfiguration(), executionControl.getExecutionRuntime()))));
            return Optional.of(parameterMap);
        } else if (list instanceof Array) {
            for (DataType parameterEntry : ((Array) list).getList()) {
                parameterMap.putAll(convertParameterEntry(parameterEntry));
            }
            return Optional.of(parameterMap);
        } else {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("fwk.setParameterList does not accept {0} as type for list",
                    list.getClass()), Level.WARN);
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
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("fwk.executeScript does not accept {0} as type for script name",
                    scriptVersion.getClass()), Level.WARN);
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
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("fwk.executeScript does not accept {0} as type for parameter file name",
                    parameterFileName.getClass()), Level.WARN);
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
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("fwk.executeScript does not accept {0} as type for environment name",
                    environmentName.getClass()), Level.WARN);
            return Optional.of(environmentName.toString());
        }
    }

    private String convertScriptName(DataType scriptName) {
        if (scriptName instanceof Text) {
            return scriptName.toString();
        } else {
            frameworkExecution.getFrameworkLog().log(MessageFormat.format("fwk.executeScript does not accept {0} as type for script name",
                    scriptName.getClass()), Level.WARN);
            return scriptName.toString();
        }
    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

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